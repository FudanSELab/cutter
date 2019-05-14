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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TableCutServiceImpl implements TableCutService {

    @Autowired
    TableRepository tableRepository;
    @Autowired
    CloseToRepository closeToRepository;

    List<Table> tableList;
    int tableSize;
    double[][] G;
    //从tableid到tableList中下标的映射
    Map<Long, Integer> tableMap = new HashMap<>();
    Map<Integer, List<Long>> clusters;

    @Override
    public Map<Integer, List<Long>> cutTable() {
        generateGraph();
        printG(G);
        if(null != G){
            CutGraphAlgorithm cutGraphAlgorithm = new SpectralClusteringAlgorithm(G, tableList, 4);
            clusters = cutGraphAlgorithm.calculate();
            System.out.println("----拆分结果：---");
            System.out.println(clusters);
        }
        return clusters;
    }

    private void printG(double[][] G){
        int n = G.length;
        System.out.println("---邻接矩阵：");
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n;j++){
                System.out.print(G[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void generateGraph(){
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

    private void initTableMap(){
        tableMap.clear();
        for(int i = 0; i < tableSize; i++){
            tableMap.put(tableList.get(i).getId(), i);
        }
    }
}
