from fastapi import APIRouter

from schemas import AppInfoRequest, AppInfoResponse
from services.cache_service import get_cached, set_cached
from services.fdroid_service import fetch_from_fdroid
from services.gemini_service import enrich_with_ai
from services.play_store_service import fetch_from_play_store

router = APIRouter()


@router.post("/app-info", response_model=AppInfoResponse)
async def app_info(request: AppInfoRequest) -> AppInfoResponse:
    cached = await get_cached(request.package_name)
    if cached is not None:
        return cached

    raw = await fetch_from_play_store(request.package_name)
    if raw is None:
        raw = await fetch_from_fdroid(request.package_name)
    description = None
    if raw is not None:
        description = raw.get("description") or raw.get("summary")

    response = await enrich_with_ai(request.package_name, request.app_name, description)
    await set_cached(request.package_name, response)
    return response
