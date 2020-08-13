package alphabeta.config;

import alphabeta.service.ABMetricService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/***
 * Configuration class to load library dependencies in certain order.
 */
@Configuration
@ConditionalOnProperty(name = "app.do-raaste.enable", havingValue = "true")
public class ABBeanLoaderConfig {

    /**
     * {@link com.alphabeta.datastore.ABMappingsDataStore} is required by {@link ABBeanProcessor}
     * which is populated by {@link ABConfigDataLoader}. Hence {@link DependsOn}
     *
     * @return
     */
    @Bean
    @DependsOn({"abMetricService"})
    public ABBeanProcessor abBeanProcessor() {
        return new ABBeanProcessor();
    }

    @Bean
    public ABMetricService abMetricService() {
        return new ABMetricService();
    }

    /**
     * {@link ABConfigDataLoader} requires {@link ABConfigurationProperties} to run
     * Hence {@link DependsOn}
     *
     * @return
     */
    @Bean
    @DependsOn({"abConfigurationProperties"})
    public ABConfigDataLoader abConfigDataLoader() {
        return new ABConfigDataLoader();
    }

    @Bean
    public ABConfigurationProperties abConfigurationProperties() {
        return new ABConfigurationProperties();
    }

}
