#!/usr/bin/env bash

set -e

echo "================================"
echo "CI DISPLAY BOOTSTRAP"
echo "================================"

sleep 8

echo "Enabling HiDPI modes..."
sudo defaults write /Library/Preferences/com.apple.windowserver DisplayResolutionEnabled -bool true || true


echo "Installing BetterDisplay..."

brew update || true
brew install --cask betterdisplay || true


echo "Locating BetterDisplay..."

APP_PATH="/Applications/BetterDisplay.app"

if [ ! -d "$APP_PATH" ]; then
    echo "BetterDisplay not found. Continuing."
    exit 0
fi

BIN_PATH="$APP_PATH/Contents/MacOS/BetterDisplay"

if [ ! -f "$BIN_PATH" ]; then
    echo "BetterDisplay binary missing."
    exit 0
fi


echo "Configuring BetterDisplay auto dummy display..."

mkdir -p ~/Library/Preferences

defaults write com.waydabber.BetterDisplay createDummyOnLaunch -bool true
defaults write com.waydabber.BetterDisplay dummyDisplayWidth -int 2560
defaults write com.waydabber.BetterDisplay dummyDisplayHeight -int 1440
defaults write com.waydabber.BetterDisplay dummyDisplayHiDPI -bool true


echo "Launching BetterDisplay in background..."

open -a "BetterDisplay"


echo "Waiting for display initialization..."
sleep 15


echo "Active displays:"
system_profiler SPDisplaysDataType | grep Resolution || true


echo "CI display bootstrap completed."