package com.github.sinsinpub.pero.config;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TimeZone;

import jodd.props.Props;
import jodd.util.StringUtil;

/**
 * Global application configuration properties.
 * <p>
 * Load and merge values from internal default, system, environment, file in etc folder.
 */
public final class AppProps extends Props {

    /** Singleton instance */
    public static final AppProps PROPS = new AppProps();
    static final String DEFAULT_PROPS_FILE = "/default.properties";
    static final String RUNTIME_PROPS_FILE = "etc/runtime.properties";
    static final String ENV_PREFIX = "pero";

    private AppProps() {
        super();
        forceVmDefaults();
        loadAndMergeProps();
    }

    private static void forceVmDefaults() {
        System.setProperty("file.encoding", "UTF-8");
        Locale.setDefault(Locale.JAPAN);
        TimeZone zone = TimeZone.getTimeZone("GMT+9");
        TimeZone.setDefault(zone);

        System.setProperty("io.netty.noJavassist", "true");
    }

    /**
     * Load and merge props in order which later overrides earlier.
     */
    private void loadAndMergeProps() {
        // Defaults in classpath
        try {
            this.load(AppProps.class.getResourceAsStream(DEFAULT_PROPS_FILE));
        } catch (Exception e) {
            // IGNORE
        }
        // JVM system properties
        load(System.getProperties());
        // Environment variables with specified prefix only
        loadEnvironmentPrefixOnly(ENV_PREFIX);
        // Configuration file from etc folder
        try {
            this.load(new File(RUNTIME_PROPS_FILE), "UTF-8");
        } catch (Exception e) {
            // IGNORE
        }
    }

    private void loadEnvironmentPrefixOnly(String prefix) {
        Map<String, String> envMap = System.getenv();
        Properties propsToMerge = new Properties();
        for (Entry<String, String> entry : envMap.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(prefix)) {
                key = key.substring(prefix.length());
                propsToMerge.put(key, entry.getValue());
            }
        }
        load(propsToMerge);
    }

    @Override
    public String toString() {
        return new StringBuilder("AppProps").append("[")
                .append("profiles=")
                .append(getActiveProfiles())
                .append(",total=")
                .append(countTotalProperties())
                .append("]")
                .toString();
    }

    public Properties extractActiveProfilesProps() {
        Properties target = new Properties();
        extractProps(target);
        return target;
    }

    public String get(String key, String defaultValue) {
        String value = super.getValue(key);
        return StringUtil.isEmpty(value) ? defaultValue : value;
    }

    public Integer getInteger(String key, Integer defaultValue) {
        String value = super.getValue(key);
        return StringUtil.isEmpty(value) ? defaultValue : Integer.valueOf(value);
    }

    public Long getLong(String key, Long defaultValue) {
        String value = super.getValue(key);
        return StringUtil.isEmpty(value) ? defaultValue : Long.valueOf(value);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        String value = super.getValue(key);
        return StringUtil.isEmpty(value) ? defaultValue : Boolean.valueOf(value);
    }

}
