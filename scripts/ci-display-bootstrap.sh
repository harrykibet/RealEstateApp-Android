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
defaults write com.waydabber.BetterDisplay dummyDisplayWidth -int 2560
defaults write com.waydabber.BetterDisplay dummyDisplayHeight -int 1440
defaults write com.waydabber.BetterDisplay dummyDisplayHiDPI -bool true

echo "Launching BetterDisplay in background..."
open -a "$APP_PATH"

# Wait for the dummy display to register
sleep 15

echo "Verifying BetterDisplay process..."
if pgrep -f BetterDisplay >/dev/null; then
    echo "BetterDisplay running"
else
    echo "BetterDisplay did not start correctly. Continuing build."
fi

echo "Display configuration:"
system_profiler SPDisplaysDataType | grep Resolution || echo "Resolution info unavailable"

echo "CI display bootstrap completed."