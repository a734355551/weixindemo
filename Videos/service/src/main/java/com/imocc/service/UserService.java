package com.imocc.service;

import com.imooc.pojo.Users;

public interface UserService {
	
	/**
	 * @Description: 判断用户名是否存在
	 */
	public boolean queryUsernameIsExist(String username);
	
	/**
	 * @Description: 保存用户(用户注册)
	 */
	public void saveUser(Users user);


	/**
	 * @Description: 用户登录，根据用户名和密码查询用户
	 */
	public Users queryUserForLogin(String username, String password);

	/**
	 * 用户修改信息
	 * @author fengzhenting
	 * @date 2019/6/6 17:22
	 * @return void
	 */
	public void updateUserInfo(Users user);

	/**
	 * 查询用户信息
	 * @author fengzhenting
	 * @date 2019/6/10 11:31
	 * @return
	 */
	public Users queryUserInfo(String userId);
}
