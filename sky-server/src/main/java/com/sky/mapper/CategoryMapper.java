package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {

    /**
     * 新增分类
     * @param category
     */
    @AutoFill(OperationType.INSERT)
    @Insert("insert into category (name,sort,type,status,create_time,update_time,create_user,update_user) values " +
            "(#{name},#{sort},#{type},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser});")
    void insert(Category category);

    /**
     * 更新分类数据
     * @param category
     */
    @AutoFill(OperationType.UPDATE)
    void update(Category category);

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据id删除分类
     * @param id
     */
    @Delete("delete from category where id=#{id}")
    void delete(Long id);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    List<Category> typeQuery(Integer type);

    @Select("select id from category where name=#{categoryName};")
    Long selectCategorIdByCategoryName(String categoryName);

    /**
     * 根据类型查询enable状态的分类
     * @param type
     * @return
     */
    List<Category> typeQueryOfEnable(Integer type);
}
