package com.tensquare.article.service;

import com.tensquare.article.pojo.Comment;
import com.tensquare.article.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import util.IdWorker;

import java.util.Date;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private MongoTemplate mongoTemplate;


    public List<Comment> findAll() {
        List<Comment> list = commentRepository.findAll();
        return list;
    }

    public Comment findById(String commentId) {
        Comment comment = commentRepository.findById(commentId).get();
        return comment;
    }

    public void save(Comment comment) {
        String id = idWorker.nextId()+"";
        comment.set_id(id);
        comment.setThumbup(0);
        comment.setPublishdate(new Date());
        commentRepository.save(comment);

    }

    public void updateById(Comment comment) {
        commentRepository.save(comment);
    }


    public void deleteById(String commentId) {
        commentRepository.deleteById(commentId);
    }

    public List<Comment> findByAticleId(String articleId) {
        List<Comment> list = commentRepository.findByArticleid(articleId);
        return list;
    }

    public void thumbup(String commentId) {
        /*Comment comment = commentRepository.findById(commentId).get();
        comment.setThumbup(comment.getThumbup()+1);
        commentRepository.save(comment);*/

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(commentId));
        Update update = new Update();
        update.inc("thumbup",1);
        mongoTemplate.updateFirst(query,update,"comment");


    }
}
