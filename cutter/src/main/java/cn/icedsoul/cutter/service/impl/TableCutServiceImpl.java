package cn.icedsoul.cutter.service.impl;

import cn.icedsoul.cutter.algorithm.CutGraphAlgorithm;
import cn.icedsoul.cutter.algorithm.girvanNewman.GirvanNewmanAlgorithm;
import cn.icedsoul.cutter.domain.po.Table;
import cn.icedsoul.cutter.domain.dto.CloseToRelation;
import cn.icedsoul.cutter.repository.CloseToRepository;
import cn.icedsoul.cutter.repository.TableRepository;
import cn.icedsoul.cutter.service.api.TableCutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TableCutServiceImpl implements TableCutService {

    @Autowired
    TableRepository tableRepository;
    @Autowired
    CloseToRepository closeToRepository;

    List<Table> tableList;
    int tableSize;
    double[][] G;
    /**
     * 从tableid到tableList中下标的映射
     */
    Map<Long, Integer> tableMap = new HashMap<>();
    Map<Integer, List<Integer>> clusters;

    @Override
    public Map<Integer, List<String>> cutTable(int k) {
        generateGraph();
//        printG(G);
//        calculateAffinity();
        if(null != G){
//            CutGraphAlgorithm cutGraphAlgorithm = new SpectralClusteringAlgorithm(G, k);
//            CutGraphAlgorithm cutGraphAlgorithm = new AsymmetricKMeansAlgorithm(G, k);
//            CutGraphAlgorithm cutGraphAlgorithm = new MCLClusteringAlgorithm(G);
//            CutGraphAlgorithm cutGraphAlgorithm = new FastNewmanAlgothrim(G, k);
            CutGraphAlgorithm cutGraphAlgorithm = new GirvanNewmanAlgorithm(G, k);
            clusters = cutGraphAlgorithm.calculate();
            return translateClusters(clusters);
        }
        return null;
    }

//    @Override
//    public Map<Integer, List<String>> communityDetection() {
//        generateGraph();
//        if(null != G){
//            CutGraphAlgorithm cutGraphAlgorithm = new CommunityDetectionAlgorithm(G);
//            clusters = cutGraphAlgorithm.calculate();
//            return translateClusters(clusters);
//        }
//        return null;
//    }

    //打印拆分List
    private Map<Integer, List<String>> translateClusters(Map<Integer, List<Integer>> clusters){
        System.out.println("----拆分结果：---");
        Map<Integer, List<String>> r = new HashMap<>();
        Iterator iterator = clusters.keySet().iterator();
        while(iterator.hasNext()){
            int num = (int)iterator.next();
            List<Integer> l = clusters.get(num);
            List<String> group = new ArrayList<>();
            for(Integer i: l){
                group.add(tableList.get(i).getTableName());
            }
            System.out.println("第"+ num + "组：" + group);
            r.put(num, group);
        }
        return r;
    }

    //生成邻接矩阵
    private void generateGraph(){
        tableList = (List)tableRepository.findAll();
        printTableList();
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

    //table名称和id的对应map
    private void initTableMap(){
        tableMap.clear();
        for(int i = 0; i < tableSize; i++){
            tableMap.put(tableList.get(i).getId(), i);
        }
    }

    //从邻接矩阵生成吸引度矩阵
    //a对b的吸引度=ab边的权重/与b相连所有边的权重
    private void calculateAffinity(){
        for(int i = 0; i < tableSize; i++){
            int sum = 0;
            for(int j = 0; j < tableSize; j++){
                sum += G[i][j];
            }
            if(sum > 0){
                for(int j = 0; j < tableSize; j++){
                    G[i][j] = G[i][j] / sum;
                }
            }
        }
        printG(G);
    }

    private void printG(double[][] G){
        int n = G.length;
        System.out.println("---吸引度矩阵------");
        for(int i = 0; i < n; i++){
            System.out.print("{ ");
            for(int j = 0; j < n;j++){
                if(j != n-1) System.out.print(G[i][j] + ", ");
                else System.out.print(G[i][j]);
            }
            System.out.println(" },");
        }
        System.out.println("----------------");
    }

    private void printTableList(){
        for(int i = 0; i < tableList.size(); i++){
            System.out.println(i+": " + tableList.get(i).getTableName());
        }
    }

}
