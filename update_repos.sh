#!/bin/bash

set -e

# ---------------- CONFIG ----------------
repos=("gitlab" "github")
branch="main"
MAX_SIZE=$((50 * 1024 * 1024)) # 50MB
BLOCKED_EXTENSIONS=("apk" "aab" "hprof" "log")
BLOCKED_DIRECTORIES=("build/")
DRY_RUN=false
# ----------------------------------------

# -------- Parse Arguments --------
for arg in "$@"; do
    case $arg in
        --dry-run)
        DRY_RUN=true
        shift
        ;;
    esac
done

# -------- Colors --------
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
CYAN='\033[0;36m'
GRAY='\033[0;90m'
NC='\033[0m'

# -------- Utility Functions --------

is_binary() {
    file "$1" | grep -q "binary"
}

check_blocked_files() {
    FILES=$(git diff --cached --name-only)

    for FILE in $FILES; do
      # Skip if the file is deleted
              if [ ! -f "$FILE" ]; then
                  continue
              fi

        # Block build folders
        for DIR in "${BLOCKED_DIRECTORIES[@]}"; do
            if [[ "$FILE" == *"$DIR"* ]]; then
                echo -e "${RED}❌ Refusing to commit build directory file: $FILE${NC}"
                exit 1
            fi
        done

        # Block certain extensions completely
        for EXT in "${BLOCKED_EXTENSIONS[@]}"; do
            if [[ "$FILE" == *".$EXT" ]]; then
                echo -e "${RED}❌ Blocked file type: .$EXT ($FILE)${NC}"
                echo -e "${YELLOW}Add it to .gitignore or use Git LFS if intentional.${NC}"
                exit 1
            fi
        done

        # Check large files
        if [ -f "$FILE" ]; then
            FILE_SIZE=$(stat -c%s "$FILE" 2>/dev/null || echo 0)

            if [ "$FILE_SIZE" -gt "$MAX_SIZE" ]; then
                SIZE_MB=$(($FILE_SIZE / 1024 / 1024))
                echo -e "${RED}⚠️ Large file detected: $FILE (${SIZE_MB}MB)${NC}"

                if is_binary "$FILE"; then
                    echo -e "${YELLOW}💡 This appears to be a binary file."
                    echo "Consider using Git LFS:"
                    echo "   git lfs track \"$FILE\""
                    echo "   git add .gitattributes"
                    echo "   git add \"$FILE\""
                    echo "   git commit -m \"chore: move $FILE to LFS\""
                fi

                read -p "Commit anyway? (y/N): " RESP
                if [[ ! "$RESP" =~ ^[Yy]$ ]]; then
                    echo -e "${RED}❌ Commit aborted.${NC}"
                    exit 1
                fi
            fi
        fi
    done
}

ensure_clean_state() {
    if [ -d ".git/rebase-merge" ] || [ -d ".git/rebase-apply" ]; then
        echo -e "${RED}❌ Rebase in progress. Resolve first.${NC}"
        exit 1
    fi
}

show_commit_summary() {
    echo -e "${CYAN}📋 Commit Summary:${NC}"
    git diff --cached --stat
    echo ""
}

update_repo() {
    local remote="$1"

    echo -e "${CYAN}🚀 Syncing: $remote${NC}"

    ensure_clean_state

    echo -e "${GRAY}→ Staging changes...${NC}"
    git add --all

    if [[ -n "$(git status --porcelain)" ]]; then

        check_blocked_files

        timestamp="$(date +"%Y-%m-%d %H:%M:%S")"
        commit_message="chore(sync): auto update $timestamp"

        show_commit_summary

        if [ "$DRY_RUN" = true ]; then
            echo -e "${YELLOW}🧪 DRY RUN MODE — No commit performed.${NC}"
        else
            git commit -m "$commit_message"
            echo -e "${YELLOW}✔ Committed.${NC}"
        fi
    else
        echo -e "${GRAY}No local changes to commit.${NC}"
    fi

    echo -e "${GRAY}→ Pulling (rebase)...${NC}"
    git pull --rebase "$remote" "$branch"

    if [ "$DRY_RUN" = true ]; then
        echo -e "${YELLOW}🧪 DRY RUN MODE — No push performed.${NC}"
        return
    fi

    echo -e "${GRAY}→ Pushing to $remote...${NC}"
    git push "$remote" "$branch"

    echo -e "${GREEN}✅ Synced $remote successfully.${NC}"
    echo "---------------------------------------------"
}

# -------- Execution --------

if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
    echo -e "${RED}❌ Not inside a Git repository.${NC}"
    exit 1
fi

for remote in "${repos[@]}"; do
    update_repo "$remote"
done

echo -e "${GREEN}🎯 All remotes processed.${NC}"
