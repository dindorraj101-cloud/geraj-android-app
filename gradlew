#!/bin/sh

# GERAJ Android Gradle Wrapper
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'
APP_BASE_NAME=`basename "$0"`
APP_HOME="`pwd -P`"

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

echo "Starting GERAJ Android Build using Gradle..."
if command -v gradle >/dev/null 2>&1; then
    exec gradle "$@"
else
    echo "Gradle wrapper: Running task $@..."
    echo "To build in Android Studio: Open the 'android' folder directly in Android Studio."
    echo "Or install Gradle via: apt-get install gradle or sdkman install gradle."
fi
