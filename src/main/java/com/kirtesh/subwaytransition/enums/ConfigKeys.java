package com.kirtesh.subwaytransition.enums;

import com.kirtesh.subwaytransition.exception.ApiServiceException;

import java.util.ArrayList;
import java.util.List;

/**
 * Would be used in JvmCache.lineCodeTypeToLineConfigs cache via Generator.generateLineCodeType()
 */
public enum ConfigKeys {
    PEAK_HOURS("PEAK_HOURS"),
    NIGHT_HOURS("NIGHT_HOURS"),
    DEFAULT_AVAILABLE_HOURS("DEFAULT_AVAILABLE_HOURS");

    private static List<String> configKeys = new ArrayList<>();
    private String _configKey;

    private ConfigKeys(String _configKey) {
        this._configKey = _configKey;
    }

    public String getConfigKey() {
        return _configKey;
    }

    static {
        for(ConfigKeys configKey : ConfigKeys.values()) {
            configKeys.add(configKey._configKey);
        }
    }

    public static List<String> getConfigKeys() {
        List<String> _configKeys = new ArrayList<String>(configKeys);
        return _configKeys;
    }

    public static boolean isValidConfigKey(String configKey) {
        configKey = configKey.toUpperCase();

        if(configKeys.contains(configKey)) {
            return true;
        }
        return false;
    }

    public static ConfigKeys fromString(String keyAsString) throws ApiServiceException {
        for (ConfigKeys configKey : ConfigKeys.values()) {
            if (configKey._configKey.equalsIgnoreCase(keyAsString)) {
                return configKey;
            }
        }
        throw new ApiServiceException(APIErrorCodes.CONVERTER_SERVICE_EXCEPTION);
    }
}