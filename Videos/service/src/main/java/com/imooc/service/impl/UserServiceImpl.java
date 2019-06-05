package com.imooc.service.impl;

import com.imocc.service.UserService;
import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UsersMapper userMapper;

	

	//@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public boolean queryUsernameIsExist(String username) {
		
		Users user = new Users();
		user.setUsername(username);
		
		Users result = userMapper.selectOne(user);
		
		return result == null ? false : true;
	}

	//@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void saveUser(Users user) {
		
//		String userId = sid.nextShort();
		String userId = "";
		user.setId(userId);
		userMapper.insert(user);
	}


}

