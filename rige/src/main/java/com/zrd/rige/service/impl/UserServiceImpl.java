package com.zrd.rige.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zrd.rige.entity.User;
import com.zrd.rige.mapper.UserMapper;
import com.zrd.rige.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
