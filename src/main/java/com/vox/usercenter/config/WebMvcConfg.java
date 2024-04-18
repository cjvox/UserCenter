package com.vox.usercenter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfg implements WebMvcConfigurer {
 
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //设置允许访问的路径
        //配置文件中增加的api应该是工程路径增加，所以还是user
        registry.addMapping("/**")
                //设置允许跨域请求的域名
//                .allowedOrigins("http://usercenter.voxcode.cn","http://findpartner.voxcode.cn")
                .allowedOrigins("http://127.0.0.1:5173")
                //是否允许证书 不再默认开启
                .allowCredentials(true)
                //设置允许的方法
                .allowedMethods("*")
                //跨域允许时间
                .maxAge(3600);
    }



}
