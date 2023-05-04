package scms;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/***
 * @author Administrator
 * @date 2023/4/1 18:24
 * @function 正则匹配返回用户头像,映射资源
 */

@Configuration
public class MyWebAppConfigurer implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/userimage/**").addResourceLocations("file:/D:\\SCMSFILE\\FileManager\\ImageFile/");
    }
}
