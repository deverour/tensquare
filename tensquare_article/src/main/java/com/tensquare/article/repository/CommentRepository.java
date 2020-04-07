package com.tensquare.article.repository;

import com.tensquare.article.pojo.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface CommentRepository extends MongoRepository<Comment,String> {

    List<Comment> findByArticleid(String articleId);

    List<Comment> findByPublishdateAndThumbup(Date date,Integer thumbup);

    List<Comment> findByUseridOrderByPublishdateDesc(String userid);
}