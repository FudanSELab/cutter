package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.domain.Method;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author IcedSoul
 * @date 19-5-6 上午10:26
 */
@Repository
public interface MethodRepository extends Neo4jRepository<Method, Long> {
    /**
     * 查询指定内容的方法
     * @param modifier
     * @param returnType
     * @param methodName
     * @param params
     * @return
     */
    Method findByModifierAndReturnTypeAndPackageNameAndClassNameAndMethodNameAndParams(List<String> modifier, String returnType, String packageName, String className, String methodName, List<String> params);

    /**
     * 根据方法名查询节点
     * @param packageName
     * @param className
     * @param methodName
     * @return
     */
    Method findByPackageNameAndClassNameAndMethodName(String packageName, String className, String methodName);

    @Query("match (n)-[r]-() delete n,r")
    void clearDatabase();
}
