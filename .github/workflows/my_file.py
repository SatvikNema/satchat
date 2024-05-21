import os
from github import Github
from github import Auth
from github.PullRequest import PullRequest

access_token: str = os.environ['GITHUB_TOKEN']
repo: str = os.environ['GITHUB_REPOSITORY']
pull_number: int = int(os.environ['PR_NUMBER'])
commit_hash: str = os.environ.get('GITHUB_SHA', "<HASH NOT AVAILABLE>")

auth = Auth.Token(access_token)
g = Github(auth=auth)

pull_request_metadata: PullRequest = g.get_repo(repo).get_pull(pull_number)
print('file changes:')
for file in pull_request_metadata.get_files():
    print(file)

comments_url = pull_request_metadata.comments_url
pull_request_metadata.create_issue_comment(f"<p>for <em>{commit_hash}</em></p> looks good!")

g.close()
