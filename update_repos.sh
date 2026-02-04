#!/bin/bash

# Define remotes and branch
repos=("gitlab" "github")
branch="main"

# Function to update a repository
update_repo() {
    local remote=$1

    echo -e "\033[0;36mUpdating repository: $remote\033[0m"

    # Pull latest changes with rebase
    git pull --rebase "$remote" "$branch"

    # Push updates
    git push "$remote" "$branch"

    echo -e "\033[0;32m Successfully updated $remote\033[0m"
    echo "---------------------------------"
}

# Loop through remotes and update each
for remote in "${repos[@]}"; do
    update_repo "$remote"
done

