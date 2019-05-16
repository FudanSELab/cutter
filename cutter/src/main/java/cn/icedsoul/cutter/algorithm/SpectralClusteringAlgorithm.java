package cn.icedsoul.cutter.algorithm;

import cn.icedsoul.cutter.domain.Table;
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
                    G[i][j] = 0.1;
                    G[j][i] = 0.1;
                }
            }
        }
    }

    @Override
    public Map<Integer, List<Integer>> calculate() {
        preProcess();
        Map<Integer, List<Integer>> clusters = new HashMap<>();

        if(null == G || k > G.length) return clusters;

        SpectralClustering sc = new SpectralClustering(G, k);
        for(int i = 0; i < sc.getNumClusters(); i ++){
            clusters.put(i, new ArrayList<>());
        }
        int[] labels = sc.getClusterLabel();
        for(int i = 0; i < labels.length; i++){
            clusters.get(labels[i]).add(i);
        }
        return clusters;
    }
}
