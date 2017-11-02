package com.fengjr.sauron.test.web.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fengjr.redis.client.RedisClusterClient;
import com.fengjr.redis.client.RedisClusterClientFactory;
import com.fengjr.sauron.test.web.TestHttpClient;
import com.fengjr.sauron.test.web.dao.UserMapper;
import com.fengjr.sauron.test.web.domain.User;
import com.fengjr.sauron.test.web.service.CommonService;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年12月18日 上午10:30:51
 * 
 */

@Controller
@RequestMapping("/common")
public class CommonInfoController {

	public RedisClusterClient redisClient = RedisClusterClientFactory.getRedisClusterClient("redisCache");

	@Resource
	CommonService commonService;

	@Resource
	UserMapper userMapper;

	@ResponseBody
	@RequestMapping(value = "/test", method = { RequestMethod.GET, RequestMethod.POST })
	public String test() {

		System.out.println("11111111111111111111" + redisClient);

		if (redisClient == null) {
			redisClient = RedisClusterClientFactory.getRedisClusterClient("redisCache");
		}

		if (redisClient != null) {

			redisClient.set("wangweitest", "123321", 100);
			System.out.println("2222222222222222222222222");

			Long remove = redisClient.remove("wangweitest");
			System.out.println("3333333333333333333333333");
			commonService.print(remove.toString());
		}

		try {
			commonService.printError("test");
		} catch (Exception e1) {
			e1.printStackTrace();

		}

		System.out.println("444444444444444444444444444444444444");

		List<User> selectAll = userMapper.selectAll();

		System.out.println("555555555555555555555555555555555555");

		for (User user : selectAll) {
			System.out.println(user.getName());
		}

		List<User> selectByPage = userMapper.selectByPage(0, 3);

		for (User user : selectByPage) {
			System.out.println(user.getUserId());
		}

		try {

			TestHttpClient.post();
		} catch (Exception e) {

			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return "success";
	}
}
