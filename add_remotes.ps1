# Define remotes
$remotes = @{
    "github" = "git@github.com:harrykibet/RealEstateApp-Android.git"
    "gitlab" = "git@gitlab.com:harrykibet/RealEstateApp.git"
}

foreach ($name in $remotes.Keys) {
    $url = $remotes[$name]

    # Check if remote already exists
    $exists = git remote | Where-Object { $_ -eq $name }

    if (-not $exists) {
        git remote add $name $url
        Write-Host "✅ Added remote '$name' with URL: $url" -ForegroundColor Green
    } else {
        Write-Host "⚠️  Remote '$name' already exists. Skipping." -ForegroundColor Yellow
    }
}
