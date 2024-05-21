import os
from github import Github
from github import Auth
from github.PullRequest import PullRequest

access_token: str = os.environ.get('GITHUB_TOKEN', 'PLACEHOLDER_TOKEN')
repo: str = os.environ.get('GITHUB_REPOSITORY', 'PLACEHOLDER_REPOSITORY')
pull_number: int = int(os.environ.get('PR_NUMBER', "-1"))
auth = Auth.Token(access_token)
g = Github(auth=auth)

pull_request_metadata: PullRequest = g.get_repo(repo).get_pull(pull_number)
print('file changes:')
for file in pull_request_metadata.get_files():
    print(file)

g.close()
