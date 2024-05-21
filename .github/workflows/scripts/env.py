import os

access_token: str = os.environ['GITHUB_TOKEN']
repo: str = os.environ['GITHUB_REPOSITORY']
pull_number: int = int(os.environ['PR_NUMBER'])
commit_hash: str = os.environ.get('GITHUB_SHA', "<HASH NOT AVAILABLE>")
source_branch: str = os.environ['SOURCE_BRANCH']
target_branch: str = os.environ['TARGET_BRANCH']