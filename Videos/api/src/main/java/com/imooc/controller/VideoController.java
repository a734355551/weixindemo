package com.imooc.controller;

import com.imocc.service.BgmService;
import com.imooc.utils.IMoocJSONResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@RestController
@Api(value="视频相关业务接口", tags= {"视频相关业务的controller"})
@RequestMapping("/video")
public class VideoController extends BasicController {
	
	@Autowired
	private BgmService bgmService;


	@ApiOperation(value="上传视频", notes="上传视频的接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name="userId", value="用户id", required=true,
					dataType="String", paramType="form"),
			@ApiImplicitParam(name="bgmId", value="背景音乐id",
					dataType="String", paramType="form"),
			@ApiImplicitParam(name="videoSeconds", value="背景音乐播放长度",
					dataType="String", paramType="form"),
			@ApiImplicitParam(name="videoWidth", value="视频宽度",
					dataType="String", paramType="form"),
			@ApiImplicitParam(name="videoHeight", value="视频高度",
					dataType="String", paramType="form"),
			@ApiImplicitParam(name="desc", value="视频描述",
					dataType="String", paramType="form")
	})
	@PostMapping(value = "/upload",headers = "content-type=multipart/form-data")
	public IMoocJSONResult upload(String userId,
								  String bgmId,Double videoSeconds, Integer videoWidth, Integer videoHeight,
								  String desc,@ApiParam(value = "短视频",required = true) MultipartFile file) throws Exception {

		if(StringUtils.isBlank(userId)){
			return IMoocJSONResult.errorMsg("用户Id不能为空");
		}

		//文件保存的命名空间
		String fileSpace = "F:/douyin_file";
		//保存到数据库中的相对路径
		String uploadPathDB = "/"+userId+"/video";
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		try {
			if(file != null ) {
				String filename = file.getOriginalFilename();
				if (StringUtils.isNotBlank(filename)) {
					//文件上传的最终保存路径
					String finalVideoPath = fileSpace + uploadPathDB + "/" + filename;
					//设置数据库保存的路径
					uploadPathDB += ("/" + filename);
					File outFile = new File(finalVideoPath);
					if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
						//创建父文件夹
						outFile.getParentFile().mkdirs();
					}
					fileOutputStream = new FileOutputStream(outFile);
					inputStream = file.getInputStream();
					//将文件输出到文件夹里
					IOUtils.copy(inputStream, fileOutputStream);
				}
			}else {
				return IMoocJSONResult.errorMsg("上传出错");
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			if(null!= fileOutputStream){
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		}

		return IMoocJSONResult.ok();
	}
}
