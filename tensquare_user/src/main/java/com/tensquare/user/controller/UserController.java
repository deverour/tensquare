package com.tensquare.user.controller;


import com.tensquare.user.pojo.User;
import com.tensquare.user.service.UserService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value="/{userId}",method = RequestMethod.GET)
    public Result selectById(@PathVariable("userId") String userId) {
        User user = userService.selectById(userId);
        return new Result(true, StatusCode.OK, "查询成功", user);
    }

}

