#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试Swagger路径
"""

import requests

def test_swagger_paths():
    """测试各种Swagger路径"""
    paths = [
        "/swagger-ui.html",
        "/swagger-ui/",
        "/swagger-ui/index.html",
        "/api-docs",
        "/v3/api-docs",
        "/swagger-resources/configuration/ui",
        "/swagger-resources/configuration/security",
        "/swagger-resources",
        "/webjars/springfox-swagger-ui/css/swagger-ui.css",
        "/webjars/springfox-swagger-ui/lib/swagger-ui-bundle.js"
    ]
    
    for path in paths:
        try:
            response = requests.get(f"http://localhost:8080{path}", timeout=5)
            print(f"{path}: {response.status_code}")
        except Exception as e:
            print(f"{path}: ERROR - {e}")

if __name__ == "__main__":
    test_swagger_paths()
