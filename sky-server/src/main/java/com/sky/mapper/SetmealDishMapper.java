package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据多个菜品Id来查套餐Id
     * @param dishIds
     * @return
     */
    //select setmeal_id from setmeal_dish where dish_id in (''); 不能是等号，查询多个数据用in
    List<Long> getSetmaelIdsByDishIds(List<Long> dishIds);
}
