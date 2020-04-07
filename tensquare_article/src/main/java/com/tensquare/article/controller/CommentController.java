package com.tensquare.article.controller;


import com.tensquare.article.pojo.Comment;
import com.tensquare.article.service.CommentService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "thumbup/{commentId}",method = RequestMethod.PUT)
    public Result thumbup(@PathVariable String commentId){
        String userId="123";
        Object flag = redisTemplate.opsForValue().get("thumbup_" + userId + "_" + commentId);
        if (flag==null){
            commentService.thumbup(commentId);
            redisTemplate.opsForValue().set("thumbup_" + userId + "_" + commentId,1);
            return new Result(true,StatusCode.OK,"点赞成功");
        }else {
            return new Result(false,StatusCode.REPERROR,"不能重复点赞");
        }



    }


    @RequestMapping(value = "/article/{articleId}",method = RequestMethod.GET)
    public Result findByArticleId(@PathVariable String articleId){
        List<Comment> list =commentService.findByAticleId(articleId);
        return new Result(true,StatusCode.OK,"查询成功",list);

    }


    @RequestMapping(method = RequestMethod.GET)
    public Result findAll(){
        List<Comment> list =   commentService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",list);
    }

    @RequestMapping(value = "/{commentId}",method = RequestMethod.GET)
    public Result findById(@PathVariable String commentId){
        Comment comment = commentService.findById(commentId);
        return new Result(true,StatusCode.OK,"查询成功",comment);

    }


    @RequestMapping(method = RequestMethod.POST)
    public Result save(@RequestBody Comment comment){
        commentService.save(comment);
        return new Result(true,StatusCode.OK,"新增成功");
    }

    @RequestMapping(value = "/{commentId}",method = RequestMethod.PUT)
    public Result updateById(@PathVariable String commentId,@RequestBody Comment comment){
        comment.set_id(commentId);
        commentService.updateById(comment);
        return new Result(true,StatusCode.OK,"修改成功");

    }

    @RequestMapping(value = "/{commentId}",method = RequestMethod.DELETE)
    public Result deleteById(@PathVariable String commentId){
        commentService.deleteById(commentId);
        return new Result(true,StatusCode.OK,"删除成功");
    }


}
