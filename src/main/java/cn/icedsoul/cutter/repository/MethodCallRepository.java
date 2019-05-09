package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.domain.Method;
import cn.icedsoul.cutter.relation.BaseRelation;
import cn.icedsoul.cutter.relation.MethodCall;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author IcedSoul
 * @date 19-5-6 下午2:38
 */
@Repository
public interface MethodCallRepository extends Neo4jRepository<MethodCall, Long> {

}
