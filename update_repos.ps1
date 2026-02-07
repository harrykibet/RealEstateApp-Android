# Define remotes and branch
$repos = @("gitlab", "github")
$branch = "main"

# Function to update a repository
function Update-Repo {
    param (
        [string]$remote
    )

    Write-Host "Updating repository: $remote" -ForegroundColor Cyan

    # Stage changes
    git add .

    # Commit only if there are changes
    if (git status --porcelain) {
        $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
        $commitMessage = "chore: auto sync $timestamp"

        git commit -m $commitMessage
        Write-Host "Committed changes: $commitMessage" -ForegroundColor Yellow
    } else {
        Write-Host "No local changes to commit." -ForegroundColor DarkGray
    }

    # Pull latest changes with rebase
    git pull --rebase $remote $branch

    # Push updates
    git push $remote $branch

    Write-Host "Successfully updated $remote" -ForegroundColor Green
    Write-Host "---------------------------------"
}

# Loop through remotes and update each
foreach ($remote in $repos) {
    Update-Repo -remote $remote
}
