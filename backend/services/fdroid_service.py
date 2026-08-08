import httpx


async def fetch_from_fdroid(package_name: str) -> dict | None:
    url = f"https://f-droid.org/api/v1/packages/{package_name}"
    try:
        async with httpx.AsyncClient(timeout=15) as client:
            resp = await client.get(url)
            if resp.status_code != 200:
                return None
            data = resp.json()
            return {
                "summary": data.get("summary"),
                "description": data.get("description"),
            }
    except Exception:
        return None
