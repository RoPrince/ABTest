package alphabeta.datastore;

import com. alphabeta.filter.TenantFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public final class ABMappingsDataStore {

    private static Logger logger = LoggerFactory.getLogger(ABMappingsDataStore.class);

    private static Map<String, Boolean> mapOfMappings = new HashMap<>();
    private static Map<String, String> appMappingValuesMap = new HashMap<>();
    private static Map<String, Class<? extends TenantFinder>> featureWiseTenantFinder = new HashMap<>();

    public static void addToMapOfMappings(String key, Boolean flag) {
        mapOfMappings.put(key, flag);
    }

    public static boolean isPilotEnabledFor(String feature, String tenantId) {

        if (StringUtils.isEmpty(tenantId)) {
            return false;
        }

        boolean isPilotEnabled = mapOfMappings.get(getKey(feature, tenantId)) != null;

        logger.info("Pilot is enabled: {} for feature: {} and tenant: {}", isPilotEnabled, feature, tenantId);

        return isPilotEnabled;
    }

    public static String getKey(String feature, String tenantId) {
        return (feature + "~~" + tenantId).trim().toUpperCase();
    }


    public static boolean isFeatureChainWide(String feature) {
        boolean chainWideFlag = mapOfMappings.get(getKey(feature, "ALL")) != null;

        logger.info("ChainWide: {} for feature: {}", chainWideFlag, feature);

        return chainWideFlag;
    }


    public static void setAppMappingValuesMap(Map<String, String> mappings) {
        ABMappingsDataStore.appMappingValuesMap.putAll(mappings);
    }

    public static void setTenantFinderMap(Map<String, Class<? extends TenantFinder>> mappings) {
        ABMappingsDataStore.featureWiseTenantFinder.putAll(mappings);
    }

    public static Class<? extends TenantFinder> getTenantFinderBy(String featureKey) {
        return ABMappingsDataStore.featureWiseTenantFinder.get(featureKey.trim().toUpperCase());
    }

    public static boolean isAppMappingAbsentInProperties(String feature) {
        return StringUtils.isEmpty(appMappingValuesMap.get(feature.trim().toUpperCase())) || "NULL".equalsIgnoreCase(appMappingValuesMap.get(feature.trim().toUpperCase()));
    }
}
