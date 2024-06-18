package com.mysite.minsoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class MinsoftApplication extends SpringBootServletInitializer{ //war 배포용

//@SpringBootApplication
//public class MinsoftApplication { 기존코드
	
    @Override //war 배포용
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MinsoftApplication.class);
    }


	public static void main(String[] args) {
		SpringApplication.run(MinsoftApplication.class, args);
	}

}


