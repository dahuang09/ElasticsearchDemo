package com.damon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.damon.dao")
public class ElasticsearchDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElasticsearchDemoApplication.class, args);
	}

}
