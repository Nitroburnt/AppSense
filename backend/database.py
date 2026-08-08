import json
import time

import aiosqlite

DATABASE_PATH = "appsense.db"


async def init_db() -> None:
    async with aiosqlite.connect(DATABASE_PATH) as db:
        await db.execute(
            """
            CREATE TABLE IF NOT EXISTS app_cache (
                package_name TEXT PRIMARY KEY,
                response TEXT NOT NULL,
                cached_at INTEGER NOT NULL
            )
            """
        )
        await db.commit()


async def get_cached_response(package_name: str) -> dict | None:
    async with aiosqlite.connect(DATABASE_PATH) as db:
        db.row_factory = aiosqlite.Row
        cursor = await db.execute(
            "SELECT response FROM app_cache WHERE package_name = ?",
            (package_name,),
        )
        row = await cursor.fetchone()
    if row is None:
        return None
    return json.loads(row["response"])


async def set_cached_response(package_name: str, response: dict) -> None:
    async with aiosqlite.connect(DATABASE_PATH) as db:
        await db.execute(
            """
            INSERT INTO app_cache (package_name, response, cached_at)
            VALUES (?, ?, ?)
            ON CONFLICT(package_name) DO UPDATE SET
                response = excluded.response,
                cached_at = excluded.cached_at
            """,
            (package_name, json.dumps(response), int(time.time())),
        )
        await db.commit()
