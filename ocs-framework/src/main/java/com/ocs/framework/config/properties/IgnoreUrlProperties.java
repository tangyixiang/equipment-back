package com.ocs.framework.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "security")
public class IgnoreUrlProperties {

    private List<String> whiteUrl;

    public void setWhiteUrl(List<String> whiteUrl) {
        this.whiteUrl = whiteUrl;
    }

    public String[] getUrls(){
        return whiteUrl.toArray(String[]::new);
    }
}
