package com.zrd.rige.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zrd.rige.common.R;
import com.zrd.rige.dto.DishDto;
import com.zrd.rige.dto.SetmealDto;
import com.zrd.rige.entity.Category;
import com.zrd.rige.entity.Dish;
import com.zrd.rige.entity.Setmeal;
import com.zrd.rige.entity.SetmealDish;
import com.zrd.rige.service.CategoryService;
import com.zrd.rige.service.DishService;
import com.zrd.rige.service.SetmealDishService;
import com.zrd.rige.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息：{}",setmealDto);

        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        // 分页构造器
        Page<Setmeal> pageInfo = new Page<>();
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件，根据name进行模糊查询
        queryWrapper.like(name != null,Setmeal::getName,name);
        // 添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,queryWrapper);

        // 对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            // 对象拷贝
            BeanUtils.copyProperties(item,setmealDto);
            // 分类id
            Long categoryId = item.getCategoryId();
            // 根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                // 分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids:{}",ids);

        setmealService.removeWithDish(ids);
        if(ids.size() > 1){
            return R.success("套餐数据批量删除成功");
        }
        return R.success("套餐数据删除成功");
    }

    /**
     * 套餐的停售或起售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status, @RequestParam List<Long> ids){
        log.info("status:{}, ids:{}",status,ids);
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Setmeal::getId, ids);
        updateWrapper.set(Setmeal::getStatus, status);
        setmealService.update(updateWrapper);
        if(ids.size() > 1){
            // 套餐批量起售或停售
            return R.success("批量操作成功");
        }
        // 套餐起售或停售
        return R.success("售卖状态修改成功");
    }

    /**
     * 根据id查询套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        Setmeal setmeal = setmealService.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        // 拷贝数据
        BeanUtils.copyProperties(setmeal,setmealDto);
        // 条件构造器
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        // 根据setmeal查询setmealDish
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(list);
        return R.success(setmealDto);
    }
    /**
     * 套餐修改
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<Setmeal> updateWithDish(@RequestBody SetmealDto setmealDto){
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        Long setmealId = setmealDto.getId();
        // 先根据id把setmealDish表中对应的套餐的数据删除
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        setmealDishService.remove(queryWrapper);
        // 然后重新添加
        setmealDishes = setmealDishes.stream().map((item)->{
            // 这个属性没有，需要手动设置一下
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());
        // 更新套餐数据
        setmealService.updateById(setmealDto);
        //更新套餐对应菜品的数据
        setmealDishService.saveBatch(setmealDishes);
        return R.success(setmealDto);
    }

    /**
     * 根据条件查询套餐
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 点击图片查看套餐详情
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    public R<List<DishDto>> showSetmealDish(@PathVariable Long id){
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);

        List<SetmealDish> records = setmealDishService.list(dishLambdaQueryWrapper);
        List<DishDto> dtoList = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long dishId = item.getDishId();
            // 根据菜品id获取具体菜品数据
            Dish dish = dishService.getById(dishId);
            BeanUtils.copyProperties(dish,dishDto);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dtoList);
    }

}
