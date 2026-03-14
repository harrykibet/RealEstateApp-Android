#!/usr/bin/env bash

set -u

echo "========================================"
echo "CI DISPLAY BOOTSTRAP"
echo "========================================"

echo "Waiting for WindowServer..."
sleep 10

echo "Enabling HiDPI modes..."
sudo defaults write /Library/Preferences/com.apple.windowserver DisplayResolutionEnabled -bool true || true

echo "Installing BetterDisplay..."
if command -v brew >/dev/null 2>&1; then
    brew update || true
    brew install --cask betterdisplay || true
else
    echo "Homebrew not available, skipping installation"
fi

echo "Searching for BetterDisplay installation..."
APP_PATH=$(find /Applications -maxdepth 2 -name "BetterDisplay.app" 2>/dev/null | head -n 1)

if [ -z "$APP_PATH" ]; then
    echo "BetterDisplay not found. Continuing without virtual display."
    exit 0
fi

echo "BetterDisplay located at: $APP_PATH"

BIN_PATH="$APP_PATH/Contents/MacOS/BetterDisplay"
if [ ! -f "$BIN_PATH" ]; then
    echo "BetterDisplay binary missing. Continuing build."
    exit 0
fi

# --- CI-safe auto dummy display configuration ---
echo "Configuring BetterDisplay auto dummy display..."
mkdir -p ~/Library/Preferences

defaults write com.waydabber.BetterDisplay createDummyOnLaunch -bool true
defaults write com.waydabber.BetterDisplay dummyDisplayWidth -int 1920
defaults write com.waydabber.BetterDisplay dummyDisplayHeight -int 1080
defaults write com.waydabber.BetterDisplay dummyDisplayHiDPI -bool true

echo "Display configuration:"
system_profiler SPDisplaysDataType | grep Resolution || echo "Resolution info unavailable"

echo "CI display bootstrap completed."