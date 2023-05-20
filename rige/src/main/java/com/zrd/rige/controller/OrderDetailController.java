package com.zrd.rige.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zrd.rige.entity.OrderDetail;
import com.zrd.rige.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单明细
 */
@RestController
@RequestMapping("/orderDetail")
@Slf4j
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;


}
