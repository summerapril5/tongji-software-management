from fastapi import APIRouter
from app.schema import ChatRequest, ChatResponse
from app.service import handle_user_message

chat_router = APIRouter()

@chat_router.post("/chat", response_model=ChatResponse)
async def chat(req: ChatRequest):
    reply = await handle_user_message(req.user_id, req.message)
    return ChatResponse(reply=reply)
