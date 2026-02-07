#!/bin/bash

# Define remotes and branch
repos=("gitlab" "github")
branch="main"

# Function to update a repository
update_repo() {
    local remote="$1"

    echo -e "\033[0;36mUpdating repository: $remote\033[0m"

    # Stage changes
    git add .

    # Commit only if there are changes
    if [[ -n "$(git status --porcelain)" ]]; then
        timestamp="$(date +"%Y-%m-%d %H:%M:%S")"
        commit_message="chore: auto sync $timestamp"

        git commit -m "$commit_message"
        echo -e "\033[0;33mCommitted changes: $commit_message\033[0m"
    else
        echo -e "\033[0;90mNo local changes to commit.\033[0m"
    fi

    # Pull latest changes with rebase
    git pull --rebase "$remote" "$branch"

    # Push updates
    git push "$remote" "$branch"

    echo -e "\033[0;32mSuccessfully updated $remote\033[0m"
    echo "---------------------------------"
}

# Loop through remotes and update each
for remote in "${repos[@]}"; do
    update_repo "$remote"
done
