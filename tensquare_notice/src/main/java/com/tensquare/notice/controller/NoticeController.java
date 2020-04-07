package com.tensquare.notice.controller;


import com.baomidou.mybatisplus.plugins.Page;
import com.tensquare.notice.pojo.Notice;
import com.tensquare.notice.pojo.NoticeFresh;
import com.tensquare.notice.service.NoticeService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notice")
@CrossOrigin
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @RequestMapping(value = "{id}",method = RequestMethod.GET)
    public Result findById(@PathVariable String id){
        Notice notice = noticeService.findById(id);
        return new Result(true, StatusCode.OK,"查询成功",notice);
    }

    @RequestMapping(value = "/search/{page}/{size}",method = RequestMethod.POST)
    public Result findByList(@RequestBody Notice notice,@PathVariable Integer page,@PathVariable Integer size){
        Page<Notice> pageData=noticeService.findByPage(notice,page,size);
        PageResult<Notice> pageResult = new PageResult<>(pageData.getTotal(),pageData.getRecords());
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Result save(@RequestBody Notice notice){
        noticeService.save(notice);
        return new Result(true,StatusCode.OK,"新增成功");
    }


    @RequestMapping(method = RequestMethod.PUT)
    public Result updateById(@RequestBody Notice notice){
        noticeService.updataById(notice);
        return new Result(true,StatusCode.OK,"修改成功");
    }


    @RequestMapping(value = "fresh/{userId}/{page}/{size}",method = RequestMethod.GET)
    public Result findByUserId(@PathVariable String userId,
                               @PathVariable Integer page,
                               @PathVariable Integer size){
        Page<NoticeFresh> pageData = noticeService.freshPage(userId,page,size);
        PageResult<NoticeFresh> pageResult = new PageResult<>(pageData.getTotal(),pageData.getRecords());

        return new Result(true, StatusCode.OK,"查询成功",pageResult);


    }

    @RequestMapping(value = "/fresh",method = RequestMethod.DELETE)
    public Result freshDelete(@RequestBody NoticeFresh noticeFresh){
        noticeService.freshDelete(noticeFresh);
        return new Result(true,StatusCode.OK,"删除成功");
    }







}
