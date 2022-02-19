package cn.edu.hstc.config;

import cn.edu.hstc.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login.html").setViewName("index");
        registry.addViewController("/amain1.html").setViewName("admin/amain1");
        registry.addViewController("/amain2.html").setViewName("admin/amain2");
        registry.addViewController("/amain3.html").setViewName("admin/amain3");
        registry.addViewController("/amain4.html").setViewName("admin/amain4");
        registry.addViewController("/amain5.html").setViewName("admin/amain5");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**").excludePathPatterns("/", "/login.html", "/user/login", "/bootstrapv5/**", "/css/**", "/images/**", "/js/**");
    }
}
