package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishItemVO {
    //份数
    private Integer copies;
    //菜品名称
    private String name;
    //菜品描述
    private String description;
    //菜品图片
    private String image;
}
