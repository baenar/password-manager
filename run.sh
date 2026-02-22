#!/bin/bash

# --- CONFIGURATION ---
# Base path to your JDK bin folder
# IMPORTANT: Change this back to your actual JDK path!
JDK_BIN="/C/PATH/TO/YOUT/JDK/bin"
# Library filenames
JLINE="jline.jar"
LOMBOK="lombok.jar"
# ---------------------

# SYSTEM DETECTION AND PATH SETUP
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" ]]; then
    # --- WINDOWS (Git Bash / Cygwin) ---
    JAVAC="$JDK_BIN/javac.exe"
    JAVA="$JDK_BIN/java.exe"
    JAVA_WIN=$(cygpath -w "$JAVA")
    CP_SEP=";"
    IS_WINDOWS=true
else
    # --- LINUX / MACOS ---
    JAVAC="javac"
    JAVA="java"
    CP_SEP=":"
    IS_WINDOWS=false
fi

# Combine libraries into a single classpath string
ALL_LIBS=".${CP_SEP}${JLINE}${CP_SEP}${LOMBOK}"
RUN_LIBS="out${CP_SEP}${JLINE}${CP_SEP}${LOMBOK}"

# PREPARATION
mkdir -p out
find PasswordManager/src -name "*.java" > sources.txt

echo "----------------------------------------------------------"
echo "Compiling for system: $OSTYPE..."
echo "Libraries: $ALL_LIBS"
echo "----------------------------------------------------------"

# COMPILATION
"$JAVAC" -cp "$ALL_LIBS" -processorpath "$ALL_LIBS" -d out @sources.txt

if [ $? -eq 0 ]; then
    echo "COMPILATION SUCCESSFUL!"
    echo ""
    echo "=========================================================="
    echo " COPY AND PASTE THE COMMAND BELOW TO RUN THE PROGRAM:"
    echo "=========================================================="
    echo ""

    if [ "$IS_WINDOWS" = true ]; then
        echo "--- FOR POWERSHELL (IntelliJ Terminal) ---"
        echo "& \"$JAVA_WIN\" -cp \"$RUN_LIBS\" Main \"$1\""
        echo ""
        echo "--- FOR GIT BASH ---"
        echo "winpty \"$JAVA\" -cp \"$RUN_LIBS\" Main \"$1\""
    else
        echo "--- FOR LINUX / MACOS ---"
        echo "java -cp \"$RUN_LIBS\" Main \"my_passwords.db\""
    fi
    echo ""
    echo "=========================================================="
else
    echo "!!! ERROR DURING COMPILATION !!!"
fi

# CLEANUP
rm sources.txt
echo ""
read -p "Press ENTER to close this window..."