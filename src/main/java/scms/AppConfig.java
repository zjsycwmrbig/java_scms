package scms;

import org.aopalliance.intercept.Interceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import scms.Interceptor.ScmsInterceptor;

/***
 * @author Administrator
 * @date 2023/3/27 21:40
 * @function
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Bean
    public ScmsInterceptor Interceptor() {
        return new ScmsInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(Interceptor())
                .addPathPatterns("/**"); // 拦截所有请求
    }

}

