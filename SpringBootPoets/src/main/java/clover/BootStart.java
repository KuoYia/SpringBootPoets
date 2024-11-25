package clover;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;


@SpringBootApplication
@EntityScan(basePackages = "clover.model")
@MapperScan("clover.dao")

public class BootStart {
    public static void main(String[] args) {
        SpringApplication.run(BootStart.class, args);
    }
}
