package com.lrm.web.admin;

import com.lrm.po.User;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpSession;

/**
 * Author: maxine yang
 * Base admin controller, automatically adds user object to all admin pages
 */
public abstract class BaseAdminController {

    /**
     * Automatically add user object to model in all admin pages
     */
    @ModelAttribute("user")
    public User getUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }
}
