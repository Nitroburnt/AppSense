import pytest
from httpx import AsyncClient, ASGITransport
from main import app


@pytest.mark.asyncio
async def test_app_info_endpoint():
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        response = await client.post(
            "/api/v1/app-info",
            json={"package_name": "com.spotify.music", "app_name": "Spotify"}
        )
        assert response.status_code == 200
        data = response.json()
        
        assert "package_name" in data
        assert data["package_name"] == "com.spotify.music"
        assert "purpose" in data
        assert "key_features" in data
        assert isinstance(data["key_features"], list)
        assert "alternatives" in data
        assert isinstance(data["alternatives"], list)
        assert "verdict" in data
        assert data["verdict"] in ["KEEP", "UNINSTALL", "NEUTRAL"]
        assert "verdict_reason" in data
        assert "source" in data


@pytest.mark.asyncio
async def test_app_info_endpoint_missing_fields():
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        response = await client.post(
            "/api/v1/app-info",
            json={"package_name": "com.test"}
        )
        assert response.status_code == 422