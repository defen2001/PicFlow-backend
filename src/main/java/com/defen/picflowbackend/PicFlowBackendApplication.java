package com.defen.picflowbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.defen.picflowbackend.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class PicFlowBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PicFlowBackendApplication.class, args);
    }

}
