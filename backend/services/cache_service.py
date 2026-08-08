from database import get_cached_response, set_cached_response
from schemas import AppInfoResponse


async def get_cached(package_name: str) -> AppInfoResponse | None:
    data = await get_cached_response(package_name)
    if data is None:
        return None
    try:
        return AppInfoResponse(**data)
    except Exception:
        return None


async def set_cached(package_name: str, response: AppInfoResponse) -> None:
    await set_cached_response(package_name, response.model_dump())
