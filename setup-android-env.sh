#!/bin/bash
# Script to set up Android SDK environment for AuraFrameFx

# Create Android SDK directory
mkdir -p $HOME/android-sdk

# Set environment variables
export ANDROID_SDK_ROOT=$HOME/android-sdk
export ANDROID_HOME=$HOME/android-sdk
export JAVA_HOME=/usr/local/sdkman/candidates/java/21.0.7-ms

# Create local.properties
cat > local.properties << EOF
sdk.dir=$ANDROID_SDK_ROOT
jvm=$JAVA_HOME
ANDROID_HOME=$ANDROID_HOME
ndkVersion=26.1.10909125
EOF

echo "Created local.properties with the following settings:"
cat local.properties

echo "To build the project, run: ./gradlew build"
