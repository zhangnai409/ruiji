package com.zrd.rige.dto;


import com.zrd.rige.entity.OrderDetail;
import com.zrd.rige.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
