package com.zrd.rige.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zrd.rige.entity.OrderDetail;
import com.zrd.rige.mapper.OrderDetailMapper;
import com.zrd.rige.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
