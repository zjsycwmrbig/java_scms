package scms.Interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author seaside
 * 2023-04-21 15:40
 */
@Configuration
@ComponentScan({"scms.Controller"})
@EnableWebMvc
//拦截器配置类
public class SpringMvcConfig implements WebMvcConfigurer {
    @Autowired
    private ScmsInterceptor scmsInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 允许的请求
        registry.addInterceptor(scmsInterceptor).addPathPatterns("/add/*"); //对请求路径进行标识
        registry.addInterceptor(scmsInterceptor).addPathPatterns("/user/*");
        registry.addInterceptor(scmsInterceptor).addPathPatterns("/query/*");
        registry.addInterceptor(scmsInterceptor).addPathPatterns("/navigate/*");
        registry.addInterceptor(scmsInterceptor).addPathPatterns("/org/*");
        registry.addInterceptor(scmsInterceptor).addPathPatterns("/notice/*");
        registry.addInterceptor(scmsInterceptor).addPathPatterns("/static/*");


    }

}
