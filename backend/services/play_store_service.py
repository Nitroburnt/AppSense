import asyncio

from google_play_scraper import app


async def fetch_from_play_store(package_name: str) -> dict | None:
    try:
        result = await asyncio.to_thread(app, package_name, lang="en", country="us")
        return {
            "description": result.get("description"),
            "category": result.get("genre"),
        }
    except Exception:
        return None
