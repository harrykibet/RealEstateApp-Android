# Define remotes and branch
$repos = ("gitlab", "github")
$branch = "main"

# Function to update a repository
function Update-Repo {
    param ([string]$remote)

    Write-Host "Updating repository: $remote" -ForegroundColor Cyan

    # Pull latest changes with rebase
    git pull --rebase $remote $branch

    # Push updates
    git push $remote $branch

    Write-Host "âœ… Successfully updated $remote" -ForegroundColor Green
    Write-Host "---------------------------------"
}

# Loop through remotes and update each
foreach ($remote in $repos) {
    Update-Repo -remote $remote
}
