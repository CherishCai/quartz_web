package cn.cherish.learn.quartz_web;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "cn.cherish.learn.quartz_demo.dal.mapper", annotationClass = Mapper.class)
public class QuartzWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuartzWebApplication.class, args);
	}
}

