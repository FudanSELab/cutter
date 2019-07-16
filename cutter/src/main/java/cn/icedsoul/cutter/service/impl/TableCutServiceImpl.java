package cn.icedsoul.cutter.service.impl;

import cn.icedsoul.cutter.algorithm.CutGraphAlgorithm;
import cn.icedsoul.cutter.algorithm.SpectralClusteringAlgorithm;
import cn.icedsoul.cutter.algorithm.communityDetection.CommunityDetectionAlgorithm;
import cn.icedsoul.cutter.algorithm.fastNewman.FastNewmanAlgothrim;
import cn.icedsoul.cutter.algorithm.girvanNewman.GirvanNewmanAlgorithm;
import cn.icedsoul.cutter.domain.bo.ShareTable;
import cn.icedsoul.cutter.domain.po.Table;
import cn.icedsoul.cutter.domain.dto.CloseToRelation;
import cn.icedsoul.cutter.repository.CloseToRepository;
import cn.icedsoul.cutter.repository.TableRepository;
import cn.icedsoul.cutter.service.api.SharingDegreeService;
import cn.icedsoul.cutter.service.api.SplitCostService;
import cn.icedsoul.cutter.service.api.TableCutService;
import cn.icedsoul.cutter.service.api.WeightCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TableCutServiceImpl implements TableCutService {

    @Autowired
    TableRepository tableRepository;
    @Autowired
    CloseToRepository closeToRepository;
    @Autowired
    WeightCalculationService weightCalculationService;
    @Autowired
    SharingDegreeService sharingDegreeService;
    @Autowired
    SplitCostService splitCostService;

    List<Table> tableList1;
    int tableSize;
    double[][] G;
    double alpha = 1, beta = 0.5, gama = 0.2;
    //从tableid到tableList中下标的映射
    Map<Long, Integer> tableId2IndexMap = new HashMap<>();
    //返回的最优解,groupNum -> group中的所有表的index
    Map<Integer, List<Integer>> clusters;


    @Override
    public Map<Integer, List<Table>> cutTable(int k) {
         generateGraph();
         printG(G);

//        CutGraphAlgorithm cutGraphAlgorithm = new SpectralClusteringAlgorithm(G, k);
//        CutGraphAlgorithm cutGraphAlgorithm = new AsymmetricKMeansAlgorithm(G, k);
//        CutGraphAlgorithm cutGraphAlgorithm = new MCLClusteringAlgorithm(G);
//        CutGraphAlgorithm cutGraphAlgorithm = new FastNewmanAlgothrim(G, k);
//        CutGraphAlgorithm cutGraphAlgorithm = new GirvanNewmanAlgorithm(G, k);
        CutGraphAlgorithm cutGraphAlgorithm = new GirvanNewmanAlgorithm(G);
//        CutGraphAlgorithm cutGraphAlgorithm = new CommunityDetectionAlgorithm(G);
        clusters = cutGraphAlgorithm.calculate();

        Map<Integer, List<Table>> result = new HashMap<>();
        translateResult(clusters, result, 1, tableList1);

        //打印聚类过程和每一步的拆分代价
//        Map<Integer, Map<Integer, List<Integer>>> process = ((GirvanNewmanAlgorithm) cutGraphAlgorithm).getAllResults();
//        clusteringProcess(process);

        return result;
    }

    @Override
    //Extract tables that sharing degree are high and then cut table
    public Map<Integer, List<Table>> cutTable2(int k) {
        generateGraph();
        printG(G);
        Map<Integer, List<Table>> result  = new HashMap<>();

        List<Set<ShareTable>> sharingClusters = sharingDegreeService.shareCalculate();
        Set<Integer> sharingTableIndexes = new HashSet<>();
        int groupNum = 1;
        System.out.println("---共享度高------");
        for(Set<ShareTable> set: sharingClusters){
            List<Table> tempTables = new ArrayList<>();
            for(ShareTable t: set){
                tempTables.add(t.getTable());
                sharingTableIndexes.add(tableId2IndexMap.get(t.getTable().getId()));
            }
            System.out.println("第"+ groupNum + "组：" + tempTables.stream().map(t -> t.getTableName()).collect(Collectors.toList()));
            result.put(groupNum, tempTables);
            groupNum++;
        }

        double[][] GwithoutShare = new double[tableSize - sharingTableIndexes.size()][tableSize - sharingTableIndexes.size()];
        int newi = 0, newj = 0;
        List<Table> tableList2 = new ArrayList<>();
        for(int i = 0; i < tableSize; i++){
            if(!sharingTableIndexes.contains(i)){
                newj = 0;
                tableList2.add(tableList1.get(i));
                for(int j = 0; j < tableSize; j++){
                    if(!sharingTableIndexes.contains(j)){
                        GwithoutShare[newi][newj] = G[i][j];
                        newj++;
                    }
                }
                newi ++;
            }
        }
        System.out.println("-----GwithoutShare---");
        printG(GwithoutShare);

        CutGraphAlgorithm cutGraphAlgorithm = new GirvanNewmanAlgorithm(GwithoutShare);
//        CutGraphAlgorithm cutGraphAlgorithm = new SpectralClusteringAlgorithm(GwithoutShare, k);
//        CutGraphAlgorithm cutGraphAlgorithm = new CommunityDetectionAlgorithm(GwithoutShare);
        clusters = cutGraphAlgorithm.calculate();

        //转换拆分结果
        translateResult(clusters, result, groupNum, tableList2);

        //打印聚类过程和每一步的拆分代价
//        Map<Integer, Map<Integer, List<Integer>>> process = ((GirvanNewmanAlgorithm) cutGraphAlgorithm).getAllResults();
//        clusteringProcess(process);

        return result;
    }



    @Override
    //Adjust the weight of tables that have high sharing degree and then cut table
    public Map<Integer, List<Table>> cutTable3(int k) {
        generateGraph();
        printG(G);

        Map<Integer, List<Table>> result  = new HashMap<>();
        //调整共享度高的表的边权重
        List<Set<ShareTable>> sharingClusters = sharingDegreeService.shareCalculate();
        List<List<Table>> tables = shareTableToTable(sharingClusters);
        adjustWeightforSharing(tables);

        //切图
        CutGraphAlgorithm cutGraphAlgorithm = new GirvanNewmanAlgorithm(G);
//        CutGraphAlgorithm cutGraphAlgorithm = new CommunityDetectionAlgorithm(G);
        clusters = cutGraphAlgorithm.calculate();
//        CutGraphAlgorithm cutGraphAlgorithm = new SpectralClusteringAlgorithm(G, k);
//        clusters = cutGraphAlgorithm.calculate();

        //转换拆分结果
        translateResult(clusters, result, 1, tableList1);

        //打印聚类过程和每一步的拆分代价
//        Map<Integer, Map<Integer, List<Integer>>> process = ((GirvanNewmanAlgorithm) cutGraphAlgorithm).getAllResults();
//        clusteringProcess(process);

        return result;
    }

    //将ShareTable转化为Table
    private List<List<Table>> shareTableToTable(List<Set<ShareTable>> shareTables){
        List<List<Table>> result = new ArrayList<>();
        for(Set<ShareTable> sts: shareTables){
            List<Table> list = new ArrayList<>();
            for(ShareTable st: sts){
                list.add(st.getTable());
            }
        }
        return result;
    }


    ////////////////////////网页用到的接口///////////////////////////////////////////////////

    //使用前台传的共享表,目前使用的是削减矩阵的权重
    @Override
    public Map<Integer, List<Table>> realCut(int k, List<List<Table>> sharingClusters) {
        generateGraph();
        printG(G);

        Map<Integer, List<Table>> result  = new HashMap<>();
        //调整共享度高的表的边权重
        adjustWeightforSharing(sharingClusters);
        //切图
        CutGraphAlgorithm cutGraphAlgorithm = new GirvanNewmanAlgorithm(G);
//        CutGraphAlgorithm cutGraphAlgorithm = new CommunityDetectionAlgorithm(G);
        clusters = cutGraphAlgorithm.calculate();
//        CutGraphAlgorithm cutGraphAlgorithm = new SpectralClusteringAlgorithm(G, k);
//        clusters = cutGraphAlgorithm.calculate();

        //转换拆分结果
        translateResult(clusters, result, 1, tableList1);

        //打印聚类过程和每一步的拆分代价
//        Map<Integer, Map<Integer, List<Integer>>> process = ((GirvanNewmanAlgorithm) cutGraphAlgorithm).getAllResults();
//        clusteringProcess(process);

        return result;
    }


    //根据共享度高的表调整已有矩阵
    private void adjustWeightforSharing(List<List<Table>> sharingClusters){
        for(List<Table> sc: sharingClusters){
            Set<Integer> indexes = new HashSet<>();
            for(Table st: sc){
                indexes.add(tableId2IndexMap.get(st.getId()));
            }
            System.out.println("高共享度group:" + indexes);
            //group里面的两table之间边不变，其他的与该group中table两连的边权重减为原来的20%
            if(indexes.size() > 1){
                for(int i = 0; i < tableSize; i++){
                    if(indexes.contains(i)){
                        for(int j = 0; j < tableSize; j++){
                            if( G[i][j] > 0  && !indexes.contains(j)){
                                //TODO: need to change a proper strategy
                                G[i][j] *= 0.2;
                                G[j][i] = G[i][j];
                            }
                        }
                    }
                }
            }
        }
        System.out.println("----Graph after adjusting weight-----");
        printG(G);
    }

    /////////////////////////////////////////////////////////////////////

    //打印聚类的过程和每次迭代的拆分代价
    private void clusteringProcess(Map<Integer, Map<Integer, List<Integer>>> process){
        System.out.println("----聚类的过程----");
        for(int p: process.keySet()){
            System.out.println("第" + p +"次聚类：");
            Map<Integer, List<Integer>> map = process.get(p);
            Map<Integer, List<Table>> mapResult  = new HashMap<>();
            for(int m: map.keySet()){
                List<Table> t = new ArrayList<>();
                List<Integer> list = map.get(m);
                for(int l: list){
                    t.add(tableList1.get(l));
                }
                System.out.println("第"+m+"组：" + t.stream().map(a -> a.getTableName()).collect(Collectors.toList()));
                mapResult.put(m, t);
            }
            calculateSplitCost(mapResult);
        }

    }

    //calculate split cost
    private void calculateSplitCost(Map<Integer, List<Table>> result){
        List<List<Long>> idList = new ArrayList<>();
        for(int groupNum: result.keySet()){
            List<Table> tables = result.get(groupNum);
            List<Long> ids = tables.stream().map(t -> t.getId()).collect(Collectors.toList());
            idList.add(ids);
        }
        splitCostService.getSplitCost(idList);
    }

    //综合sql/trace/scenario三个层次的矩阵，得到最终的用于图分割的矩阵
    public void generateGraph(){
        tableList1 = (List)tableRepository.findAll();
        printTableList(tableList1);
        if(null != tableList1) {
            tableSize = tableList1.size();
            //初始化从tableid到tableList中下标的映射
            initTableId2IndexMap();
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
                    if(i == j) continue;
                    G[i][j] = alpha * G1[i][j] + beta * G2[i][j] + gama * G3[i][j];
                }
            }
        }

        //计算非零元素占G中所有元素的比例
        int notZeroNum = 0;
        for(int i = 0; i < tableSize; i++){
            for(int j = 0; j < tableSize; j++){
                if(G[i][j] > 0 ) notZeroNum ++;
            }
        }
        double pro = (double)notZeroNum / (tableSize * tableSize);
        System.out.println("G规模：" + (tableSize*tableSize) + " 非0元素的个数：" + notZeroNum + " 占比：" + pro);
    }


    //table的id和在矩阵中的index的对应map
    private void initTableId2IndexMap(){
        tableId2IndexMap.clear();
        for(int i = 0; i < tableSize; i++){
            tableId2IndexMap.put(tableList1.get(i).getId(), i);
        }
    }


    /**
     * 获取最终结果
     * @param clusters: 算法得出的分组结果
     * @param result: 最终要返回的结果
     * @param groupNum： 起始组数序号，从1开始，如果事先提出了共享度高的表，则不是从1开始
     */
    private Map<Integer, List<Table>> translateResult(Map<Integer, List<Integer>> clusters, Map<Integer, List<Table>> result, int groupNum, List<Table> tableList){
        System.out.println("----拆分结果：---");
        for(int num: clusters.keySet()){
            List<Integer> indexList = clusters.get(num);
            List<Table> group = indexList.stream().map(l -> tableList.get(l)).collect(Collectors.toList());
            System.out.println("第"+ groupNum + "组：" + group.stream().map(g -> g.getTableName()).collect(Collectors.toList()));
            result.put(groupNum, group);
            groupNum++;
        }
        return result;
    }

    ////////////////////打印辅助类//////////////////////////////
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

    private void printTableList(List<Table> tableList){
        for(int i = 0; i < tableList.size(); i++){
            System.out.println(i+": " + tableList.get(i).getTableName());
        }
    }

}
