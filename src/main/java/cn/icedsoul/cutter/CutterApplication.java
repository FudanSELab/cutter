package cn.icedsoul.cutter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jRepositories(basePackages="cn.icedsoul.cutter.repository")
public class CutterApplication {

    public static void main(String[] args) {
        SpringApplication.run(CutterApplication.class, args);
    }

}
