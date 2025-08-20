package com.schedule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@Configuration
public class ExternalConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();

        // 获取当前工作目录
        String currentDir = System.getProperty("user.dir");
        String configFilePath = Paths.get(currentDir, "external-config.properties").toString();

        if (!Files.exists(Paths.get(configFilePath))) {
            // 如果文件不存在，创建文件并写入默认配置
            try {
                createDefaultConfigFile(configFilePath);
            } catch (IOException e) {
                System.err.println("Failed to create default configuration file: " + e.getMessage());
                return configurer; // 返回未设置位置的configurer，避免启动失败
            }
        }

        FileSystemResource resource = new FileSystemResource(configFilePath);
        configurer.setLocation(resource);
        System.out.println("Loaded external configuration from: " + configFilePath);

        return configurer;
    }

    private static void createDefaultConfigFile(String filePath) throws IOException {
        Properties defaultProps = new Properties();
        defaultProps.setProperty("tenant.id", "default-tenant-id-value"); // 设置默认值
        defaultProps.setProperty("duban.jieduan.flow.id", "P20250317024044268OH9V0"); // 设置默认值
        defaultProps.setProperty("duban.zhongzhi.flow.id", "P20250317023921141NMNHM"); // 设置默认值
        defaultProps.setProperty("duban.tianbao.flow.id", "P20250317024005965KV0QA"); // 设置默认值
        defaultProps.setProperty("duban.banjie.flow.id", "P2025031702412775351LGK"); // 设置默认值
        defaultProps.setProperty("duban.zhibiao.flow.id", "P20250317024238936JP1OW"); // 设置默认值
        defaultProps.setProperty("duban.qingdan.flow.id", "P20250317024217271OJX06"); // 设置默认值
        defaultProps.setProperty("duban.geren.flow.id", "P20250317024307714B6UVF"); // 设置默认值

        defaultProps.setProperty("duban.jieduan.jiaoban.id", "m85i4x1dgshw6"); // 设置默认值
        defaultProps.setProperty("duban.zhongzhi.qiantou.id", "m85hvad96zqkc"); // 设置默认值
        defaultProps.setProperty("duban.zhongzhi.jiaoban.id", "m85i2kyhmvg3q"); // 设置默认值
        defaultProps.setProperty("duban.banjie.jiaoban.id", "m85i04a1nuxd2"); // 设置默认值
        defaultProps.setProperty("duban.banjie.qiantou.id", "m85hstanyga4a"); // 设置默认值
        defaultProps.setProperty("duban.geren.jiaoban.id", "m88kgsbhz5tm1"); // 设置默认值

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            defaultProps.store(fos, "Default Configuration");
            System.out.println("Created default configuration file at: " + filePath);
        }
    }
}
