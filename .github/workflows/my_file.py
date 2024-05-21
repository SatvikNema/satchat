import os

for key in os.environ.keys():
    print(f'{key} -> {os.environ[key]}')