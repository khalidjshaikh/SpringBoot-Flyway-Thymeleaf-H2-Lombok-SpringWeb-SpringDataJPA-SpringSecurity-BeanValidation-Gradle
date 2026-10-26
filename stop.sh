#!/usr/bin/env bash
set -uo pipefail

cd "$(dirname "$0")"

PID_FILE=".run.pid"
PID=""

if [[ -f "$PID_FILE" ]]; then
	PID="$(cat "$PID_FILE" 2>/dev/null || true)"
fi

if [[ -z "${PID:-}" ]] || ! kill -0 "$PID" 2>/dev/null; then
	PID="$(pgrep -f 'banking.jar' | head -n 1 || true)"
fi

if [[ -z "${PID:-}" ]]; then
	echo "No running banking instance found."
	rm -f "$PID_FILE"
	exit 0
fi

echo "Stopping banking (PID $PID)..."
kill "$PID" 2>/dev/null || true

for _ in $(seq 1 20); do
	if ! kill -0 "$PID" 2>/dev/null; then
		break
	fi
	sleep 0.5
done

if kill -0 "$PID" 2>/dev/null; then
	echo "Process did not stop in time; force killing..."
	kill -9 "$PID" 2>/dev/null || true
fi

rm -f "$PID_FILE"
echo "Stopped."