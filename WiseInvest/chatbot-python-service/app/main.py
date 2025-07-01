from fastapi import FastAPI
from app.router import chat_router

app = FastAPI(title="基金智能机器人服务", version="1.0")
app.include_router(chat_router, prefix="/api")
