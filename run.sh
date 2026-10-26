#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

JAR="build/libs/banking.jar"
PID_FILE=".run.pid"

if [[ ! -f "$JAR" ]]; then
	echo "No jar found. Building first..."
	./build.sh
fi

JAVA_BIN="${JAVA_HOME:+$JAVA_HOME/bin/}java"
if ! command -v "$JAVA_BIN" >/dev/null 2>&1; then
	echo "Java was not found. Install Java 27 or set JAVA_HOME." >&2
	exit 1
fi

JAVA_VERSION="$("$JAVA_BIN" -version 2>&1 | sed -n 's/.*version "\([0-9]*\).*/\1/p' | head -n 1)"
if [[ -z "$JAVA_VERSION" || "$JAVA_VERSION" -lt 27 ]]; then
	echo "Java 27 or newer is required (found: ${JAVA_VERSION:-unknown})." >&2
	exit 1
fi

if [[ -f "$PID_FILE" ]]; then
	OLD_PID="$(cat "$PID_FILE" 2>/dev/null || true)"
	if [[ -n "${OLD_PID:-}" ]] && kill -0 "$OLD_PID" 2>/dev/null; then
		echo "Application already running (PID $OLD_PID). Use ./stop.sh first." >&2
		exit 1
	fi
fi

echo "Starting banking on http://localhost:8080/"
# shellcheck disable=SC2086
"$JAVA_BIN" ${JAVA_OPTS:-} -jar "$JAR" "$@" &
APP_PID=$!
echo "$APP_PID" > "$PID_FILE"

cleanup() {
	rm -f "$PID_FILE"
}
trap cleanup EXIT INT TERM

wait "$APP_PID"