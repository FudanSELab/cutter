package cn.icedsoul.cutter;

import cn.icedsoul.cutter.algorithm.CutGraphAlgorithm;
import cn.icedsoul.cutter.algorithm.SpectralClusteringAlgorithm;
import cn.icedsoul.cutter.domain.Table;
import cn.icedsoul.cutter.queryresult.CloseToRelation;
import cn.icedsoul.cutter.repository.CloseToRepository;
import cn.icedsoul.cutter.repository.TableRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import smile.clustering.KMeans;
import smile.clustering.SpectralClustering;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CutTableGraphTest {

    @Autowired
    TableRepository tableRepository;
    @Autowired
    CloseToRepository closeToRepository;

    List<Table> tableList;
    int tableSize;
    double[][] G;
    //从tableid到tableList中下标的映射
    Map<Long, Integer> tableMap = new HashMap<>();

    @Test
    public void testMethod() {
        generateGraph();
        printG(G);
        if(null != G){
            CutGraphAlgorithm cutGraphAlgorithm = new SpectralClusteringAlgorithm(G, tableList, 3);
            Map<Integer, List<Long>> clusters = cutGraphAlgorithm.calculate();
            System.out.println("----拆分结果：---");
            System.out.println(clusters);
        }
    }

    public void printG(double[][] G){
        int n = G.length;
        System.out.println("---邻接矩阵：");
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n;j++){
                System.out.print(G[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void generateGraph(){
        tableList = (List)tableRepository.findAll();
        if(null != tableList){
            tableSize = tableList.size();
            //初始化从tableid到tableList中下标的映射
            initTableMap();
            G = new double[tableSize][tableSize];
            for(int i = 0; i < tableSize; i++){
                List<CloseToRelation> CloseToRelationList = closeToRepository.findCloseTosOfNode(tableList.get(i).getId());
                System.out.println("----"+CloseToRelationList);
                for(CloseToRelation ctr: CloseToRelationList){
                    G[tableMap.get(ctr.getStartTableId())][tableMap.get(ctr.getEndTableId())] = ctr.getWeight();
                    G[tableMap.get(ctr.getEndTableId())][tableMap.get(ctr.getStartTableId())] = ctr.getWeight();
                }
            }
            for(int i = 0; i < tableSize; i++){
                for(int j = 0; j < i; j++){
                    G[i][j] = G[j][i];
                }
            }
        }
    }

    public void initTableMap(){
        tableMap.clear();
        for(int i = 0; i < tableSize; i++){
            tableMap.put(tableList.get(i).getId(), i);
        }
    }


//    public void cutGraphBySpectralClustering(int k){
//        if(null == G || k > G.length) return;
//
//        SpectralClustering sc = new SpectralClustering(G, k);
//        Map<Integer, List<Table>> clusters = new HashMap<>();
//        for(int i = 0; i < sc.getNumClusters(); i ++){
//            clusters.put(i, new ArrayList<>());
//        }
//        int[] labels = sc.getClusterLabel();
//        for(int i = 0; i < labels.length; i++){
//            clusters.get(labels[i]).add(tableList.get(i));
//        }
//        System.out.println(clusters);
//    }


}
