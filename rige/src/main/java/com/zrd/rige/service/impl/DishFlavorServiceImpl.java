package com.zrd.rige.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zrd.rige.entity.DishFlavor;
import com.zrd.rige.mapper.DishFlavorMapper;
import com.zrd.rige.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
