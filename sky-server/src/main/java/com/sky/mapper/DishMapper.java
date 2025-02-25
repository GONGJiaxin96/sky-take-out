package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 插入菜品数据
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT) //使用自动填充将创建时间等填充
    void insert(Dish dish);

    /**
     * 菜品的分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据主键查询菜品
     * @param id
     * @return
     */
    Dish getById(Long id);

    /**
     *根据主键删除菜品
     * @param id
     */
    void deleteById(Long id);

    /**
     * 根据id动态修改菜品基本信息（写成动态的，dish里面有值的时候在进行修改）
     * @param dish
     */
    @AutoFill(value = OperationType.UPDATE)//空的上传人，上传时间等信息自动填充
    void update(Dish dish);
}
