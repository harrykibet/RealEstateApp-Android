#!/usr/bin/env bash

set -euo pipefail

echo "========================================"
echo "CI DISPLAY BOOTSTRAP"
echo "========================================"

echo "Enabling HiDPI display modes..."
sudo defaults write /Library/Preferences/com.apple.windowserver DisplayResolutionEnabled -bool true || true

echo "Installing BetterDisplay..."

if ! command -v brew >/dev/null 2>&1; then
  echo "Homebrew not found"
  exit 1
fi

brew update || true
brew install --cask betterdisplay --no-quarantine || true

echo "Launching BetterDisplay..."
open -a "BetterDisplay"

echo "Waiting for BetterDisplay initialization..."
sleep 10

BETTERDISPLAY_BIN="/Applications/BetterDisplay.app/Contents/MacOS/BetterDisplay"

if [ ! -f "$BETTERDISPLAY_BIN" ]; then
  echo "BetterDisplay binary not found!"
  exit 1
fi

echo "Creating persistent dummy display..."

"$BETTERDISPLAY_BIN" --create-dummy || true

echo "Waiting for WindowServer to register virtual display..."
sleep 8

echo "Applying high resolution HiDPI framebuffer..."

"$BETTERDISPLAY_BIN" --set-resolution 2560x1440 || true

echo "Final display configuration:"
system_profiler SPDisplaysDataType | grep Resolution || true

echo "Display bootstrap completed."