import requests
import os
from app.config import get_api_key

SILICONFLOW_API_URL = "https://api.siliconflow.cn/v1/chat/completions"
SILICONFLOW_API_KEY = get_api_key()

def call_qwen_model(messages):

    payload = {
        "model": "Qwen/QwQ-32B",
        "messages": messages,
        "stream": False,
        "max_tokens": 512,
        "min_p": 0.05,
        "stop": None,
        "temperature": 0.7,
        "top_p": 0.7,
        "top_k": 50,
        "frequency_penalty": 0.5,
        "n": 1,
        "response_format": {"type": "text"},
        "tools": [
            {
                "type": "function",
                "function": {
                    "description": "",
                    "name": "",
                    "parameters": {},
                    "strict": False
                }
            }
        ]
    }

    headers = {
        "Authorization": f"Bearer {SILICONFLOW_API_KEY}",
        "Content-Type": "application/json"
    }

    response = requests.post(SILICONFLOW_API_URL, json=payload, headers=headers)

    if response.status_code == 200:
        data = response.json()
        return data['choices'][0]['message']['content']
    else:
        raise Exception(f"调用 Qwen 模型失败,读取的API KEY:{SILICONFLOW_API_KEY},报错信息 : {response.status_code}, {response.text}")
