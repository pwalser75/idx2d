package idx2d.app;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Typed access to a demo's configuration values, mirroring the old applet
 * {@code getParameter}/{@code getDoubleParameter}/... helpers.
 */
public final class Params {

    private final Map<String, String> values;

    private Params(Map<String, String> values) {
        this.values = values;
    }

    public static Params of(String... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("Expected key/value pairs");
        }
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            map.put(keyValues[i], keyValues[i + 1]);
        }
        return new Params(map);
    }

    public String getString(String key, String defaultValue) {
        return values.getOrDefault(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        String value = values.get(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public int getHex(String key, int defaultValue) {
        String value = values.get(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value, 16);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public double getDouble(String key, double defaultValue) {
        String value = values.get(key);
        if (value == null) return defaultValue;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public float getFloat(String key, float defaultValue) {
        return (float) getDouble(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = values.get(key);
        if (value == null) return defaultValue;
        return Boolean.parseBoolean(value);
    }
}
