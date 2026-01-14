package com.lrm.web.admin;

import com.lrm.po.User;
import com.lrm.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

/**
 * Author: maxine yang
 */
@Controller
@RequestMapping("/admin")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @GetMapping
    public String loginPage() {
        return "admin/login";
    }


    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpSession session,
                        RedirectAttributes attributes) {
        try {
            logger.info("Attempting login, username: {}", username);
            logger.info("Password length: {}", password != null ? password.length() : 0);
            
            User user = userService.checkUser(username, password);
            logger.info("User query result: {}", user != null ? "User found" : "User not found");
            
            if (user != null) {
                logger.info("Login successful, user ID: {}, username: {}", user.getId(), user.getUsername());
                user.setPassword(null);
                session.setAttribute("user", user);
                logger.info("User set in session successfully, redirecting to /admin/index");
                return "redirect:/admin/index";
            } else {
                logger.warn("Login failed, invalid username or password: {}", username);
                attributes.addFlashAttribute("message", "Invalid username or password");
                return "redirect:/admin";
            }
        } catch (Exception e) {
            logger.error("Error occurred during login", e);
            e.printStackTrace();
            attributes.addFlashAttribute("message", "Login failed, please try again later");
            return "redirect:/admin";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/admin";
    }
}
