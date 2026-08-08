import os

from pydantic import BaseModel


class Settings(BaseModel):
    gemini_api_key: str | None = None
    openai_api_key: str | None = None
    database_url: str = "sqlite:///appsense.db"

    @classmethod
    def from_env(cls) -> "Settings":
        return cls(
            gemini_api_key=os.environ.get("GEMINI_API_KEY"),
            openai_api_key=os.environ.get("OPENAI_API_KEY"),
            database_url=os.environ.get("DATABASE_URL", "sqlite:///appsense.db"),
        )


settings = Settings.from_env()
