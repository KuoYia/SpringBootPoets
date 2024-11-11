package clover;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("clover.dao")
public class BootStart {
    public static void main(String[] args) {
        SpringApplication.run(BootStart.class, args);
    }
}
