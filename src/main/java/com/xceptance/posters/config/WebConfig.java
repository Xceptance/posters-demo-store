/*
 * Copyright (c) 2013-2024 Xceptance Software Technologies GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xceptance.posters.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.xceptance.posters.interceptor.CommonDataInterceptor;

/**
 * Spring MVC configuration for the Posters application.
 * Registers interceptors, static resource handlers, and locale resolution.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer
{
    private final CommonDataInterceptor commonDataInterceptor;

    public WebConfig(CommonDataInterceptor commonDataInterceptor)
    {
        this.commonDataInterceptor = commonDataInterceptor;
    }

    @Bean
    public LocaleResolver localeResolver()
    {
        return new UrlLocaleResolver();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(commonDataInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/assets/**", "/h2-console/**", "/api/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/");
    }
}
