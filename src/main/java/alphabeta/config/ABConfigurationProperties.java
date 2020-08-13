/*
 *
 * Copyright (c) 2017 Target Inc
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Target Inc and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Target Inc
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Target Inc
 *
 */

package alphabeta.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app.do-raaste")
@ConditionalOnProperty(name = "app.do-raaste.enable", havingValue = "true")
public class ABConfigurationProperties {

    @Value("${header:#{null}}")
    private String header; // Either header or param is required

    @Value("${param:#{null}}")
    private String param; // Either header or param is required

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    private Map<String, Map<String, String>> mappings;

    public Map<String, Map<String, String>> getMappings() {
        return mappings;
    }

    public void setMappings(Map<String, Map<String, String>> mappings) {
        this.mappings = mappings;
    }
}
