memory_store = {}

def get_context(user_id: str):
    return memory_store.get(user_id, [])

def save_context(user_id: str, user_msg: str, bot_msg: str):
    history = memory_store.setdefault(user_id, [])
    history.append({"role": "user", "content": user_msg})
    history.append({"role": "assistant", "content": bot_msg})
