import patch_parser
from code_analyser import analyse
from env import base_branch, source_branch
from github_client import gh_pull_request, gh_repository, gh_client_instance

file_change_metadata = []
for file in gh_pull_request.get_files():
    filename = file.filename
    lines_changed = patch_parser.get_lines_changed(file.patch)

    old_contents = gh_repository.get_contents(filename, ref=base_branch)
    new_contents = gh_repository.get_contents(filename, ref=source_branch)

    file_change_metadata.append({
        'filename': filename,
        'old_content': old_contents,
        'old_lines': list(map(lambda e: e[0], lines_changed)),
        'new_content': new_contents,
        'new_lines': list(map(lambda e: e[1], lines_changed))
    })


analyse(file_change_metadata)
gh_client_instance.close()
