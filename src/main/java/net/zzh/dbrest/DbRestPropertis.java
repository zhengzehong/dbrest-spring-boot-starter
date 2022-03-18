package net.zzh.dbrest;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.dbrest")
public class DbRestPropertis {

    private String basePackage;

    private String resultHandler;

    private String requestHandler;

    private String enabled;

    public DbRestPropertis() {
    }

    public DbRestPropertis(String basePackage, String requestHandler, String resultHandler) {
        this.basePackage = basePackage;
        this.resultHandler = resultHandler;
        this.requestHandler = requestHandler;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getResultHandler() {
        return resultHandler;
    }

    public void setResultHandler(String resultHandler) {
        this.resultHandler = resultHandler;
    }

    public String getRequestHandler() {
        return requestHandler;
    }

    public void setRequestHandler(String requestHandler) {
        this.requestHandler = requestHandler;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }
}
