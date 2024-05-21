
from github import Github
from github import Auth
from github.PullRequest import PullRequest
from github.Repository import Repository

from env import access_token, repo, pull_number

auth = Auth.Token(access_token)
gh_client_instance = Github(auth=auth)
gh_repository: Repository = gh_client_instance.get_repo(repo)
gh_pull_request: PullRequest = gh_repository.get_pull(pull_number)