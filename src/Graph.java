import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by haoxiong on 12/15/2017.
 * Copyright belongs to haoxiong, Email: haoxiong@outlook.com
 */
public class Graph {
    private double[][] weights;
    Node[] nodes;
    private int V;

    Graph(int V) {
        this.V = V;
        this.weights = new double[V][V];
        this.nodes = new Node[V];
        for (int i = 0; i < V; i++) {
            nodes[i] = new Node(i);
        }
    }

    public void prim() {
        //Is it necessary to use Fibonacci Heap?
        // FibonacciHeap<Node> heap = new FibonacciHeap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(n -> n.key));
        nodes[V-1].key = Integer.MIN_VALUE;
        queue.addAll(Arrays.asList(this.nodes));
        boolean[] marked = new boolean[this.V];
        while (!queue.isEmpty()) {
            Node u = queue.poll();
            marked[u.id] = true;
            for (int i = 0; i < this.V; i++) {
                double w = this.getWeight(u.id, i);
                if (w != 0) {
                    Node v = this.nodes[i];
                    if (!marked[i] && w < v.key) {
                        queue.remove(v);
                        v.parent = u;
                        v.key = w;
                        queue.add(v);
                    }
                }
            }
        }
    }

    public void printMST() {
        for (Node u : this.nodes) {
            Node v = u.parent;
            if (v != null)
                System.out.print("[" + v.id + " -> " + u.id + " : " + this.getWeight(u.id, v.id) + "]");
        }
        System.out.println();
    }

    public ArrayList<Node> getNeighbors(int root) {
        ArrayList<Node> neibs = new ArrayList<>();
        //add parent
        Node parent = nodes[root].parent;
        if (parent != null) {
            neibs.add(parent);
        }
        //add all children
        for (Node node : nodes) {
            if (node.parent != null && node.parent.id == root) {
                neibs.add(node);
            }
        }
        return neibs;
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


    //Unit Test
    public static void main(String args[]) {
        int V = 8;
        Graph graph = new Graph(V);

        graph.setWeight(0, 7, -0.16);
        graph.setWeight(2, 3, -0.17);
        graph.setWeight(1, 7, -0.19);
        graph.setWeight(0, 2, -0.26);

        graph.setWeight(5, 7, -0.28);
        graph.setWeight(1, 3, -0.29);
        graph.setWeight(1, 5, -0.32);
        graph.setWeight(2, 7, -0.34);

        graph.setWeight(4, 5, -0.35);
        graph.setWeight(1, 2, -0.36);
        graph.setWeight(4, 7, -0.37);
        graph.setWeight(0, 4, -0.38);

        graph.setWeight(6, 2, -0.40);
        graph.setWeight(3, 6, -0.52);
        graph.setWeight(6, 0, -0.58);
        graph.setWeight(6, 4, -0.93);

        graph.prim();
        graph.printMST();
        for (int i = 0; i < V; i++) {
            System.out.println(graph.getNeighbors(i).size());
        }
    }
}

class Node {
    int id;
    Node parent;
    double key;

    Node(int id) {
        this.id = id;
        parent = null;
        key = Integer.MAX_VALUE;
    }

    @Override
    public String toString() {
        return "[Node:" + this.id + ", Key:" + key + ", Parent:" + (parent == null ? "Null" : parent.toString()) + "]";
    }
}