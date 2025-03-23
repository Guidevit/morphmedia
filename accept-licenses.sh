#!/bin/bash

# Create licenses directory
mkdir -p /nix/store/android-sdk/licenses/

# Accept Android SDK licenses
echo -e "8933bad161af4178b1185d1a37fbf41ea5269c55\nd56f5187479451eabf01fb78af6dfcb131a6481e\n24333f8a63b6825ea9c5514f83c2829b004d1fee" > /nix/store/android-sdk/licenses/android-sdk-license
echo "84831b9409646a918e30573bab4c9c91346d8abd" > /nix/store/android-sdk/licenses/android-sdk-preview-license
echo "33b6a2b64607f11b759f320ef9dff4ae5c47d97a" > /nix/store/android-sdk/licenses/google-gdk-license

echo "Android SDK licenses accepted."