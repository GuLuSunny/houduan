package com.ydsw.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "flood")
public class FloodModelConfig {
    /** Python 模型 FastAPI 服务地址 */
    private String apiUrl = "http://localhost:8000";

    /** 默认数据集ID */
    private int defaultDatasetId = 1;

    /** 默认运行ID */
    private int defaultRunId = 1;
}