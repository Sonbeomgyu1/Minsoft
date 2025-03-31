package com.mysite.minsoft.login.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="ADMIN")
public class SiteUser  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "NAME", unique = true)
    private String name;


}