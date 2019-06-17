package cn.icedsoul.cutter.service.impl;

import cn.icedsoul.cutter.algorithm.CutGraphAlgorithm;
import cn.icedsoul.cutter.algorithm.SpectralClusteringAlgorithm;
import cn.icedsoul.cutter.algorithm.girvanNewman.GirvanNewmanAlgorithm;
import cn.icedsoul.cutter.domain.po.Table;
import cn.icedsoul.cutter.domain.dto.CloseToRelation;
import cn.icedsoul.cutter.repository.CloseToRepository;
import cn.icedsoul.cutter.repository.TableRepository;
import cn.icedsoul.cutter.service.api.SharingDegreeService;
import cn.icedsoul.cutter.service.api.TableCutService;
import cn.icedsoul.cutter.service.api.WeightCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TableCutServiceImpl implements TableCutService {

    @Autowired
    TableRepository tableRepository;
    @Autowired
    CloseToRepository closeToRepository;
    @Autowired
    WeightCalculationService weightCalculationService;

    List<Table> tableList;
    int tableSize;
    double[][] G;
    double alpha = 1, beta = 0.5, gama = 0.2;
    //从tableid到tableList中下标的映射
    Map<Long, Integer> tableMap = new HashMap<>();
    Map<Integer, List<Integer>> clusters;

    @Override
    public Map<Integer, List<String>> cutTable(int k) {
         generateGraph();
         printG(G);

        if(null != G){
//            CutGraphAlgorithm cutGraphAlgorithm = new SpectralClusteringAlgorithm(G, k);
//            CutGraphAlgorithm cutGraphAlgorithm = new AsymmetricKMeansAlgorithm(G, k);
//            CutGraphAlgorithm cutGraphAlgorithm = new MCLClusteringAlgorithm(G);
//            CutGraphAlgorithm cutGraphAlgorithm = new FastNewmanAlgothrim(G, k);
//            CutGraphAlgorithm cutGraphAlgorithm = new GirvanNewmanAlgorithm(G, k);
            CutGraphAlgorithm cutGraphAlgorithm = new GirvanNewmanAlgorithm(G);
            clusters = cutGraphAlgorithm.calculate();
            return translateClusters(clusters);
        }
        return null;
    }


    public void generateGraph(){
        tableList = (List)tableRepository.findAll();
        if(null != tableList) {
            tableSize = tableList.size();
            //初始化从tableid到tableList中下标的映射
            initTableMap();
            //合成G
            G = new double[tableSize][tableSize];
            List<Double[][]> weightMatrixs = weightCalculationService.addSimilarWeight();
            Double[][] G1 = weightMatrixs.get(0);
            System.out.println("---G1----");
            printG123(G1);
            Double[][] G2 = weightMatrixs.get(1);
            System.out.println("---G2----");
            printG123(G2);
            Double[][] G3 = weightMatrixs.get(2);
            System.out.println("---G3----");
            printG123(G3);
            for(int i = 0; i < tableSize; i++){
                for(int j = 0; j < tableSize; j++){
                    G[i][j] = alpha * G1[i][j] + beta * G2[i][j] + gama * G3[i][j];
                }
            }
        }
    }

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


    //table的id和在矩阵中的index的对应map
    private void initTableMap(){
        tableMap.clear();
        for(int i = 0; i < tableSize; i++){
            tableMap.put(tableList.get(i).getId(), i);
        }
    }

    private void printG(double[][] G){
        int n = G.length;
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

    private void printG123(Double[][] G){
        int n = G.length;
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
