package com.imooc.controller;

import com.imocc.service.BgmService;
import com.imooc.utils.IMoocJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value="背景音乐业务接口", tags= {"背景音乐业务的controller"})
@RequestMapping("/bgm")
public class BgmController extends BasicController {
	
	@Autowired
	private BgmService bgmService;


	@ApiOperation(value="获取背景音乐列表", notes="查询背景音乐列表的接口")
	@PostMapping("/list")
	public IMoocJSONResult query() throws Exception {

		return IMoocJSONResult.ok(bgmService.queryBgmList());
	}
}
