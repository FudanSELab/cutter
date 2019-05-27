package cn.icedsoul.cutter.service.impl;

import cn.icedsoul.cutter.algorithm.CutGraphAlgorithm;
import cn.icedsoul.cutter.algorithm.SpectralClusteringAlgorithm;
import cn.icedsoul.cutter.domain.Table;
import cn.icedsoul.cutter.queryresult.CloseToRelation;
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
        if(null != G){
            CutGraphAlgorithm cutGraphAlgorithm = new SpectralClusteringAlgorithm(G, k);
            clusters = cutGraphAlgorithm.calculate();
            return translateClusters(clusters);
        }
        return null;
    }

    @Override
    public Map<Integer, List<String>> communityDetection() {
        generateGraph();
//        if(null != G){
//            CutGraphAlgorithm cutGraphAlgorithm = new CommunityDetectionAlgorithm(G);
//            clusters = cutGraphAlgorithm.calculate();
//            return translateClusters(clusters);
//        }
        return null;
    }


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

    private void generateGraph(){
        tableList = (List)tableRepository.findAll();
        if(null != tableList){
            tableSize = tableList.size();
            //初始化从tableid到tableList中下标的映射
            initTableMap();
            G = new double[tableSize][tableSize];
            for(int i = 0; i < tableSize; i++){
                List<CloseToRelation> closetorelationlist = closeToRepository.findCloseTosOfNode(tableList.get(i).getId());
                System.out.println("----"+closetorelationlist);
                for(CloseToRelation ctr: closetorelationlist){
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

    private void initTableMap(){
        tableMap.clear();
        for(int i = 0; i < tableSize; i++){
            tableMap.put(tableList.get(i).getId(), i);
        }
    }
}
