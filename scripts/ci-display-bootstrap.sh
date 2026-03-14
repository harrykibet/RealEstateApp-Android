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

echo "BetterDisplay located at:"
echo "$APP_PATH"


BIN_PATH="$APP_PATH/Contents/MacOS/BetterDisplay"

if [ ! -f "$BIN_PATH" ]; then
    echo "BetterDisplay binary missing. Continuing."
    exit 0
fi


echo "Launching BetterDisplay service in background..."

"$BIN_PATH" >/dev/null 2>&1 &

sleep 8


echo "Verifying BetterDisplay process..."

if pgrep -f BetterDisplay >/dev/null; then
    echo "BetterDisplay running"
else
    echo "BetterDisplay did not start correctly. Continuing build."
    exit 0
fi


echo "Creating dummy display..."

"$BIN_PATH" --create-dummy >/dev/null 2>&1 || true

sleep 6


echo "Applying HiDPI framebuffer resolution..."

"$BIN_PATH" --set-resolution 2560x1440 >/dev/null 2>&1 || true


echo "Display configuration:"

system_profiler SPDisplaysDataType | grep Resolution || echo "Resolution info unavailable"


echo "Display bootstrap completed."