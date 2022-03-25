package net.zzh.dbrest;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(DbRestPropertis.class)
@ConfigurationProperties(prefix = "spring.dbrest")
public class DbRestPropertis {

    private String basePackage;

    private String resultHandler;

    private String requestHandler;

    private boolean enabled = true;

    private boolean showSql = true;

    public DbRestPropertis() {
    }

    public DbRestPropertis(String basePackage, String requestHandler, String resultHandler, boolean enabled, boolean showSql) {
        this.basePackage = basePackage;
        this.showSql = showSql;
        this.resultHandler = resultHandler;
        this.requestHandler = requestHandler;
        this.enabled = enabled;
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

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getShowSql() {
        return showSql;
    }

    public void setShowSql(boolean showSql) {
        this.showSql = showSql;
    }
}
