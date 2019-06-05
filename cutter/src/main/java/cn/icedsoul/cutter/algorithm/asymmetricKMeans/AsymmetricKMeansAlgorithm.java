package cn.icedsoul.cutter.algorithm.asymmetricKMeans;

import cn.icedsoul.cutter.algorithm.CutGraphAlgorithm;
import cn.icedsoul.cutter.algorithm.asymmetricKMeans.AsymmetricKMeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsymmetricKMeansAlgorithm implements CutGraphAlgorithm {

    double[][] G;
    int k;

    public AsymmetricKMeansAlgorithm(double[][] G, int k){
        this.G = G;
        this.k = k;
    }

    public void preProcess(){
        int n = G.length;
        for(int i = 0; i < n; i++){
            G[i][i] = 1;
        }
    }

    @Override
    public Map<Integer, List<Integer>> calculate() {
        preProcess();
        Map<Integer, List<Integer>> clusters = new HashMap<>();
        if(null == G || k > G.length) return clusters;
        AsymmetricKMeans ak = new AsymmetricKMeans(G, k);
        for(int i = 0; i < ak.getNumClusters(); i ++){
            clusters.put(i, new ArrayList<>());
        }
        int[] labels = ak.getClusterLabel();
        for(int i = 0; i < labels.length; i++){
            clusters.get(labels[i]).add(i);
        }
        System.out.println("----clusters:----");
        System.out.println(clusters);
        return clusters;
    }


}
