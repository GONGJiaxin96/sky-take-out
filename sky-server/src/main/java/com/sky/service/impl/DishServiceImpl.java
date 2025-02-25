package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
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
    @Autowired
    private SetmealDishMapper setmealDishMapper;

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

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        //根据接口文档，符合的只有DishVO
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 批量删除菜品（先查询，后删除）
     * @param ids
     */
    @Override
    @Transactional//多表操作需要添加事务注解
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否能够删除--是否起售中
        //先遍历数组，取出id，然后根据id查询菜品
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            //判断菜品的状态
            if (dish.getStatus() == StatusConstant.ENABLE) {
                //菜品处于起售中，不嫩能够删除 -- 抛出业务异常
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //当前菜品是否被套餐关联了 --关联了无法被删除
        List<Long> setmaelIds = setmealDishMapper.getSetmaelIdsByDishIds(ids);
        //判断查出来的结果
        if (setmaelIds !=null &&setmaelIds.size()>0) {
            //查到了菜品被套餐关联，不能被删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品表中的菜品数据 -- 批量删除用集合，遍历后调用SQL的删除
        for (Long id : ids) {
            dishMapper.deleteById(id);
            //删除菜品关联的口味数据 -- 没有关联的菜品删除后，口味表直接删除
            dishFlavorMapper.deleteByDishId(id);
        }
    }

    /**
     * 根据id查询菜品和对应的口味数据
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //根据id查询菜品数据
        Dish dish = dishMapper.getById(id);
        //根据菜品id查询口味数据
        List<DishFlavor> dishFlavors= dishFlavorMapper.getBydishid(id);
        //将查询到的数据封装到DishVO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);//将口味数据设置进去
        return dishVO;
    }

    /**
     * 根据id修改菜品基本信息和对应的口味
     * @param dishDTO
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        //操作两张表--菜品表和口味表
        //修改菜品的基本信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish); //这里只需要菜品表基本信息
        //删除原有的口味关联数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //重新插入口味数据 -- 新的口味数据DTO里已经传过来了
        List<DishFlavor> flavors = dishDTO.getFlavors();
         //判断一下集合有值且不为空的时候再批量插入
        if (flavors !=null && flavors.size()>0) {
            // 批量插入之前重新设置一下dishId -- 遍历集合，重新插入dishId（先删除后插入，口味数据可能是新增出来的）
            flavors.forEach(dishFlavor -> {dishFlavor.setDishId(dishDTO.getId());});
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
