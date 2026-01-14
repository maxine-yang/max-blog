package com.lrm.service;

import com.lrm.po.User;

/**
 * Author: maxine yang
 */
public interface UserService {

    User checkUser(String username, String password);
}
