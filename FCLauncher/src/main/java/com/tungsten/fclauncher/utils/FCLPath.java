package com.tungsten.fclauncher.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class FCLPath {
    public static Context CONTEXT;

    public static String NATIVE_LIB_DIR;

    public static String INTERNAL_DIR;
    public static String FILES_DIR;
    public static String CACHE_DIR;

    public static String PLUGIN_DIR;
    public static String AUTHLIB_INJECTOR_PATH;
    public static String LIB_FIXER_PATH;
    public static String MIO_LAUNCH_WRAPPER;
    public static String BACKGROUND_DIR;
    public static String LT_BACKGROUND_PATH;
    public static String DK_BACKGROUND_PATH;

    public static String RUNTIME_DIR;
    public static String JAVA_8_PATH;
    public static String JAVA_11_PATH;
    public static String JAVA_17_PATH;
    public static String JAVA_21_PATH;
    public static String JNA_PATH;
    public static String LWJGL_DIR;
    public static String CACIOCAVALLO_8_DIR;
    public static String CACIOCAVALLO_11_DIR;
    public static String CACIOCAVALLO_17_DIR;

    public static String EXTERNAL_DIR;
    public static String SHARED_COMMON_DIR;
    public static String CONTROLLER_DIR;
    public static String LOG_DIR;

    public static Properties APP_CONFIG_PROPERTIES;

    public static void loadPaths(Context context) {
        CONTEXT = context;

        NATIVE_LIB_DIR = context.getApplicationInfo().nativeLibraryDir;

        FILES_DIR = context.getFilesDir().getAbsolutePath();
        INTERNAL_DIR = new File(FILES_DIR).getParentFile().getAbsolutePath();
        CACHE_DIR = INTERNAL_DIR + "/cache";

        APP_CONFIG_PROPERTIES = loadProp(context);

        PLUGIN_DIR = FILES_DIR + "/plugins";
        AUTHLIB_INJECTOR_PATH = PLUGIN_DIR + "/authlib-injector.jar";
        LIB_FIXER_PATH = PLUGIN_DIR + "/MioLibFixer.jar";
        MIO_LAUNCH_WRAPPER = PLUGIN_DIR + "/MioLaunchWrapper.jar";
        BACKGROUND_DIR = FILES_DIR + "/background";
        LT_BACKGROUND_PATH = BACKGROUND_DIR + "/lt.png";
        DK_BACKGROUND_PATH = BACKGROUND_DIR + "/dk.png";

        RUNTIME_DIR = context.getDir("runtime", 0).getAbsolutePath();
        JAVA_8_PATH = RUNTIME_DIR + "/java/jre8";
        JAVA_11_PATH = RUNTIME_DIR + "/java/jre11";
        JAVA_17_PATH = RUNTIME_DIR + "/java/jre17";
        JAVA_21_PATH = RUNTIME_DIR + "/java/jre21";
        JNA_PATH = RUNTIME_DIR + "/jna";
        LWJGL_DIR = RUNTIME_DIR + "/lwjgl";
        CACIOCAVALLO_8_DIR = RUNTIME_DIR + "/caciocavallo";
        CACIOCAVALLO_11_DIR = RUNTIME_DIR + "/caciocavallo11";
        CACIOCAVALLO_17_DIR = RUNTIME_DIR + "/caciocavallo17";

        EXTERNAL_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + APP_CONFIG_PROPERTIES.getProperty("put-directory","FCL-Server");
        if (APP_CONFIG_PROPERTIES.getProperty("put-directory-suffix","true").equals("true")) {
            EXTERNAL_DIR = EXTERNAL_DIR + "/" + context.getPackageName().substring("com.tungsten.fcl.".length());
        }
        SHARED_COMMON_DIR = EXTERNAL_DIR + "/.minecraft";
        CONTROLLER_DIR = EXTERNAL_DIR + "/controllers";
        LOG_DIR = EXTERNAL_DIR + "/logs";

        init(INTERNAL_DIR);
        init(FILES_DIR);
        init(CACHE_DIR);
        init(PLUGIN_DIR);
        init(BACKGROUND_DIR);
        init(RUNTIME_DIR);
        init(JAVA_8_PATH);
        init(JAVA_11_PATH);
        init(JAVA_17_PATH);
        init(JAVA_21_PATH);
        init(JNA_PATH);
        init(LWJGL_DIR);
        init(CACIOCAVALLO_8_DIR);
        init(CACIOCAVALLO_11_DIR);
        init(CACIOCAVALLO_17_DIR);
        init(EXTERNAL_DIR);
        init(SHARED_COMMON_DIR);
        init(CONTROLLER_DIR);
        init(LOG_DIR);
    }

    private static boolean init(String path) {
        if (!new File(path).exists()) {
            return new File(path).mkdirs();
        }
        return true;
    }

    private static Properties loadProp(Context context) {
        String local_prop = EXTERNAL_DIR + "local.properties";
        File local_prop_file = new File(local_prop);
        if (local_prop_file.exists()) {
            try (FileInputStream local_prop_stream = new FileInputStream(local_prop_file)) {
                Properties properties = new Properties();
                properties.load(local_prop_stream);
                return properties;
            } catch (Exception ignore) {
                return new PropertiesFileParse("local.properties", context).getProperties();
            }
        } else {
            return new PropertiesFileParse("local.properties", context).getProperties();
        }
    }

}