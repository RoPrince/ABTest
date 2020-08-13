package alphabeta.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "app.do-raaste.enable", havingValue = "true")
public class ABConfigDataLoader extends WebMvcConfigurerAdapter {

    private Logger logger = LoggerFactory.getLogger(ABConfigDataLoader.class);

    @Autowired
    private ABConfigurationProperties abConfigurationProperties;

    @PostConstruct
    public void init() throws IOException, ClassNotFoundException {

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Map<String, String>> appConfigMappings = abConfigurationProperties.getMappings();
        logger.info("Mappings found in app yaml '{}'", objectMapper.writeValueAsString(appConfigMappings));

        Map<String, String> mappingValuesMap = new HashMap<>();
        Map<String, Class<? extends TenantFinder>> tenantFinderMap = new HashMap<>();

        for (Map.Entry<String, Map<String, String>> entry : appConfigMappings.entrySet()) {
            String featureKey = entry.getKey().trim().toUpperCase();
            String commaSeparatedValues = entry.getValue().get("value");

            mappingValuesMap.put(featureKey, commaSeparatedValues);

            String[] tenants = commaSeparatedValues.split(",");

            if (tenants != null && tenants.length > 0) {
                for (String tenant : tenants) {
                    ABMappingsDataStore.addToMapOfMappings(ABMappingsDataStore.getKey(featureKey, tenant.trim()), Boolean.TRUE);
                }
            }

            String tenantFinderClassRef = entry.getValue().get("tenant-finder");

            if (tenantFinderClassRef != null) {
                String[] tenantFinderTypeValuePair = tenantFinderClassRef.split(":");

                if (!"class".equals(tenantFinderTypeValuePair[0])) {
                    throw new IllegalArgumentException(tenantFinderTypeValuePair[0].concat(" is not supported type"));
                }

                String tenantFilterClassName = tenantFinderTypeValuePair[1];
                try {
                    Class<? extends TenantFinder> cls = (Class<? extends TenantFinder>) Class.forName(tenantFilterClassName.trim());
                    tenantFinderMap.put(featureKey, cls);

                } catch (ClassNotFoundException e) {
                    logger.error("Tenant finder class not found '{}'", tenantFilterClassName, e);
                    throw new ClassNotFoundException(tenantFilterClassName, e);
                } catch (ClassCastException e) {
                    logger.error("Tenant finder class is not of type TenantFilter '{}'", tenantFilterClassName, e);
                    throw new IllegalArgumentException(tenantFilterClassName, e);
                }
            }

        }
        ABMappingsDataStore.setAppMappingValuesMap(mappingValuesMap);
        ABMappingsDataStore.setTenantFinderMap(tenantFinderMap);

    }


    /**
     * Once loaded all beans. Validate AB bean config
     *
     * @param event
     */
    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {

        ABBeanDataStore.validateABConfigs();

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TenantInterceptor(abConfigurationProperties));
    }


}