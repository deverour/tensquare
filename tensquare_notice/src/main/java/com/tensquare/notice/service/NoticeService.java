package com.tensquare.notice.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.tensquare.notice.client.ArticleClient;
import com.tensquare.notice.client.UserClient;
import com.tensquare.notice.dao.NoticeDao;
import com.tensquare.notice.dao.NoticeFreshDao;
import com.tensquare.notice.pojo.Notice;
import com.tensquare.notice.pojo.NoticeFresh;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;

import java.util.Date;
import java.util.HashMap;
import java.util.List;


@Service
public class NoticeService {

    @Autowired
    private NoticeDao noticeDao;

    @Autowired
    private NoticeFreshDao noticeFreshDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private ArticleClient articleClient;

    @Autowired
    private UserClient userClient;

    private void getInfo(Notice notice){
        Result userResult = userClient.findById(notice.getOperatorId());
        HashMap usermap =(HashMap)userResult.getData();
        notice.setOperatorName(usermap.get("nickname").toString());


        Result articleResult = articleClient.findById(notice.getTargetId());
        HashMap articlemap =(HashMap)articleResult.getData();
        notice.setTargetName(articlemap.get("title").toString());


    }


    public Notice findById(String id) {
        Notice notice = noticeDao.selectById(id);
        getInfo(notice);
        return notice;
    }


    public Page<Notice> findByPage(Notice notice, Integer page, Integer size) {
        //封装分页对象
        Page<Notice> pageData = new Page<>(page, size);

        //执行分页查询
        List<Notice> noticeList = noticeDao.selectPage(pageData, new EntityWrapper<>(notice));

        for (Notice n:noticeList){
            getInfo(n);
        }

        //设置结果集到分页对象中
        pageData.setRecords(noticeList);

        //返回
        return pageData;
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(Notice notice) {
        //0未读
        System.out.println("新增一条通知");
        notice.setState("0");
        notice.setCreatetime(new Date());

        String id = idWorker.nextId()+"";
        notice.setId(id);
        noticeDao.insert(notice);


        /*NoticeFresh noticeFresh = new NoticeFresh();
        noticeFresh.setNoticeId(id);
        noticeFresh.setUserId(notice.getReceiverId());
        noticeFreshDao.insert(noticeFresh);*/

    }

    public void updataById(Notice notice) {
        noticeDao.updateById(notice);
    }

    public Page<NoticeFresh> freshPage(String userId, Integer page, Integer size) {
        NoticeFresh noticeFresh = new NoticeFresh();
        noticeFresh.setUserId(userId);
        Page<NoticeFresh> pageData = new Page<>(page,size);

        List<NoticeFresh> noticeFreshList = noticeFreshDao.selectPage(pageData, new EntityWrapper<>(noticeFresh));

        pageData.setRecords(noticeFreshList);

        return pageData;

    }

    public void freshDelete(NoticeFresh noticeFresh) {
        noticeFreshDao.delete(new EntityWrapper<>(noticeFresh));
    }
}
