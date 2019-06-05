package cn.icedsoul.cutter.algorithm.communityDetection;

import cn.icedsoul.cutter.algorithm.CutGraphAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunityDetectionAlgorithm implements CutGraphAlgorithm {

    double[][] G;

    public CommunityDetectionAlgorithm(double[][] G){
        this.G = G;
    }

    @Override
    public Map<Integer, List<Integer>> calculate() {
        Map<Integer, List<Integer>> clusters = new HashMap<>();
        if(null == G) return clusters;

        Louvain louvain = new Louvain();
        louvain.init(G);
        louvain.louvain();

        //打印每个簇有哪些节点
        System.out.println("-----拆分结果：------");
        for(int i=0;i<louvain.global_n;i++){
            clusters.put(i, new ArrayList<>());
        }
        for(int i=0;i<louvain.global_n;i++){
            clusters.get(louvain.global_cluster[i]).add(i);
        }
        for(int i=0;i<louvain.global_n;i++){
            if(clusters.get(i).size()== 0) clusters.remove(i);
        }
        System.out.println("----clusters:----");
        System.out.println(clusters);
        return clusters;
    }

}
