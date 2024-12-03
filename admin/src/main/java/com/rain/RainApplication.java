package com.rain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动应用
 */
@SpringBootApplication(scanBasePackages = {"com.*"})
public class RainApplication {

    public static void main(String[] args) {
        SpringApplication.run(RainApplication.class , args);
        System.out.println("Rain ————> 雨 ， 启动成功！！！");
    }

}
