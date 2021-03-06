package cn.icedsoul.cutter.algorithm;

import smile.clustering.SpectralClustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpectralClusteringAlgorithm implements CutGraphAlgorithm {

    double[][] G;
    int k;


    public SpectralClusteringAlgorithm(double[][] G, int k){
        this.G = G;
        this.k = k;
    }

    public void preProcess(){
        int n = G.length;
        for(int i = 0; i < n; i++){
            for(int j = i + 1; j < n; j++){
                if(G[i][j] == 0){
                    G[i][j] = 0.000001;
                    G[j][i] = 0.000001;
                }
            }
        }
    }

    @Override
    public Map<Integer, List<Integer>> calculate() {
        // 不能有孤立的点
        preProcess();
        Map<Integer, List<Integer>> clusters = new HashMap<>();

        if(null == G || k > G.length) return clusters;
//        printG(G);
        SpectralClustering sc = new SpectralClustering(G, k);
        for(int i = 0; i < sc.getNumClusters(); i ++){
            clusters.put(i, new ArrayList<>());
        }
        int[] labels = sc.getClusterLabel();
        for(int i = 0; i < labels.length; i++){
            clusters.get(labels[i]).add(i);
        }
        System.out.println("----clusters:----");
        System.out.println(clusters);
        return clusters;
    }


//    private void printG(double[][] G){
//        int n = G.length;
//        System.out.println("---邻接矩阵------");
//        for(int i = 0; i < n; i++){
//            for(int j = 0; j < n;j++){
//                System.out.print(G[i][j] + " ");
//            }
//            System.out.println();
//        }
//        System.out.println("----------------");
//    }
}
