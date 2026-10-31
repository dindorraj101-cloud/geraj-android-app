@rem GERAJ Android Project Windows Gradle Wrapper
@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Gradle startup script for Windows
@rem
@rem ##########################################################################

echo GERAJ Android Studio Project - Gradle Build
if not "%JAVA_HOME%" == "" goto runGradle
echo Warning: JAVA_HOME is not set. Please set JAVA_HOME to build via command line.
echo Or open this project directly in Android Studio Ladybug/Koala.
:runGradle
gradle.bat %*
