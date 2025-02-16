package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     *新增菜品和对应的口味
     * @param dishDTO
     */
    @Override
    @Transactional //因为涉及多张表的数据，所以要保证数据一致性
    public void saveWithFlavor(DishDTO dishDTO) {

        //(不需要将实体类数据传进去，只需要一个实体对象即可)new出来的空的，通过DTO给对象赋值
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //向菜品表插1条数据
        dishMapper.insert(dish);

        //获取insert语句生成的主键值
        Long dishId = dish.getId(); //先从mapper中获得主键值，才能在这里获取id

        //向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors(); //拿到口味表的集合
        //判断是否存在口味
        if (flavors != null && flavors.size()>0) {
            //遍历集合，插入id
            flavors.forEach(dishFlavor -> {dishFlavor.setDishId(dishId);});

            //插入数据 -- 插入到口味表的mapper
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
