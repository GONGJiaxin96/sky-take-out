package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     *新增菜品和对应的口味
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品id删除对应的口味数据
     * @param id
     */
    void deleteByDishId(Long id);

    /**
     * 根据菜品Id查询对应的口味数据
     * @param dishId
     * @return
     */
    List<DishFlavor> getBydishid(Long dishId);
}
