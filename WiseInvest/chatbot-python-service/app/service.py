from app.memory import get_context, save_context
from app.prompt import build_prompt
from app.llm_client import call_qwen_model


async def handle_user_message(user_id: str, message: str) -> str:
    context = get_context(user_id)
    messages = build_prompt(context, message)
    reply = call_qwen_model(messages)
    save_context(user_id, message, reply)
    return reply
