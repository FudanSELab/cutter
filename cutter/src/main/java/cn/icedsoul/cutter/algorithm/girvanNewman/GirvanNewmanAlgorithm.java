package cn.icedsoul.cutter.algorithm.girvanNewman;

import cn.icedsoul.cutter.algorithm.CutGraphAlgorithm;
import cz.cvut.fit.krizeji1.girvan_newman.GirvanNewmanClusterer;
import org.gephi.clustering.api.Cluster;
import org.gephi.graph.api.*;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

import java.util.*;
import java.util.stream.Collectors;

public class GirvanNewmanAlgorithm implements CutGraphAlgorithm {
    double[][] G;
    int k = 0;
    Map<Integer, ArrayList<Cluster>> allResults;
    Map<Integer, Double> modularityMap;

    public GirvanNewmanAlgorithm(double[][] G){
        this.G = G;
    }

    public GirvanNewmanAlgorithm(double[][] G, int k){
        this.G = G;
        this.k = k;
    }

    @Override
    public Map<Integer, List<Integer>> calculate() {
        Map<Integer, List<Integer>> clusters = new HashMap<>();
        if(null == G) return clusters;

        // Given
        Lookup lookup = Lookup.getDefault();
        ProjectController pc = lookup.lookup(ProjectController.class);
        pc.newProject();
        @SuppressWarnings("unused")
//        Workspace workspace = pc.getCurrentWorkspace();
        GraphController controller = Lookup.getDefault().lookup(GraphController.class);
        GraphModel model = controller.getModel();
        model.resetIDGen();
        UndirectedGraph graph = model.getUndirectedGraph();
        List<Node> nodeList = new LinkedList<>();
        for(int i = 0; i < G.length; i++){
            nodeList.add(createNode(model, graph));
        }
        for(int i = 0; i < G.length; i++){
            for(int j = i+1; j < G.length; j++){
                if(G[i][j] > 0){
                    createEdge(model, graph, nodeList.get(i), nodeList.get(j), (float) G[i][j]);
                }
            }
        }
        // When
        GirvanNewmanClusterer clusterer = new GirvanNewmanClusterer();
        if(k > 0){
            clusterer.setPreferredNumberOfClusters(k);
        }
        clusterer.execute(model);
        // Then
        Cluster[] result = clusterer.getClusters();
        for(int i = 0; i < result.length; i++){
            List<Integer> l = Arrays.stream(result[i].getNodes()).map(n -> n.getId() - 1).collect(Collectors.toList());
            System.out.println(i + ":" + l);
            clusters.put(i, l);
        }

        //获取每一步的分割方案
        allResults = clusterer.getAllResults();
        //获取模块度map
        modularityMap = clusterer.getModularityMap();

        return clusters;
    }


    private static void createEdge(GraphModel model, UndirectedGraph graph, Node a, Node b, float weight) {
        Edge edge = model.factory().newEdge(a, b, weight, false);
        graph.addEdge(edge);
    }

    private static Node createNode(GraphModel model, UndirectedGraph graph) {
        Node node = model.factory().newNode();
        graph.addNode(node);
        return node;
    }

    public Map<Integer, Double> getModularityMap(){
        return modularityMap;
    }

    public Map<Integer, Map<Integer, List<Integer>>> getAllResults(){
        Map<Integer, Map<Integer, List<Integer>>> r = new HashMap<>();

        for(int groupNum: allResults.keySet()){
            Map<Integer, List<Integer>> m = new HashMap<>();
            List<Cluster> oneProposal = allResults.get(groupNum);
            for(int i = 0; i < oneProposal.size(); i++){
                List<Integer> l = Arrays.stream(oneProposal.get(i).getNodes()).map(n -> n.getId() - 1).collect(Collectors.toList());
                m.put(i, l);
            }
            r.put(groupNum, m);
        }

        return r;
    }
}
