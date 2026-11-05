#!/bin/sh
# Bootstrap: obtiene el JAR oficial del wrapper si aún no está presente.
set -eu
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
URL="https://services.gradle.org/distributions/gradle-8.11.1-wrapper.jar"
EXPECTED="2db75c40782f5e8ba1fc278a5574bab070adccb2d21ca5a6e5ed840888448046"

if [ ! -s "$JAR" ]; then
    echo "Descargando Gradle Wrapper oficial 8.11.1 (primera ejecución)..." >&2
    TEMP="$JAR.download"
    trap 'rm -f "$TEMP"' EXIT HUP INT TERM
    if command -v curl >/dev/null 2>&1; then
        curl --fail --location --silent --show-error "$URL" -o "$TEMP"
    elif command -v wget >/dev/null 2>&1; then
        wget --quiet "$URL" -O "$TEMP"
    else
        echo "Se requiere curl o wget para obtener el wrapper" >&2
        exit 1
    fi
    if command -v sha256sum >/dev/null 2>&1; then
        ACTUAL=$(sha256sum "$TEMP" | cut -d ' ' -f 1)
    elif command -v shasum >/dev/null 2>&1; then
        ACTUAL=$(shasum -a 256 "$TEMP" | cut -d ' ' -f 1)
    else
        echo "Se requiere sha256sum o shasum para verificar el wrapper" >&2
        exit 1
    fi
    if [ "$ACTUAL" != "$EXPECTED" ]; then
        echo "Checksum incorrecto al descargar el wrapper" >&2
        exit 1
    fi
    mv "$TEMP" "$JAR"
    trap - EXIT HUP INT TERM
fi
exec java -classpath "$JAR" org.gradle.wrapper.GradleWrapperMain "$@"
