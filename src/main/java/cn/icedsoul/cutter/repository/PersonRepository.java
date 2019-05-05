package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.domain.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * @author IcedSoul
 * @date 19-5-5 上午10:49
 */
public interface PersonRepository extends Neo4jRepository<Person, Integer> {
    Person findByName(String name);
}
