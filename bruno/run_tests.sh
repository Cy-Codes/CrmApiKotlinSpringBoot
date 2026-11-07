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

# Write endpoints require a Bearer token with role admin/user. Mint dev tokens when the helper script is available
# (override with ADMIN_TOKEN / USER_TOKEN). See README "Authentication & roles".
DEV_TOKEN_SCRIPT="${SCRIPT_DIR}/../../crm-infra/scripts/dev-token.sh"
ADMIN_TOKEN="${ADMIN_TOKEN:-}"
USER_TOKEN="${USER_TOKEN:-}"
if [ -x "${DEV_TOKEN_SCRIPT}" ]; then
    ADMIN_TOKEN="${ADMIN_TOKEN:-$("${DEV_TOKEN_SCRIPT}" admin)}"
    USER_TOKEN="${USER_TOKEN:-$("${DEV_TOKEN_SCRIPT}" user)}"
fi

exec "${BRU_BIN}" run . -r --env "${ENV_NAME}" \
    --env-var "admin_token=${ADMIN_TOKEN}" \
    --env-var "user_token=${USER_TOKEN}" \
    "$@"
