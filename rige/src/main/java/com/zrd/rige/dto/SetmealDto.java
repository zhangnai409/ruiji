package com.zrd.rige.dto;


import com.zrd.rige.entity.Setmeal;
import com.zrd.rige.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
