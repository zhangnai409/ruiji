package com.zrd.rige.controller;

import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zrd.rige.common.BaseContext;
import com.zrd.rige.common.R;
import com.zrd.rige.dto.OrdersDto;
import com.zrd.rige.entity.OrderDetail;
import com.zrd.rige.entity.Orders;
import com.zrd.rige.entity.ShoppingCart;
import com.zrd.rige.service.OrderDetailService;
import com.zrd.rige.service.OrderService;
import com.zrd.rige.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 查询历史订单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize){
        // 获取当前用户id
        Long userId = BaseContext.getCurrentId();
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>(page, pageSize);
        // 条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        // 查询当前用户id订单数据
        queryWrapper.eq(userId != null, Orders::getUserId,userId);
        // 按时间降序
        queryWrapper.orderByDesc(Orders::getOrderTime);

        orderService.page(pageInfo,queryWrapper);

        List<OrdersDto> list = pageInfo.getRecords().stream().map((item)->{
            OrdersDto ordersDto = new OrdersDto();
            // 获取orderId，然后根据这个id，去orderDetail表中查询数据
            Long orderId = item.getId();
            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderDetail::getOrderId,orderId);
            List<OrderDetail> details = orderDetailService.list(wrapper);
            BeanUtils.copyProperties(item,ordersDto);
            ordersDto.setOrderDetails(details);
            return ordersDto;
        }).collect(Collectors.toList());
        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");
        ordersDtoPage.setRecords(list);

        log.info("list:{}",list);
        return R.success(ordersDtoPage);
    }

    /**
     * 订单明细
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, Long number, String beginTime, String endTime){
        // 获取当前id
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>(page,pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Orders::getOrderTime);
        // 订单号
        queryWrapper.eq(number != null,Orders::getId,number);
        // 时间段
        queryWrapper.gt(!StringUtils.isEmpty(beginTime), Orders::getOrderTime,beginTime)
                .gt(!StringUtils.isEmpty(endTime), Orders::getOrderTime, endTime);
        orderService.page(pageInfo, queryWrapper);
        List<OrdersDto> list = pageInfo.getRecords().stream().map((item)->{
            OrdersDto ordersDto = new OrdersDto();
            // 获取 orderId，然后根据这个去orderDetail表中查询数据
            Long orderId = item.getId();
            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderDetail::getOrderId,orderId);
            List<OrderDetail> details = orderDetailService.list(wrapper);
            BeanUtils.copyProperties(item, ordersDto);
            ordersDto.setOrderDetails(details);
            return ordersDto;
        }).collect(Collectors.toList());

        BeanUtils.copyProperties(pageInfo, ordersDtoPage, "records");
        ordersDtoPage.setRecords(list);

        log.info("list:{}",list);
        return R.success(ordersDtoPage);
    }
    /**
     * 再来一单
     * @param map
     * @return
     */
    @PostMapping("/again")
    public R<String> again(@RequestBody Map<String,String> map){
        // 获取 order_id
        Long orderId = Long.valueOf(map.get("id"));
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        // 查询订单的口味细节数据
        queryWrapper.eq(OrderDetail::getOrderId,orderId);
        List<OrderDetail> details = orderDetailService.list(queryWrapper);
        // 获取用户id
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCarts = details.stream().map((item)->{
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(item,shoppingCart);
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());

        // 加入购物车
        shoppingCartService.saveBatch(shoppingCarts);
        return R.success("喜欢就再来一单~");
    }

    /**
     * 修改订单状态
     * @param map
     * @return
     */
    @PutMapping
    public R<String> changeStatus(@RequestBody Map<String, String> map){
        int status = Integer.parseInt(map.get("status"));
        Long orderId = Long.valueOf(map.get("id"));
        log.info("修改订单状态:status={}, id={}",status, orderId);
        LambdaUpdateWrapper<Orders> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Orders::getId, orderId);
        updateWrapper.set(Orders::getStatus, status);
        orderService.update(updateWrapper);
        return R.success("订单状态修改成功");
    }
}
