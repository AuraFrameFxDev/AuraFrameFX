#!/bin/bash
set -e
set -x

# Set up environment variables
export ANDROID_SDK_ROOT=/home/codespace/android-sdk
export ANDROID_HOME=/home/codespace/android-sdk
export JAVA_HOME=/usr/local/sdkman/candidates/java/21.0.7-ms

echo "Setting up Android SDK in $ANDROID_HOME"

# Create directories
mkdir -p $ANDROID_HOME

# Update local.properties
cat > /workspaces/AuraFrameFx-Beta/local.properties << EOF
sdk.dir=$ANDROID_HOME
jvm=$JAVA_HOME
ANDROID_HOME=$ANDROID_HOME
ndkVersion=26.1.10909125
EOF

echo "Updated local.properties"
cat /workspaces/AuraFrameFx-Beta/local.properties

echo "Attempting to run Gradle tasks"
cd /workspaces/AuraFrameFx-Beta
./gradlew tasks --stacktrace
