import pytest
import time
from unittest.mock import patch, AsyncMock
from httpx import AsyncClient, ASGITransport
from main import app
from schemas import AppInfoResponse


@pytest.mark.asyncio
async def test_cache_works():
    with patch("routers.app_info.enrich_with_ai", new_callable=AsyncMock) as mock_enrich:
        mock_enrich.return_value = AppInfoResponse(
            package_name="com.test.cache",
            purpose="Test app",
            key_features=["Feature 1"],
            alternatives=["Alt 1"],
            verdict="KEEP",
            verdict_reason="Test reason",
            source="gemini"
        )
        
        transport = ASGITransport(app=app)
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            # First call - should call enrich_with_ai
            start1 = time.time()
            response1 = await client.post(
                "/api/v1/app-info",
                json={"package_name": "com.test.cache", "app_name": "Test Cache"}
            )
            elapsed1 = time.time() - start1
            
            assert response1.status_code == 200
            data1 = response1.json()
            assert data1["package_name"] == "com.test.cache"
            assert mock_enrich.call_count == 1
            
            # Second call - should be served from cache
            start2 = time.time()
            response2 = await client.post(
                "/api/v1/app-info",
                json={"package_name": "com.test.cache", "app_name": "Test Cache"}
            )
            elapsed2 = time.time() - start2
            
            assert response2.status_code == 200
            data2 = response2.json()
            assert data2["package_name"] == "com.test.cache"
            
            # enrich_with_ai should still be called only once
            assert mock_enrich.call_count == 1, "enrich_with_ai should not be called on cache hit"
            
            # Second call should be fast (under 50ms)
            assert elapsed2 < 0.05, f"Second call took {elapsed2:.3f}s, expected under 0.05s"