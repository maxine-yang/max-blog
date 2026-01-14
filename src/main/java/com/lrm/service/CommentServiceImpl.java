package com.lrm.service;

import com.lrm.dao.CommentRepository;
import com.lrm.po.Comment;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author: maxine yang
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public List<Comment> listCommentByBlogId(Long blogId) {
        Sort sort = Sort.by("createTime");
        List<Comment> comments = commentRepository.findByBlogIdAndParentCommentNull(blogId,sort);
        return eachComment(comments);
    }

    @Transactional
    @Override
    public Comment saveComment(Comment comment) {
        if (comment.getParentComment() != null && comment.getParentComment().getId() != null) {
            Long parentCommentId = comment.getParentComment().getId();
            if (parentCommentId != -1) {
                comment.setParentComment(commentRepository.findById(parentCommentId).orElse(null));
            } else {
                comment.setParentComment(null);
            }
        } else {
            comment.setParentComment(null);
        }
        comment.setCreateTime(new Date());
        return commentRepository.save(comment);
    }


    /**
     * Loop through each top-level comment node
     * @param comments
     * @return
     */
    private List<Comment> eachComment(List<Comment> comments) {
        List<Comment> commentsView = new ArrayList<>();
        for (Comment comment : comments) {
            Comment c = new Comment();
            BeanUtils.copyProperties(comment,c);
            commentsView.add(c);
        }
        // Merge all levels of comment children into first-level children collection
        combineChildren(commentsView);
        return commentsView;
    }

    /**
     *
     * @param comments Root nodes, collection of objects where blog is not null
     * @return
     */
    private void combineChildren(List<Comment> comments) {

        for (Comment comment : comments) {
            List<Comment> replys1 = comment.getReplyComments();
            for(Comment reply1 : replys1) {
                // Loop recursively to find children, store in tempReplys
                recursively(reply1);
            }
            // Modify top-level node's reply collection to iteratively processed collection
            comment.setReplyComments(tempReplys);
            // Clear temporary storage
            tempReplys = new ArrayList<>();
        }
    }

    // Collection to store all children found through iteration
    private List<Comment> tempReplys = new ArrayList<>();
    /**
     * Recursive iteration, peeling the onion
     * @param comment Object being iterated
     * @return
     */
    private void recursively(Comment comment) {
        tempReplys.add(comment);// Add top node to temporary storage collection
        if (comment.getReplyComments().size()>0) {
            List<Comment> replys = comment.getReplyComments();
            for (Comment reply : replys) {
                tempReplys.add(reply);
                if (reply.getReplyComments().size()>0) {
                    recursively(reply);
                }
            }
        }
    }
}
