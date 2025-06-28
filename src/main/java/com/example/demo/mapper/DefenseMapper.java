// DefenseMapper.java
package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Defense;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DefenseMapper extends BaseMapper<Defense> {
}