package com.sky.mapper;

import com.sky.entity.Category;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {

    /**
     * 新增分类
     * @param category
     */
    @Insert("insert into category (name,sort,type,status,create_time,update_time,create_user,update_user) values " +
            "(#{name},#{sort},#{type},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser});")
    void insert(Category category);

    /**
     * 更新分类数据
     * @param category
     */
    void update(Category category);
}
