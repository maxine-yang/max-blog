package com.lrm.web.admin;

import com.lrm.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * Author: maxine yang
 */
@Controller
@RequestMapping("/admin")
public class AdminController extends BaseAdminController {

    @Autowired
    private BlogService blogService;

    @GetMapping("/index")
    public String index(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/admin";
        }
        model.addAttribute("user", session.getAttribute("user"));
        return "admin/index";
    }

    /**
     * Get blog publishing statistics (for heatmap)
     * Return format: {"2026-01-10": 2, "2026-01-11": 1, ...}
     */
    @GetMapping("/api/blog-stats")
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> getBlogStats() {
        Map<String, Integer> stats = blogService.getBlogStatsByDate();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get blog publishing statistics (ECharts format)
     * Return format: [["2026-01-10", 2], ["2026-01-11", 1], ...]
     */
    @GetMapping("/api/blog-stats-echarts")
    @ResponseBody
    public ResponseEntity<List<Object[]>> getBlogStatsForECharts() {
        Map<String, Integer> stats = blogService.getBlogStatsByDate();
        List<Object[]> result = new java.util.ArrayList<>();
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            result.add(new Object[]{entry.getKey(), entry.getValue()});
        }
        return ResponseEntity.ok(result);
    }
}
