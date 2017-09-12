package cn.cherish.learn.quartz_web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@MapperScan(basePackages = "cn.cherish.learn.quartz_demo.dal.mapper", annotationClass = Repository.class)
public class QuartzWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuartzWebApplication.class, args);
	}
}

