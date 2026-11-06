#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${SCRIPT_DIR}"

ENV_NAME="${1:-Local-MsSql}"
shift || true

BRU_BIN="bru"
if ! command -v bru >/dev/null 2>&1 && [ -x "/opt/homebrew/bin/bru" ]; then
    BRU_BIN="/opt/homebrew/bin/bru"
fi

exec "${BRU_BIN}" run . -r --env "${ENV_NAME}" "$@"
