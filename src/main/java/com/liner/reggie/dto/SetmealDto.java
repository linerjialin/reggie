package com.liner.reggie.dto;

import com.liner.reggie.entity.Setmeal;
import com.liner.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
