package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
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

    /**
     * 分页查询套餐信息
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    @Select("select id from setmeal where name=#{setmealName};")
    Long selectSetmealIdBySetmealName(String setmealName);

    /**
     * 修改套餐的基本信息
     * @param setmeal
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 批量删除套餐
     * @param ids
     */
    void deleteSetmeals(Long[] ids);
}
