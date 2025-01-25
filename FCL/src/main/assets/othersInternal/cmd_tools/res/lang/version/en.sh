#!/system/bin/sh
lang_version_help="Usage: version ([Args]) ([runtime])\n\
Get the version of the runtime.\n\
[Args]\n\
    -h, --help\n\
        display this help and exit\n\
    -n\n\
        do not output the trailing newline\n\
        (should only be used in .sh script, does NOT support output in FCL Shell!)\n\
[runtime]\n\
         Available component: cacio, cacio11, cacio17, java11, java17, java21, java8, jna, lwjgl, lwjgl-boat, game, other.\n\
         when empty: list all components and their version\n\
         when not empty: get the version of the specific component"
lang_version_notfound="component not found."