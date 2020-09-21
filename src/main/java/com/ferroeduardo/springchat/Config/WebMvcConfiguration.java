package com.ferroeduardo.springchat.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@EnableWebMvc
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/signup").setViewName("signup");
        registry.addViewController("/error").setViewName("error");
        registry.addViewController("/chat").setViewName("chat");
        registry.addViewController("/admin/panel").setViewName("adminPanel");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        long maxAge = 5;
        registry
                .addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/")
                .setCacheControl(CacheControl.maxAge(maxAge, TimeUnit.MINUTES));
        registry
                .addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCacheControl(CacheControl.maxAge(maxAge, TimeUnit.MINUTES));
        registry
                .addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/")
                .setCacheControl(CacheControl.maxAge(maxAge, TimeUnit.MINUTES));
        registry
                .addResourceHandler("/admin/js/**")
                .addResourceLocations("classpath:/static/admin/js/")
                .setCacheControl(CacheControl.maxAge(maxAge, TimeUnit.MINUTES));
        registry
                .addResourceHandler("/admin/css/**")
                .addResourceLocations("classpath:/static/admin/css/")
                .setCacheControl(CacheControl.maxAge(maxAge, TimeUnit.MINUTES));
        registry
                .addResourceHandler("/webjars/**/**")
                .addResourceLocations("/webjars/")
                .setCacheControl(CacheControl.maxAge(maxAge, TimeUnit.MINUTES))
                .resourceChain(false);
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/favicon.ico")
                .setCachePeriod(0);
    }
}
