package com.lrm.web;

import com.lrm.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Author: maxine yang
 */
@Controller
public class AboutShowController {

    @Autowired
    private BlogService blogService;

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    /**
     * Get blog publishing statistics (for heatmap)
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
