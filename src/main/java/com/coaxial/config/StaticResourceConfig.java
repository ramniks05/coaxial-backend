package com.coaxial.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(StaticResourceConfig.class);

    @Value("${file.upload.base-dir:uploads}")
    private String baseDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Get absolute path
        String absolutePath = Paths.get(baseDir).toAbsolutePath().toString();
        
        // Ensure path ends with separator for proper URL mapping
        if (!absolutePath.endsWith(File.separator)) {
            absolutePath += File.separator;
        }
        
        // Convert to file:// URL format (normalize path separators)
        String fileUrl = "file:///" + absolutePath.replace("\\", "/");
        
        logger.info("Configuring static file serving:");
        logger.info("  Base directory: {}", baseDir);
        logger.info("  Absolute path: {}", absolutePath);
        logger.info("  File URL: {}", fileUrl);
        logger.info("  URL pattern: /uploads/**");
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(fileUrl);
    }
}


