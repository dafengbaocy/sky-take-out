package com.sky.config;

import com.sky.properties.MinIoProperties;
import com.sky.utils.MinIoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MinioConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public MinIoUtil aliOssUtil(MinIoProperties minIoProperties){
        log.info("开始创建阿里云文件上传工具类对象：{}",minIoProperties);
        return new MinIoUtil(minIoProperties);
    }
}
