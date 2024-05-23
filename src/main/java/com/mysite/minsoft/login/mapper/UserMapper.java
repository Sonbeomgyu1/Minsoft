package com.mysite.minsoft.login.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.mysite.minsoft.login.model.SiteUser;

@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM ADMIN")
    List<SiteUser> getAllUsers();
    
    // Add more methods for other CRUD operations
}
