package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper {

    /**
     * 插入套餐信息
     * @param setmeal
     */
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 根据套餐id查询套餐信息
     * @param id
     * @return
     */
    @Select("select s.*,c.name categoryName from setmeal s left outer join category c on s.category_id = c.id where s.id=#{id};")
    SetmealVO selectBySetmealId(Long id);
}
