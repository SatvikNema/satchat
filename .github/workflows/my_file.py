import os
from github import Github
from github import Auth

access_token = os.environ['GITHUB_TOKEN']
print(f'access token is {access_token}')
auth = Auth.Token(access_token)
g = Github(auth=auth)

for repo in g.get_user().get_repos():
    print(repo.name)

g.close()