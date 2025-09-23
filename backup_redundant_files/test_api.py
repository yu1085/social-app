import requests
import json

# Test the send-code endpoint
url = "http://localhost:8080/api/auth/send-code"
data = {"phone": "1380013800"}

try:
    response = requests.post(url, data=data)
    print(f"Status Code: {response.status_code}")
    print(f"Response Headers: {dict(response.headers)}")
    print(f"Response Body: {response.text}")
    
    if response.status_code == 200:
        result = response.json()
        print(f"Success: {result}")
    else:
        print(f"Error: {response.text}")
        
except Exception as e:
    print(f"Exception: {e}")
