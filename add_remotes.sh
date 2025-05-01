#!/bin/bash

# Define remotes
declare -A remotes=(
  ["github"]="git@github.com:harrykibet/RealEstateApp-Android.git"
  ["gitlab"]="git@gitlab.com:harrykibet/RealEstateApp.git"
)

for name in "${!remotes[@]}"; do
  url="${remotes[$name]}"

  if git remote get-url "$name" &>/dev/null; then
    echo -e "\e[33m⚠️  Remote '$name' already exists. Skipping.\e[0m"
  else
    git remote add "$name" "$url"
    echo -e "\e[32m✅ Added remote '$name' with URL: $url\e[0m"
  fi
done
