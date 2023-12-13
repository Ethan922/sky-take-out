package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    void insert(User user);

    @Select("select * from user where openid=#{openid};")
    User selectByOpenid(String openid);

    @Select("select * from user;")
    List<User> getAllUsers();

    @Select("select * from user where id=#{userId};")
    User getById(Long userId);

    Integer getUserCountByMap(Map map);
}
