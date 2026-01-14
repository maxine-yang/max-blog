package com.lrm.web;

import com.lrm.NotFoundException;
import com.lrm.po.Blog;
import com.lrm.service.BlogService;
import com.lrm.service.TagService;
import com.lrm.service.TypeService;
import com.lrm.vo.BlogQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Author: maxine yang
 */
@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    @GetMapping("/")
    public String index(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        Model model) {
        model.addAttribute("page",blogService.listBlog(pageable));
        model.addAttribute("types", typeService.listTypeTop(6));
        model.addAttribute("tags", tagService.listTagTop(10));
        model.addAttribute("recommendBlogs", blogService.listRecommendBlogTop(8));
        return "index";
    }


    @PostMapping("/search")
    public String search(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                         @RequestParam String query, Model model) {
        model.addAttribute("page", blogService.listBlog("%"+query+"%", pageable));
        model.addAttribute("query", query);
        return "search";
    }

    @GetMapping("/blog/{id}")
    public String blog(@PathVariable("id") Long id,Model model) {
        try {
            Blog blog = blogService.getAndConvert(id);
            if (blog == null) {
                return "error/404";
            }
            model.addAttribute("blog", blog);
            // Add recently published posts list (exclude current post)
            List<Blog> recentBlogs = blogService.listRecommendBlogTop(10);
            recentBlogs.removeIf(b -> b.getId().equals(id));
            if (recentBlogs.size() > 8) {
                recentBlogs = recentBlogs.subList(0, 8);
            }
            model.addAttribute("recentBlogs", recentBlogs);
            return "blog";
        } catch (NotFoundException e) {
            return "error/404";
        } catch (Exception e) {
            // Log error and return error page
            logger.error("Error loading blog with id: {}", id, e);
            return "error/error";
        }
    }

    @GetMapping("/footer/newblog")
    public String newblogs(Model model) {
        model.addAttribute("newblogs", blogService.listRecommendBlogTop(3));
        return "_fragments :: newblogList";
    }

    /**
     * Get blog publishing statistics (for homepage heatmap)
     * Return format: [["yyyy-MM-dd", count], ...]
     */
    @GetMapping("/api/stats/blog-count")
    @ResponseBody
    public ResponseEntity<List<Object[]>> getBlogCountStats() {
        java.util.Map<String, Integer> stats = blogService.getBlogStatsByDate();
        List<Object[]> result = new java.util.ArrayList<>();
        for (java.util.Map.Entry<String, Integer> entry : stats.entrySet()) {
            result.add(new Object[]{entry.getKey(), entry.getValue()});
        }
        return ResponseEntity.ok(result);
    }

}
