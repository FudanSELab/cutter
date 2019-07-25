package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.domain.po.Method;
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
     * 查询特定方法是否存在
     * @param modifier
     * @param returnType
     * @param packageName
     * @param className
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

    @Query("match (n)-[r]-() delete r")
    void clearRelation();

    @Query("match (n) delete n")
    void clearNode();
    /**
     * 寻找源点
     */
    @Query("match (a:Method)" +
            "where a.methodName='Entry'" +
            "return a")
    List<Method> findEntry();

    /**
     * 寻找调用sql的所有method
     */
    @Query("match (m:Method)-[r:EXECUTE]->(s:Sql) " +
            "where id(s)={0} and r.scenarioFrequency > 0 " +
            "return m")
    List<Method> findMethodsBySql(long sqlId);

    @Query("match (c:Class)-[r:METHOD_CONTAIN]->(m:Method) " +
            "where id(c)={0}" +
            "return id(m)")
    List<Long> getMethodsByClassId(long classId);
}
