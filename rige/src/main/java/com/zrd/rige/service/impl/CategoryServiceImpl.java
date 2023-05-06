package com.zrd.rige.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zrd.rige.common.CustomException;
import com.zrd.rige.entity.Category;
import com.zrd.rige.entity.Dish;
import com.zrd.rige.entity.Setmeal;
import com.zrd.rige.mapper.CategoryMapper;
import com.zrd.rige.service.CategoryService;
import com.zrd.rige.service.DishService;
import com.zrd.rige.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        // 查询当前分类是否关联了菜品，若已经关联，抛出一个业务异常
        if(count1 > 0){
            // 已经关联菜品，抛出业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        LambdaQueryWrapper<Setmeal> setmealServiceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件，根据分类id进行查询
        setmealServiceLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count();
        // 查询当前分类是否关联了套餐，若已经关联，抛出一个业务异常
        if(count2 > 0){
            // 已经关联套餐，抛出业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        // 正常删除分类
        super.removeById(id);
    }
}
