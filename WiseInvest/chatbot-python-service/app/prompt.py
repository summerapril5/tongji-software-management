def build_prompt(context, user_input):
    messages = [
        {"role": "system", "content": "你是一个专业的基金理财助手，擅长解答基金产品、市场走势、风险提示等问题。"},
    ]
    messages.extend(context)
    messages.append({"role": "user", "content": user_input})
    return messages
