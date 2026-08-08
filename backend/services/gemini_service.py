import asyncio
import json

import google.generativeai as genai

from config import settings
from schemas import AppInfoResponse

MODEL_NAME = "gemini-2.0-flash"
VALID_VERDICTS = ("KEEP", "UNINSTALL", "NEUTRAL")


def _google_search_tool():
    from google.generativeai import protos

    return protos.Tool(
        google_search_retrieval=protos.GoogleSearchRetrieval(
            dynamic_retrieval_config=protos.DynamicRetrievalConfig(
                mode=protos.DynamicRetrievalConfig.MODE_DYNAMIC,
                dynamic_threshold=0.6,
            )
        )
    )


def _build_prompt(package_name: str, app_name: str, raw_description: str | None) -> str:
    prompt = (
        "You are AppSense, an Android app analyzer. Analyze the app and return ONLY "
        "valid JSON (no markdown fences) matching exactly:\n"
        '{"package_name": "...", "purpose": "...", "key_features": ["..."], '
        '"alternatives": ["..."], "verdict": "KEEP|UNINSTALL|NEUTRAL", "verdict_reason": "..."}\n'
        f"App package name: {package_name}\n"
        f"App name: {app_name}\n"
    )
    if raw_description:
        prompt += f"\nOfficial description to base analysis on:\n{raw_description[:2000]}\n"
    else:
        prompt += "\nNo description available. Use your knowledge of this app.\n"
    return prompt


def _parse_response(text: str, package_name: str) -> AppInfoResponse | None:
    text = text.strip()
    if text.startswith("```"):
        text = text.split("\n", 1)[-1].rsplit("```", 1)[0].strip()
    try:
        data = json.loads(text)
    except Exception:
        return None
    verdict = str(data.get("verdict", "NEUTRAL")).upper()
    if verdict not in VALID_VERDICTS:
        verdict = "NEUTRAL"
    try:
        return AppInfoResponse(
            package_name=str(data.get("package_name", package_name)),
            purpose=str(data.get("purpose", "")),
            key_features=[str(f) for f in data.get("key_features", [])],
            alternatives=[str(a) for a in data.get("alternatives", [])],
            verdict=verdict,
            verdict_reason=str(data.get("verdict_reason", "")),
            source="gemini",
        )
    except Exception:
        return None


def _generate_json(prompt: str, use_grounding: bool) -> AppInfoResponse | None:
    genai.configure(api_key=settings.gemini_api_key)
    model = genai.GenerativeModel(MODEL_NAME)
    if use_grounding:
        try:
            resp = model.generate_content(prompt, tools=[_google_search_tool()])
        except Exception:
            resp = model.generate_content(prompt)
    else:
        resp = model.generate_content(prompt)
    return _parse_response(resp.text)


def _heuristic_response(package_name: str, app_name: str) -> AppInfoResponse:
    lower = app_name.lower()
    if package_name.startswith("com.google.") or package_name.startswith("com.android."):
        return AppInfoResponse(
            package_name=package_name,
            purpose=f"{app_name} is a Google or Android system component.",
            key_features=["System component"],
            alternatives=[],
            verdict="KEEP",
            verdict_reason="Preinstalled/system component; removing it may break other apps.",
            source="heuristic",
        )
    if any(word in lower for word in ("facebook", "instagram", "snapchat", "tiktok", "whatsapp")):
        return AppInfoResponse(
            package_name=package_name,
            purpose=f"{app_name} is a social media application.",
            key_features=["Social networking", "Messaging"],
            alternatives=["Use in a browser to limit screen time"],
            verdict="UNINSTALL",
            verdict_reason="High-attention social app; remove it if it is rarely used.",
            source="heuristic",
        )
    return AppInfoResponse(
        package_name=package_name,
        purpose=f"{app_name} is an application installed on this device.",
        key_features=["Runs locally on your device"],
        alternatives=[],
        verdict="NEUTRAL",
        verdict_reason="Not enough data to recommend removal; inspect manually.",
        source="heuristic",
    )


async def enrich_with_ai(
    package_name: str, app_name: str, raw_description: str | None
) -> AppInfoResponse:
    if not settings.gemini_api_key:
        return _heuristic_response(package_name, app_name)
    prompt = _build_prompt(package_name, app_name, raw_description)
    try:
        parsed = await asyncio.to_thread(_generate_json, prompt, raw_description is None)
        if parsed is not None:
            return parsed
    except Exception:
        pass
    return _heuristic_response(package_name, app_name)
