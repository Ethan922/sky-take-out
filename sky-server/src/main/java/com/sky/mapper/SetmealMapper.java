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

import java.util.List;

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

    /**
     * 根据分类id查询该分类下的套餐数量
     * @param categoryId
     * @return
     */
    @Select("select count(*) from setmeal where category_id=#{categoryId};")
    int selectByCategoryId(Long categoryId);

    /**
     * 查询未启售套餐的数量
     */
    @Select("select count(*) from setmeal where status=0;")
    Integer selectCountOfDisableSetmeals();

    /**
     * 查询启售套餐的数量
     */
    @Select("select count(*) from setmeal where status=1;")
    Integer selectCountOfEnableSetmeals();
}
