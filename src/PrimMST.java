import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by haoxiong on 12/15/2017.
 * Copyright belongs to haoxiong, Email: haoxiong@outlook.com
 */
public class PrimMST {

    static void prim(Graph G) {
//      FibonacciHeap<Node> heap = new FibonacciHeap<>();
        PriorityQueue<Graph.Node> queue = new PriorityQueue<>(Comparator.comparingDouble(node -> node.key));
        queue.addAll(Arrays.asList(G.nodes));
        G.nodes[0].key = 0;
        boolean[] marked = new boolean[G.V];
        while (!queue.isEmpty()) {
            Graph.Node u = queue.poll();
            marked[u.id] = true;
            for (int i = 0; i < G.V; i++) {
                double w = G.getWeight(u.id, i);
                Graph.Node v = G.nodes[i];
                if (w > 0 && !marked[i] && w < v.key) {
                    v.parent = u;
                    v.key = w;
                }
            }
        }
    }

    public static void main(String args[]) {
        int V = 8;
        Graph graph = new Graph(V);

        graph.setWeight(0, 7, 0.16);
        graph.setWeight(2, 3, 0.17);
        graph.setWeight(1, 7, 0.19);
        graph.setWeight(0, 2, 0.26);

        graph.setWeight(5, 7, 0.28);
        graph.setWeight(1, 3, 0.29);
        graph.setWeight(1, 5, 0.32);
        graph.setWeight(2, 7, 0.34);

        graph.setWeight(4, 5, 0.35);
        graph.setWeight(1, 2, 0.36);
        graph.setWeight(4, 7, 0.37);
        graph.setWeight(0, 4, 0.38);

        graph.setWeight(6, 2, 0.40);
        graph.setWeight(3, 6, 0.52);
        graph.setWeight(6, 0, 0.58);
        graph.setWeight(6, 4, 0.93);

        PrimMST.prim(graph);

        for(Graph.Node u : graph.nodes)
        {
            Graph.Node v = u.parent;
            if (v!=null)
            System.out.println(u.id + " - " + v.id + " : " + graph.getWeight(u.id, v.id));
        }


    }

}


class Graph {
    private double[][] weights;
    Node[] nodes;
    int V;

    Graph(int V) {
        this.V = V;
        this.weights = new double[V][V];
        this.nodes = new Node[V];
        for (int i = 0; i < V; i++) {
            nodes[i] = new Node(i);
        }
    }

    public void setWeight(int uid, int vid, double weight) {
        if (uid > vid) {
            int k = uid;
            uid = vid;
            vid = k;
        }
        weights[uid][vid] = weight;
    }

    public double getWeight(int uid, int vid) {
        if (uid > vid) {
            int k = uid;
            uid = vid;
            vid = k;
        }
        return this.weights[uid][vid];
    }

    public boolean hasEdge(int uid, int vid) {
        return getWeight(uid, vid) > 0;
    }

    class Node {
        int id;
        Node parent;
        double key;

        Node(int id) {
            this.id = id;
            parent = null;
            key = Long.MAX_VALUE;
        }
    }
}
