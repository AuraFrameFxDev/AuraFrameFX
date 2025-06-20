#!/bin/bash

# Set up Android SDK and Java environment variables
export ANDROID_SDK_ROOT=/home/codespace/android-sdk
export ANDROID_HOME=/home/codespace/android-sdk
export JAVA_HOME=/usr/local/sdkman/candidates/java/21.0.7-ms
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# Download Android SDK if not present
if [ ! -d "$ANDROID_HOME/cmdline-tools/latest" ]; then
    echo "Android SDK not found. Downloading..."
    mkdir -p $ANDROID_HOME/cmdline-tools
    cd $ANDROID_HOME
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
    unzip -q commandlinetools-linux-11076708_latest.zip -d $ANDROID_HOME/cmdline-tools
    mkdir -p $ANDROID_HOME/cmdline-tools/latest
    mv $ANDROID_HOME/cmdline-tools/cmdline-tools/* $ANDROID_HOME/cmdline-tools/latest/
    rm commandlinetools-linux-11076708_latest.zip
    rm -rf $ANDROID_HOME/cmdline-tools/cmdline-tools
    
    # Accept licenses and install required components
    yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses
    $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0" "ndk;26.1.10909125" "cmake;3.22.1"
fi

# Update local.properties with actual paths
cat > /workspaces/AuraFrameFx-Beta/local.properties << EOF
sdk.dir=$ANDROID_HOME
jvm=$JAVA_HOME
ANDROID_HOME=$ANDROID_HOME
ndkVersion=26.1.10909125
ndk.dir=$ANDROID_HOME/ndk/26.1.10909125
cmake.dir=$ANDROID_HOME/cmake/3.22.1
cmakeVersion=3.22.1
ndkPath=$ANDROID_HOME/ndk/26.1.10909125
cmakePath=$ANDROID_HOME/cmake/3.22.1
cmakeVersionMajor=3.22
cmakeVersionMinor=1
cmakeVersionPatch=1
cmakeVersionFull=3.22.1
cmakeVersionFullWithRevision=3.22.1.20221027-gf8f8c3b0
cmakeRevision=gf8f8c3b0
cmakeRevisionDate=20221027
cmakeRevisionDateFull=2022-10-27 12:00:00 +
EOF

echo "Environment setup complete. Now running Gradle..."

# Run gradle command with all arguments passed to this script
cd /workspaces/AuraFrameFx-Beta
./gradlew "$@"
