package com.tensquare.article.controller;


import com.baomidou.mybatisplus.plugins.Page;
import com.tensquare.article.pojo.Article;
import com.tensquare.article.service.ArticleService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("article")
@CrossOrigin
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "thumbup/{articleId}",method = RequestMethod.PUT)
    public Result thumbup(@PathVariable String articleId){
        System.out.println("thumbup");
        String userId="4";
        String key = "thumbup_article_" + userId + "_" + articleId;
        Object flag =redisTemplate.opsForValue().get(key);
        if (flag == null){
            articleService.thumbup(articleId,userId);
            redisTemplate.opsForValue().set(key,1);
            return new Result(true,StatusCode.OK,"点赞成功");
        }else {
            return new Result(false,StatusCode.REPERROR,"不能重复点赞");
        }



    }



    @RequestMapping(value = "subscribe",method = RequestMethod.POST)
    public Result subscribe(@RequestBody Map map){
        System.out.println("subscribe");
        Boolean flag = articleService.subscribe(map.get("articleId").toString(),map.get("userId").toString());
        System.out.println("subscribe2");
        if (flag ==true){
            return new Result(true,StatusCode.OK,"订阅成功");

        }else {
            return new Result(true,StatusCode.OK,"取消订阅成功");
        }
        
    }

    //测试公共异常处理
    @RequestMapping(value = "exception",method = RequestMethod.GET)
    public Result test(){
        int a= 1/0;
        return null;
    }

    @RequestMapping(value = "search/{page}/{size}",method = RequestMethod.POST)
    public Result findByPage(@PathVariable Integer page,@PathVariable Integer size,@RequestBody Map<String,Object> map ){
        Page<Article> pageData = articleService.findByPage(map,page,size);
        PageResult<Article> pageResult = new PageResult<>(
                pageData.getTotal(),pageData.getRecords()
        );



        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    @RequestMapping(value = "{articleId}",method = RequestMethod.DELETE)
    public Result deleteById(@PathVariable String articleId){
        articleService.deleteById(articleId);
        return new Result(true,StatusCode.OK,"删除成功");
    }


    @RequestMapping(value = "{articleId}",method = RequestMethod.PUT)
    public Result updateById(@PathVariable String articleId,@RequestBody Article article){
        article.setId(articleId);
        articleService.updateById(article);

        return new Result(true,StatusCode.OK,"修改成功");


    }

    @RequestMapping(method = RequestMethod.POST)
    public Result save(@RequestBody Article article){
        System.out.println("save");
        articleService.save(article);
        return new Result(true,StatusCode.OK,"新增成功");
    }

    @RequestMapping(value = "{articleId}",method = RequestMethod.GET)
    public Result findById(@PathVariable String articleId){
       Article article = articleService.findById(articleId);
       return new Result(true,StatusCode.OK,"查询成功",article);

    }


    @RequestMapping(method = RequestMethod.GET)
    public Result findAll(){
        List<Article> list = articleService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",list);
    }
}
