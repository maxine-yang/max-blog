package com.lrm.config;

import com.lrm.dao.UserRepository;
import com.lrm.po.User;
import com.lrm.util.MD5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Data initializer - creates default admin user
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if users already exist
        if (userRepository.count() == 0) {
            logger.info("Starting to initialize default admin user...");
            
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(MD5Utils.code("admin123")); // Default password: admin123
            admin.setNickname("maxine");
            admin.setEmail("hamanomax01@gmail.com");
            admin.setAvatar("/images/avatar.png");
            admin.setType(1); // 1 indicates admin
            admin.setCreateTime(new Date());
            admin.setUpdateTime(new Date());
            
            userRepository.save(admin);
            logger.info("Default admin user created successfully!");
            logger.info("Username: admin");
            logger.info("Password: admin123");
            logger.info("Please change the password immediately after login!");
        } else {
            logger.info("Users already exist in database, skipping initialization");
        }
    }
}
