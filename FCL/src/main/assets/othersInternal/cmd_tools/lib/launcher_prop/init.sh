#!/system/bin/sh
pkgs="$(pm list packages --uid "$(id -u)")"
if [[ "$?" != "0" ]]; then
  echo "WARN: Local apps are NOT ALLOWED to access Shell on this device."
  echo "WARN: Some features may NOT work."
  export FCL_PATH_INTERNAL="${HOME}"
  echo "WARN: Using '/sdcard/Download' as external dir."
  export FCL_PATH_EXTERNAL="/sdcard/Download"
else
  for pkg in ${pkgs};do
    export FCL_CONF_PKGID="${pkg/#package:/}"
    unset pkg
    break
  done
  export FCL_PATH_INTERNAL="/data/data/${FCL_CONF_PKGID}"
  FCL_PATH_PKG="$(pm path ${FCL_CONF_PKGID})"
  export FCL_PATH_PKG="${FCL_PATH_PKG/#package:/}"
  appCfg="$(unzip -p "${FCL_PATH_PKG}" "assets/local.properties")"
  FCL_PATH_EXTERNAL="$(echo -n "${appCfg}" | grep "put-directory=")"
  export FCL_PATH_EXTERNAL="/sdcard/${FCL_PATH_EXTERNAL/#put-directory=/}"
  dirSuffix="$(echo "${appCfg}" | grep "put-directory-suffix=")"
  dirSuffix="${dirSuffix/#put-directory-suffix=/}"
  if [[ "${dirSuffix}" == "true" ]]; then
    export FCL_PATH_EXTERNAL="${FCL_PATH_EXTERNAL}/${FCL_CONF_PKGID/#com.tungsten.fcl./}"
  fi
  unset dirSuffux
  FCL_CONF_NSa="$(echo "${appCfg}" | grep "primary-nameserver=")"
  export FCL_CONF_NSa="${FCL_CONF_NSa/#primary-nameserver=/}"
  FCL_CONF_NSb="$(echo "${appCfg}" | grep "secondary-nameserver=")"
  export FCL_CONF_NSb="${FCL_CONF_NSb/#secondary-nameserver=/}"
  unset appCfg
fi
unset pkgs