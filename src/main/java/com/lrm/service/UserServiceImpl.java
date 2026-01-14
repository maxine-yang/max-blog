package com.lrm.service;

import com.lrm.dao.UserRepository;
import com.lrm.po.User;
import com.lrm.util.MD5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author: maxine yang
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public User checkUser(String username, String password) {
        String hashedPassword = MD5Utils.code(password);
        logger.debug("Checking user: username={}, password hash={}", username, hashedPassword);
        
        User user = userRepository.findByUsernameAndPassword(username, hashedPassword);
        
        if (user != null) {
            logger.debug("User found: id={}, username={}", user.getId(), user.getUsername());
        } else {
            logger.warn("User not found or password incorrect: username={}", username);
        }
        
        return user;
    }
}
