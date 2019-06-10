package com.imooc.controller;

import com.imocc.service.UserService;
import com.imooc.pojo.Users;
import com.imooc.utils.IMoocJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@RestController
@Api(value="用户相关业务的接口", tags= {"用户相关业务的controller"})
@RequestMapping("/user")
public class UserController extends BasicController {
	
	@Autowired
	private UserService userService;

	@ApiOperation(value="用户上传头像", notes="用户上传头像的接口")
	@ApiImplicitParam(name="userId", value="用户id", required=true,
						dataType="String", paramType="query")
	@PostMapping("/uploadFace")
	public IMoocJSONResult logout(String userId, @RequestParam("file") MultipartFile[] files) throws Exception {

		if(StringUtils.isBlank(userId)){
			return IMoocJSONResult.errorMsg("用户Id不能为空");
		}

		//文件保存的命名空间
		String fileSpace = "F:/douyin_file";
		//保存到数据库中的相对路径
		String uploadPathDB = "/"+userId+"/face";
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		try {
			if(files != null && files.length >0) {
				String filename = files[0].getOriginalFilename();
				if (StringUtils.isNotBlank(filename)) {
					//文件上传的最终保存路径
					String finalFacePath = fileSpace + uploadPathDB + "/" + filename;
					//设置数据库保存的路径
					uploadPathDB += ("/" + filename);
					File outFile = new File(finalFacePath);
					if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
						//创建父文件夹
						outFile.getParentFile().mkdirs();
					}
					fileOutputStream = new FileOutputStream(outFile);
					inputStream = files[0].getInputStream();
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
		Users users = new Users();
		users.setId(userId);
		users.setFaceImage(uploadPathDB);
		userService.updateUserInfo(users);
		return IMoocJSONResult.ok(uploadPathDB);
	}
	
}
