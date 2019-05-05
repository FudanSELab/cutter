package cn.icedsoul.cutter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

/**
 * @author IcedSoul
 * @date 19-5-5 上午10:40
 */
@Configuration
@EnableNeo4jRepositories(basePackages="cn.icedsoul.cutter.repository")
public class Neo4jConfig {

}
