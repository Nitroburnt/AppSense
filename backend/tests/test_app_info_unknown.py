import pytest
from httpx import AsyncClient, ASGITransport
from main import app


@pytest.mark.asyncio
async def test_app_info_unknown_package():
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        response = await client.post(
            "/api/v1/app-info",
            json={"package_name": "com.unknown.nonexistent.package.zzz", "app_name": "Unknown App"}
        )
        assert response.status_code == 200
        data = response.json()
        
        assert "package_name" in data
        assert data["package_name"] == "com.unknown.nonexistent.package.zzz"
        assert "purpose" in data
        assert "key_features" in data
        assert "alternatives" in data
        assert "verdict" in data
        assert data["verdict"] in ["KEEP", "UNINSTALL", "NEUTRAL"]
        assert "verdict_reason" in data
        assert "source" in data