param(
    [switch]$DryRun
)

$ErrorActionPreference = "Stop"

# ---------------- CONFIG ----------------
$repos = @("gitlab", "github")
$branch = "main"
$MAX_SIZE = 50MB
$BLOCKED_EXTENSIONS = @("apk", "aab", "hprof")
$BLOCKED_DIRECTORIES = @("build\")
# ----------------------------------------

# -------- Utility Functions --------

function Ensure-CleanState {
    if (Test-Path ".git\rebase-merge" -or Test-Path ".git\rebase-apply") {
        Write-Host "❌ Rebase in progress. Resolve first." -ForegroundColor Red
        exit 1
    }
}

function Show-CommitSummary {
    Write-Host "`n📋 Commit Summary:" -ForegroundColor Cyan
    git diff --cached --stat
    Write-Host ""
}

function Test-IsBinary {
    param($FilePath)
    try {
        $bytes = [System.IO.File]::ReadAllBytes($FilePath)
        return ($bytes -contains 0)
    } catch {
        return $false
    }
}

function Check-BlockedFiles {
    $files = git diff --cached --name-only

    foreach ($file in $files) {

        if (-not (Test-Path $file)) {
            continue
        }

        # 🚫 Block build folders
        foreach ($dir in $BLOCKED_DIRECTORIES) {
            if ($file -like "*$dir*") {
                Write-Host "❌ Refusing to commit build directory file: $file" -ForegroundColor Red
                exit 1
            }
        }

        $extension = [System.IO.Path]::GetExtension($file).TrimStart('.')

        # 🚫 Hard block extensions
        if ($BLOCKED_EXTENSIONS -contains $extension) {
            Write-Host "❌ Blocked file type: .$extension ($file)" -ForegroundColor Red
            Write-Host "Add it to .gitignore or use Git LFS if intentional." -ForegroundColor Yellow
            exit 1
        }

        # 📏 Check large files
        $fileInfo = Get-Item $file
        if ($fileInfo.Length -gt $MAX_SIZE) {

            $sizeMB = [math]::Round($fileInfo.Length / 1MB, 2)
            Write-Host "⚠️  Large file detected: $file ($sizeMB MB)" -ForegroundColor Red

            if (Test-IsBinary $file) {
                Write-Host "💡 This appears to be a binary file." -ForegroundColor Yellow
                Write-Host "Consider using Git LFS:"
                Write-Host "   git lfs track `"$file`""
                Write-Host "   git add .gitattributes"
                Write-Host "   git add `"$file`""
                Write-Host "   git commit -m `"chore: move $file to LFS`""
            }

            $resp = Read-Host "Commit anyway? (y/N)"
            if ($resp -notmatch "^[Yy]$") {
                Write-Host "❌ Commit aborted." -ForegroundColor Red
                exit 1
            }
        }
    }
}


function Update-Repo {
    param($remote)

    Write-Host "`n🚀 Syncing: $remote" -ForegroundColor Cyan

    Ensure-CleanState

    Write-Host "→ Pulling (rebase)..." -ForegroundColor DarkGray
    git pull --rebase $remote $branch

    Write-Host "→ Staging changes..." -ForegroundColor DarkGray
    git add -A

    $status = git status --porcelain
    if ([string]::IsNullOrWhiteSpace($status)) {
        Write-Host "No changes detected." -ForegroundColor DarkGray
        return
    }

    Check-BlockedFiles

    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $commitMessage = "chore(sync): auto update $timestamp"

    Show-CommitSummary

    if ($DryRun) {
        Write-Host "🧪 DRY RUN MODE — No commit or push performed." -ForegroundColor Yellow
        return
    }

    git commit -m $commitMessage
    Write-Host "✔ Committed." -ForegroundColor Yellow

    Write-Host "→ Pushing to $remote..." -ForegroundColor DarkGray
    git push $remote $branch

    Write-Host "✅ Synced $remote successfully." -ForegroundColor Green
    Write-Host "---------------------------------------------"
}

# -------- Execution --------

git rev-parse --is-inside-work-tree *> $null
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Not inside a Git repository." -ForegroundColor Red
    exit 1
}

foreach ($remote in $repos) {
    Update-Repo -remote $remote
}

Write-Host "`n🎯 All remotes processed." -ForegroundColor Green
