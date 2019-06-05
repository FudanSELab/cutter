package cn.icedsoul.cutter.algorithm.fastNewman;

import cn.icedsoul.cutter.algorithm.CutGraphAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FastNewmanAlgothrim implements CutGraphAlgorithm {

    double[][] G;
    int k;

    public FastNewmanAlgothrim(double[][] G, int k){
        this.G = G;
        this.k = k;
    }

    @Override
    public Map<Integer, List<Integer>> calculate() {
        Map<Integer, List<Integer>> clusters = new HashMap<>();
        if(null == G) return clusters;

        int n = G.length;
        Community d = new Community(n);
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(G[i][j] > 0){
                    d.insertEdge(i+1,j+1,G[i][j]);
                } else if(i == j){
                    d.insertEdge(i+1,j+1, 1);
                }
            }
        }
        System.out.println("---fast newman---");
        ProgramEntrance p = new ProgramEntrance(d);
        p.start_clustering();

        Map<Integer, Map<Integer, List<Integer>>> result = p.getResult();
        Map<Integer, List<Integer>> temp = result.get(G.length - k);
        System.out.println("temp: " + temp);
        for(int i : temp.keySet()){
            List<Integer> l = temp.get(i);
            System.out.println("l : " + l);
            List<Integer> a = new ArrayList<>();
            for(int j: l){
                a.add(j - 1);
            }
            clusters.put(i, a);
        }
        return clusters;
    }
}
