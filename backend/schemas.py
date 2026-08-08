from typing import List

from pydantic import BaseModel


class AppInfoRequest(BaseModel):
    package_name: str
    app_name: str


class AppInfoResponse(BaseModel):
    package_name: str
    purpose: str
    key_features: List[str]
    alternatives: List[str]
    verdict: str
    verdict_reason: str
    source: str
