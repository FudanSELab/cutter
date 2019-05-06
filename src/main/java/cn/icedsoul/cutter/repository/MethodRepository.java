package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.domain.Method;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

/**
 * @author IcedSoul
 * @date 19-5-6 上午10:26
 */
public interface MethodRepository extends Neo4jRepository<Method, Long> {
    /**
     * 查询指定内容的方法
     * @param modifier
     * @param returnType
     * @param methodName
     * @param params
     * @return
     */
    Method findByModifierAndReturnTypeAndMethodNameAndParams(List<String> modifier, String returnType, String methodName, List<String> params);

    /**
     * 根据方法名查询
     * @param methodName
     * @return
     */
    Method findByMethodName(String methodName);
}
