package com.tensquare.article.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.tensquare.article.client.NoticeClient;
import com.tensquare.article.dao.ArticleDao;
import com.tensquare.article.pojo.Article;
import com.tensquare.article.pojo.Notice;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import util.IdWorker;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ArticleService {

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private NoticeClient noticeClient;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public List<Article> findAll() {
        return articleDao.selectList(null);
    }

    public Article findById(String articleId) {
        return articleDao.selectById(articleId);
    }

    public void save(Article article) {
        System.out.println("article.getUserid()"+article.getUserid());
        String userId = "3";
        article.setUserid(userId);
        String id = idWorker.nextId()+"";
        article.setId(id);
        article.setVisits(0);
        article.setThumbup(0);
        article.setComment(0);
        articleDao.insert(article);

        String authorKey = "article_author_" + article.getUserid();
        Set<String> set = redisTemplate.boundSetOps(authorKey).members();
        for (String uid : set){
            Notice notice = new Notice();
            notice.setReceiverId(uid);
            notice.setOperatorId(userId);
            notice.setAction("publish");
            notice.setTargetType("article");
            notice.setTargetId(id);
            notice.setType("sys");
            noticeClient.save(notice);

        }

        rabbitTemplate.convertAndSend("article_subscribe",userId,id);

    }

    public void updateById(Article article) {
        articleDao.updateById(article);

      /*  EntityWrapper<Article> Wrapper = new EntityWrapper<>();
        Wrapper.eq("id",article.getId());

        articleDao.update(article, Wrapper);*/
    }

    public void deleteById(String articleId) {
        articleDao.deleteById(articleId);
    }

    public Page<Article> findByPage(Map<String,Object> map, Integer page, Integer size) {
        EntityWrapper<Article> wrapper = new EntityWrapper<>();
        Set<String> keySet = map.keySet();
        for (String key : keySet){
          /*  if (map.get(key) != null){
                wrapper.eq(key,map.get(key));
            } */
            wrapper.eq(map.get(key) != null,key,map.get(key));
        }
        Page<Article> pageData = new Page<>(page,size);
        List<Article> list = articleDao.selectPage(pageData,wrapper);
        pageData.setRecords(list);
        return pageData;
    }

    public Boolean subscribe(String artocleId, String userId) {
        String authorId = articleDao.selectById(artocleId).getUserid();
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate.getConnectionFactory());
        DirectExchange exchange = new DirectExchange("article_subscribe");
        rabbitAdmin.declareExchange(exchange);
        Queue queue=new Queue("article_subscribe_"+userId,true);
        Binding binding = BindingBuilder.bind(queue).to(exchange).with((authorId));


        String userKey = "article_subscribe_" + userId;
        String authorKey = "article_author_" + authorId;
        Boolean flag = redisTemplate.boundSetOps(userKey).isMember(authorId);
        if (flag ==true){
            redisTemplate.boundSetOps(userKey).remove(authorId);
            redisTemplate.boundSetOps(authorKey).remove(userId);
            rabbitAdmin.removeBinding(binding);
            return false;

        }else {
            redisTemplate.boundSetOps(userKey).add(authorId);
            redisTemplate.boundSetOps(authorKey).add(userId);
            rabbitAdmin.declareQueue(queue);
            rabbitAdmin.declareBinding(binding);

            return true;
        }

    }

    public void thumbup(String articleId,String userId) {

        Article article = articleDao.selectById(articleId);
        article.setThumbup(article.getThumbup()+1);
        articleDao.updateById(article);

        Notice notice = new Notice();
        notice.setReceiverId(article.getUserid());
        notice.setOperatorId(userId);
        notice.setAction("publish");
        notice.setTargetType("article");
        notice.setTargetId(articleId);
        notice.setTargetType("user");
        noticeClient.save(notice);

    }
}
