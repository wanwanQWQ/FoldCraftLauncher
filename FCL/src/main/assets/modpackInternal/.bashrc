#!/system/bin/sh
export FCL_PATH_SHELL="$(dirname "$(readlink -f "${0}")")/fcl_shell"
chmod +x "${FCL_PATH_SHELL}/bin/"*
export PATH="${FCL_PATH_SHELL}/bin":${PATH}
. ${FCL_PATH_SHELL}/init.sh