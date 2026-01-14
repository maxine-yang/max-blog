package com.lrm.service;

import com.lrm.NotFoundException;
import com.lrm.dao.BlogRepository;
import com.lrm.po.Blog;
import com.lrm.po.Type;
import com.lrm.util.MarkdownUtils;
import com.lrm.util.MyBeanUtils;
import com.lrm.vo.BlogQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.*;
import java.util.*;

/**
 * Author: maxine yang
 */
@Service
public class BlogServiceImpl implements BlogService {


    @Autowired
    private BlogRepository blogRepository;

    @Override
    public Blog getBlog(Long id) {
        Blog blog = blogRepository.findById(id).orElse(null);
        if (blog != null) {
            // Extract first image from content if firstPicture is not set
            if ((blog.getFirstPicture() == null || blog.getFirstPicture().trim().isEmpty()) 
                && blog.getContent() != null && !blog.getContent().trim().isEmpty()) {
                String firstImage = MarkdownUtils.extractFirstImage(blog.getContent());
                if (firstImage != null && !firstImage.isEmpty()) {
                    blog.setFirstPicture(firstImage);
                }
            }
        }
        return blog;
    }

    @Transactional
    @Override
    public Blog getAndConvert(Long id) {
        Blog blog = blogRepository.findById(id).orElse(null);
        if (blog == null) {
            throw new NotFoundException("Blog not found");
        }
        
        // Force lazy-loaded fields to load (especially content)
        String originalContent = null;
        try {
            originalContent = blog.getContent(); // Trigger lazy loading of content
        } catch (Exception e) {
            // If getting content fails, set to empty string
            originalContent = "";
        }
        
        Blog b = new Blog();
        BeanUtils.copyProperties(blog,b);
        // Ensure related objects are properly set
        b.setUser(blog.getUser());
        b.setType(blog.getType());
        b.setTags(blog.getTags() != null ? blog.getTags() : new ArrayList<>());
        b.setComments(blog.getComments() != null ? blog.getComments() : new ArrayList<>());
        
        // Ensure title is not null
        if (b.getTitle() == null) {
            b.setTitle("");
        }
        
        // Use original queried content (lazy loading already triggered)
        String content = originalContent;
        if (content != null && !content.isEmpty()) {
            try {
                b.setContent(MarkdownUtils.markdownToHtmlExtensions(content));
            } catch (Exception e) {
                // If Markdown conversion fails, use original content
                b.setContent(content);
            }
        } else {
            b.setContent("");
        }
        
        try {
            blogRepository.updateViews(id);
        } catch (Exception e) {
            // If updating views fails, log error but don't affect return result
            // Optionally log the error
        }
        return b;
    }


    @Override
    public Page<Blog> listBlog(Pageable pageable, BlogQuery blog) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (!"".equals(blog.getTitle()) && blog.getTitle() != null) {
                    predicates.add(cb.like(root.<String>get("title"), "%"+blog.getTitle()+"%"));
                }
                if (blog.getTypeId() != null) {
                    predicates.add(cb.equal(root.<Type>get("type").get("id"), blog.getTypeId()));
                }
                if (blog.isRecommend()) {
                    predicates.add(cb.equal(root.<Boolean>get("recommend"), blog.isRecommend()));
                }
                cq.where(predicates.toArray(new Predicate[predicates.size()]));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        },pageable);
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    @Override
    public Page<Blog> listBlog(Long tagId, Pageable pageable) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                Join join = root.join("tags");
                return cb.equal(join.get("id"),tagId);
            }
        },pageable);
    }

    @Override
    public Page<Blog> listBlog(String query, Pageable pageable) {
        return blogRepository.findByQuery(query,pageable);
    }

    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC,"updateTime");
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, size, sort);
        return blogRepository.findTop(pageable);
    }

    @Override
    public Map<String, List<Blog>> archiveBlog() {
        List<String> years = blogRepository.findGroupYear();
        Map<String, List<Blog>> map = new HashMap<>();
        for (String year : years) {
            map.put(year, blogRepository.findByYear(year));
        }
        return map;
    }

    @Override
    public Long countBlog() {
        return blogRepository.count();
    }


    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        // Extract first image from content if firstPicture is not set
        if ((blog.getFirstPicture() == null || blog.getFirstPicture().trim().isEmpty()) 
            && blog.getContent() != null && !blog.getContent().trim().isEmpty()) {
            String firstImage = com.lrm.util.MarkdownUtils.extractFirstImage(blog.getContent());
            if (firstImage != null && !firstImage.isEmpty()) {
                blog.setFirstPicture(firstImage);
            }
        }
        
        if (blog.getId() == null) {
            // If createTime is already set (from frontend custom time), don't override
            if (blog.getCreateTime() == null) {
                blog.setCreateTime(new Date());
            }
            blog.setUpdateTime(new Date());
            blog.setViews(0);
        } else {
            blog.setUpdateTime(new Date());
        }
        return blogRepository.save(blog);
    }

    @Transactional
    @Override
    public Blog updateBlog(Long id, Blog blog) {
        Blog b = blogRepository.findById(id).orElse(null);
        if (b == null) {
            throw new NotFoundException("Blog not found");
        }
        // Preserve original createTime before copying properties
        Date originalCreateTime = b.getCreateTime();
        BeanUtils.copyProperties(blog,b, MyBeanUtils.getNullPropertyNames(blog));
        // If createTime was not set in the blog object (user didn't change publish time), 
        // preserve the original createTime. Otherwise, use the new createTime from blog object.
        if (blog.getCreateTime() == null) {
            b.setCreateTime(originalCreateTime);
        }
        
        // Extract first image from content if firstPicture is not set
        if ((b.getFirstPicture() == null || b.getFirstPicture().trim().isEmpty()) 
            && b.getContent() != null && !b.getContent().trim().isEmpty()) {
            String firstImage = com.lrm.util.MarkdownUtils.extractFirstImage(b.getContent());
            if (firstImage != null && !firstImage.isEmpty()) {
                b.setFirstPicture(firstImage);
            }
        }
        
        b.setUpdateTime(new Date());
        return blogRepository.save(b);
    }

    @Transactional
    @Override
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }

    @Override
    public Map<String, Integer> getBlogStatsByDate() {
        List<Object[]> results = blogRepository.findBlogStatsByDate();
        Map<String, Integer> stats = new HashMap<>();
        for (Object[] result : results) {
            String date = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            stats.put(date, count.intValue());
        }
        return stats;
    }
}
