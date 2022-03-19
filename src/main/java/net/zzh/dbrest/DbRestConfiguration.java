package net.zzh.dbrest;

import net.zzh.dbrest.spring.CrudHandler;
import net.zzh.dbrest.spring.DbRestScannerConfigure;
import net.zzh.dbrest.spring.SpringContextHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

@Configuration
@ConditionalOnClass(DbRestScannerConfigure.class)
@EnableConfigurationProperties(DbRestPropertis.class)
@ConditionalOnProperty(prefix = "spring.dbrest", value = "enabled", matchIfMissing = true)
public class DbRestConfiguration implements EnvironmentAware {

    private Environment environment;

    private DbRestPropertis dbRestPropertis;

    //初始bean时处理操作
    @Bean("springContextHolder")
    public SpringContextHolder SpringContextHolder(){
        return new SpringContextHolder();
    }

    @Bean("crudHandler")
    @DependsOn({"dbRestScannerConfigure"})
    public CrudHandler crudHandler(){
        return new CrudHandler();
    }

    //初始bean时处理操作
    @Bean
    @DependsOn({"springContextHolder"})
    public DbRestScannerConfigure dbRestScannerConfigure() {
        DbRestScannerConfigure dbRestScannerConfigure = new DbRestScannerConfigure();
        dbRestScannerConfigure.setBasePackage(dbRestPropertis.getBasePackage());
        return dbRestScannerConfigure;
    }

    /**
     * 载入配置
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        String basePackge = environment.getProperty("spring.dbrest.base-package");
        String requestHandler = environment.getProperty("spring.dbrest.request-handler");
        String resultHandler = environment.getProperty("spring.dbrest.result-handler");
        this.dbRestPropertis = new DbRestPropertis(basePackge,requestHandler,resultHandler);
        this.environment = environment;
        DbRestPropertisHolder.setDbRestPropertis(dbRestPropertis);
    }
}
