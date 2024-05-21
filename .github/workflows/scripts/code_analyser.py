from typing import Dict, List

from env import commit_hash
from github_client import gh_pull_request


def analyse(file_change_metadata: List[Dict]):
    add_issue_comment("lookinggg good!")


def generate_prompt():
    # todo
    pass


def send_prompt(prompt: str) -> str:
    # todo
    pass


def add_issue_comment(comment: str):
    gh_pull_request.create_issue_comment(f"<p>for <em>{commit_hash}</em></p> {comment}")
