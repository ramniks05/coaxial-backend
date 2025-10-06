package com.coaxial.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.coaxial.entity.User;
import com.coaxial.service.UserService;

@Component
public class AdminUserInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserInitializer.class);

    @Autowired
    private UserService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            User adminUser = userService.createAdminUser();
            if (adminUser != null) {
                logger.info("Admin user initialized successfully: {}", adminUser.getUsername());
            } else {
                logger.info("Admin user already exists");
            }
        } catch (Exception e) {
            logger.error("Failed to initialize admin user", e);
        }
    }
}
