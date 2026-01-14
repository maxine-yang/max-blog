package com.lrm.web.admin;

import com.lrm.po.Blog;
import com.lrm.po.User;
import com.lrm.service.BlogService;
import com.lrm.service.TagService;
import com.lrm.service.TypeService;
import com.lrm.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

/**
 * Author: maxine yang
 */
@Controller
@RequestMapping("/admin")
public class BlogController extends BaseAdminController {

    private static final String INPUT = "admin/blogs-input";
    private static final String LIST = "admin/blogs";
    private static final String REDIRECT_LIST = "redirect:/admin/blogs";


    @Autowired
    private BlogService blogService;
    @Autowired
    private TypeService typeService;
    @Autowired
    private TagService tagService;

    @GetMapping("/blogs")
    public String blogs(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        BlogQuery blog, Model model) {
        model.addAttribute("types", typeService.listType());
        model.addAttribute("page", blogService.listBlog(pageable, blog));
        return LIST;
    }

    @PostMapping("/blogs/search")
    public String search(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                         BlogQuery blog, Model model) {
        model.addAttribute("page", blogService.listBlog(pageable, blog));
        return "admin/blogs :: blogList";
    }


    @GetMapping("/blogs/input")
    public String input(Model model) {
        setTypeAndTag(model);
        model.addAttribute("blog", new Blog());
        return INPUT;
    }

    private void setTypeAndTag(Model model) {
        model.addAttribute("types", typeService.listType());
        model.addAttribute("tags", tagService.listTag());
    }


    @GetMapping("/blogs/{id}/input")
    public String editInput(@PathVariable("id") Long id, Model model) {
        setTypeAndTag(model);
        Blog blog = blogService.getBlog(id);
        if (blog == null) {
            blog = new Blog();
        } else {
            blog.init();
        }
        model.addAttribute("blog",blog);
        return INPUT;
    }



    @PostMapping("/blogs")
    public String post(Blog blog, RedirectAttributes attributes, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            attributes.addFlashAttribute("message", "Please login first");
            return "redirect:/admin";
        }
        blog.setUser(user);
        
        if (blog.getType() != null && blog.getType().getId() != null) {
            blog.setType(typeService.getType(blog.getType().getId()));
        }
        
        String tagIds = blog.getTagIds();
        if (tagIds != null && !tagIds.trim().isEmpty()) {
            blog.setTags(tagService.listTag(tagIds));
        } else {
            blog.setTags(new ArrayList<>());
        }
        
        // Handle custom publish time
        String publishTimeStr = blog.getPublishTime();
        if (publishTimeStr != null && !publishTimeStr.trim().isEmpty()) {
            try {
                // Convert datetime-local format (yyyy-MM-ddTHH:mm) from frontend to Date
                // datetime-local format may be yyyy-MM-ddTHH:mm or yyyy-MM-ddTHH:mm:ss
                LocalDateTime localDateTime;
                if (publishTimeStr.length() == 16) {
                    // Format: yyyy-MM-ddTHH:mm
                    localDateTime = LocalDateTime.parse(publishTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
                } else {
                    // Format: yyyy-MM-ddTHH:mm:ss or others
                    localDateTime = LocalDateTime.parse(publishTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
                Date publishTime = java.sql.Timestamp.valueOf(localDateTime);
                blog.setCreateTime(publishTime);
            } catch (Exception e) {
                // If parsing fails, use default time (handled in Service)
            }
        }
        
        // Extract first image from content if firstPicture is not set
        if ((blog.getFirstPicture() == null || blog.getFirstPicture().trim().isEmpty()) 
            && blog.getContent() != null && !blog.getContent().trim().isEmpty()) {
            String firstImage = com.lrm.util.MarkdownUtils.extractFirstImage(blog.getContent());
            if (firstImage != null && !firstImage.isEmpty()) {
                blog.setFirstPicture(firstImage);
            }
        }
        
        Blog b;
        if (blog.getId() == null) {
            b =  blogService.saveBlog(blog);
        } else {
            b = blogService.updateBlog(blog.getId(), blog);
        }

        if (b == null ) {
            attributes.addFlashAttribute("message", "Operation failed");
        } else {
            attributes.addFlashAttribute("message", "Operation successful");
        }
        return REDIRECT_LIST;
    }


    @GetMapping("/blogs/{id}/delete")
    public String delete(@PathVariable("id") Long id,RedirectAttributes attributes) {
        blogService.deleteBlog(id);
        attributes.addFlashAttribute("message", "Deleted successfully");
        return REDIRECT_LIST;
    }

}
