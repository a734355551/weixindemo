package com.imooc.controller;

import com.imocc.enums.VideoStatusEnum;
import com.imocc.service.BgmService;
import com.imocc.service.VideoService;
import com.imooc.pojo.Bgm;
import com.imooc.pojo.Videos;
import com.imooc.utils.FetchVideoCover;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MergeVideoMp3;
import com.imooc.utils.PagedResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@RestController
@Api(value="视频相关业务接口", tags= {"视频相关业务的controller"})
@RequestMapping("/video")
public class VideoController extends BasicController {
	
	@Autowired
	private VideoService videoService;
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
		String coverPathDB = "/"+userId+"/video";
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		// 文件上传的最终保存路径
		String finalVideoPath = "";
		try {
			if(file != null ) {
				String filename = file.getOriginalFilename();
				//abc.mp4
				String fileNamePrefix = filename.split("\\.")[0];
				if (StringUtils.isNotBlank(filename)) {
					//文件上传的最终保存路径
					finalVideoPath = fileSpace + uploadPathDB + "/" + filename;
					//设置数据库保存的路径
					uploadPathDB += ("/" + filename);
					coverPathDB = coverPathDB + "/" + fileNamePrefix + ".jpg";
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

		// 判断bgmId是否为空，如果不为空，
		// 那就查询bgm的信息，并且合并视频，生产新的视频
		if (StringUtils.isNotBlank(bgmId)) {
			Bgm bgm = bgmService.queryBgmById(bgmId);
			String mp3InputPath = FILE_SPACE + bgm.getPath();

			MergeVideoMp3 tool = new MergeVideoMp3(FFMPEG_EXE);
			String videoInputPath = finalVideoPath;

			String videoOutputName = UUID.randomUUID().toString() + ".mp4";
			uploadPathDB = "/" + userId + "/video" + "/" + videoOutputName;
			finalVideoPath = FILE_SPACE + uploadPathDB;
			tool.convertor(videoInputPath, mp3InputPath, videoSeconds, finalVideoPath);
		}
		System.out.println("uploadPathDB=" + uploadPathDB);
		System.out.println("finalVideoPath=" + finalVideoPath);

		// 对视频进行截图
		FetchVideoCover videoInfo = new FetchVideoCover(FFMPEG_EXE);
		videoInfo.getCover(finalVideoPath, FILE_SPACE + coverPathDB);

		// 保存视频信息到数据库
		Videos video = new Videos();
		video.setAudioId(bgmId);
		video.setUserId(userId);
		video.setVideoSeconds(Double.valueOf(videoSeconds).floatValue());
		video.setVideoHeight(videoHeight);
		video.setVideoWidth(videoWidth);
		video.setVideoDesc(desc);
		video.setVideoPath(uploadPathDB);
		video.setCoverPath(coverPathDB);
		video.setStatus(VideoStatusEnum.SUCCESS.value);
		video.setCreateTime(new Date());
		String videoId = videoService.saveVideo(video);

		return IMoocJSONResult.ok(videoId);
	}


	@ApiOperation(value="上传封面", notes="上传封面的接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name="userId", value="用户id", required=true,
					dataType="String", paramType="form"),
			@ApiImplicitParam(name="videoId", value="视频主键id", required=true,
					dataType="String", paramType="form")
	})
	@PostMapping(value="/uploadCover", headers="content-type=multipart/form-data")
	public IMoocJSONResult uploadCover(String userId,
									   String videoId,
									   @ApiParam(value="视频封面", required=true)
											   MultipartFile file) throws Exception {

		if (StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)) {
			return IMoocJSONResult.errorMsg("视频主键id和用户id不能为空...");
		}

		// 保存到数据库中的相对路径
		String uploadPathDB = "/" + userId + "/video";

		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		// 文件上传的最终保存路径
		String finalCoverPath = "";
		try {
			if (file != null) {

				String fileName = file.getOriginalFilename();
				if (StringUtils.isNotBlank(fileName)) {

					finalCoverPath = FILE_SPACE + uploadPathDB + "/" + fileName;
					// 设置数据库保存的路径
					uploadPathDB += ("/" + fileName);

					File outFile = new File(finalCoverPath);
					if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
						// 创建父文件夹
						outFile.getParentFile().mkdirs();
					}

					fileOutputStream = new FileOutputStream(outFile);
					inputStream = file.getInputStream();
					IOUtils.copy(inputStream, fileOutputStream);
				}

			} else {
				return IMoocJSONResult.errorMsg("上传出错...");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return IMoocJSONResult.errorMsg("上传出错...");
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		}

		videoService.updateVideo(videoId, uploadPathDB);

		return IMoocJSONResult.ok();
	}


	/**
	 * 分页查询或者搜索视屏列表
	 * @author fengzhenting
	 * @date 2019/7/3 22:48
	 * @param video
	 * @param isSaveRecord 1:需要保存  0不需要保存 或者为空的时候
	 * @param page
	 * @return com.imooc.utils.IMoocJSONResult
	 */
	@PostMapping(value="/showAll")
	public IMoocJSONResult showAll(@RequestBody  Videos video, Integer isSaveRecord, Integer page, Integer pageSize) throws Exception {

		if (page == null) {
			page = 1;
		}

		if (pageSize == null) {
			pageSize = PAGE_SIZE;
		}

		PagedResult result = videoService.getAllVideos(video, isSaveRecord, page, pageSize);
		return IMoocJSONResult.ok(result);
	}

	/**
	 * 热搜词
	 */
	@PostMapping(value="/hot")
	public IMoocJSONResult hot() throws Exception {
		return IMoocJSONResult.ok(videoService.getHotwords());
	}
}
