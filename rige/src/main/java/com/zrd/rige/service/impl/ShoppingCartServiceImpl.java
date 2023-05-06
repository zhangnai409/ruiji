package com.zrd.rige.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zrd.rige.entity.ShoppingCart;
import com.zrd.rige.mapper.ShoppingCartMapper;
import com.zrd.rige.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
