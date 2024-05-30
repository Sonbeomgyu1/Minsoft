package com.mysite.minsoft.login.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.mysite.minsoft.login.model.SiteUser;

@Mapper
public interface UserMapper {
    
    @Select("SELECT ID, PASSWORD FROM ADMIN WHERE ID = #{id}")
    SiteUser getUserById(String id);
    
    // Add more methods for other CRUD operations
}

