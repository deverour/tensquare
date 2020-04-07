package com.tensquare.notice.client;

import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "tensquare-user")
public interface UserClient {

    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public Result findById(@PathVariable("userId") String userId);
}