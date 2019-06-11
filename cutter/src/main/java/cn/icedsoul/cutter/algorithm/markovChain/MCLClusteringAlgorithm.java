package cn.icedsoul.cutter.algorithm.markovChain;

import cn.icedsoul.cutter.algorithm.CutGraphAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MCLClusteringAlgorithm implements CutGraphAlgorithm {
    double[][] G;

    public MCLClusteringAlgorithm(double[][] G){
        this.G = G;
    }


    @Override
    public Map<Integer, List<Integer>> calculate() {
        Map<Integer, List<Integer>> clusters = new HashMap<>();

        if(null == G) return clusters;
        printG(G);

        MCLClustering mc = new MCLClustering(2, 2, G);
        mc.runMCL();

        int clusterId = 0;
        List<String> clusterList = new ArrayList<String>();
        double[][] transitionMatrix = mc.getTransitionMatrix();
        for (int i = 0; i < transitionMatrix.length; i++) {
            List<Integer> verticesList = new ArrayList<Integer>();
            for (int j = 0; j < transitionMatrix.length; j++) {
                if (transitionMatrix[i][j] > 0)  {
                    verticesList.add(j);
                }
            }
            if (verticesList.size() > 0) {
                String vertexIds = verticesList.stream().map(x -> x.toString()).collect(Collectors.joining());
                if (!clusterList.contains(vertexIds)) {
                    clusterList.add(vertexIds);
                    clusterId++;
                    clusters.put(clusterId, verticesList);
                }
            }
        }
        System.out.println("----clusters:----");
        System.out.println(clusters);
        return clusters;
    }

    private void printG(double[][] G){
        int n = G.length;
        System.out.println("---邻接矩阵------");
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n;j++){
                System.out.print(G[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("----------------");
    }
}
