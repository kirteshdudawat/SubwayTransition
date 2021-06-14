package com.kirtesh.subwaytransition.utils;

import com.kirtesh.subwaytransition.enums.ConfigKeys;
import org.springframework.stereotype.Component;

@Component
public class Generator {

    private static final String UNDERSCORE = "_";

    /**
     * Used in creating key for JvmCache.lineCodeTypeToLineConfigs
     *
     * @param lineCodeInUpperCase eg. CC, EW.
     * @param configKeys Its enum.
     * @return String, concats lineCode and configKey name. eg. CC_PEAK_HOURS
     */
    public static String generateLineCodeType(String lineCodeInUpperCase, ConfigKeys configKeys) {
        return new StringBuilder(lineCodeInUpperCase).append(UNDERSCORE).append(configKeys.getConfigKey()).toString();
    }
}
