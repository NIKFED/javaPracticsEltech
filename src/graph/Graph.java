package graph;

import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.*;
import java.util.List;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import static java.lang.System.exit;

public class Graph {
    protected CustomUndirectedWeightedGraph <String, MyWeightedEdge> graph; //сам граф

    protected double[][] adjacencyMatrix; //матрица смежности

    protected static final double INF = Double.POSITIVE_INFINITY;

    public Graph () { //конструктор класса графа

        graph = new CustomUndirectedWeightedGraph<String, MyWeightedEdge>(MyWeightedEdge.class);
    }

    public Graph (Set<MyWeightedEdge> edges) { //конструктор класса графа

        graph = new CustomUndirectedWeightedGraph<String, MyWeightedEdge>(MyWeightedEdge.class);
    }

    public void readGraph(String path) throws IOException { //ввод графа из файла
        FileReader file = new FileReader(path);
        CharBuffer buf = CharBuffer.allocate(10000);
        file.read(buf);
        file.close();
        String current_string = "";
        List<String> graph_txt = new ArrayList<String>();
        for (char ch : buf.array()) {
            current_string += ch;
            if (ch == '\n') {
                graph_txt.add(current_string);
                current_string = "";
            }
        }
        for (int i = 0; i < graph_txt.size(); ++i) {
            String[] split_command = new String[3];
            split_command = graph_txt.get(i).split(" ");
            graph.addVertex(split_command[0]);
            graph.addVertex(split_command[1]);
            graph.addEdge(split_command[0], split_command[1]);
            graph.setEdgeWeight(graph.getEdge(split_command[0], split_command[1]), new Double(split_command[2]));
        }
    }

    public double[][] createAdjacencyMatrix(CustomUndirectedWeightedGraph<String, MyWeightedEdge> simpleG) { //создание матрицы смежности
        HashMap<String, Integer> contributorIndex = new HashMap<String, Integer>();
        int i = 0;
        for (String v : simpleG.vertexSet()) {
            contributorIndex.put(v, i);
            i++;
        }
        double[][] matrix = new double[simpleG.vertexSet().size()][simpleG.vertexSet().size()];
        for (MyWeightedEdge e : simpleG.edgeSet()) {
            String nodeI = simpleG.getEdgeSource(e);
            String nodeF = simpleG.getEdgeTarget(e);
            if(simpleG.containsEdge(e)) {
                double w = simpleG.getEdgeWeight(e);
                int j = contributorIndex.get(nodeI);
                int k = contributorIndex.get(nodeF);
                matrix[j][k] = w;
                matrix[k][j] = w;
            }
        }
        for (int v1 = 0; v1 < simpleG.vertexSet().size(); ++v1) {
            for(int v2 = 0; v2 < simpleG.vertexSet().size(); ++v2) {
                if(matrix[v1][v2] == 0.0) {
                    matrix[v1][v2] = INF;
                }
            }
        }
        return matrix;
    }

    public void makeAdjacencyMatrix() { //ну вы поняли

        this.adjacencyMatrix = this.createAdjacencyMatrix(this.graph);
    }

    public double[][] getAdjacencyMatrix() {

        return adjacencyMatrix;
    }

    public CustomUndirectedWeightedGraph<String, MyWeightedEdge> getGraph() {

        return graph;
    }

    public Graph go() {
        makeAdjacencyMatrix();
        Graph mst = new Graph();
        int n = graph.vertexSet().size();
        Vector<Boolean> used = new Vector<Boolean>(n);
        Vector<Double> min_e = new Vector<Double>(n);
        min_e.ensureCapacity(n);
        used.ensureCapacity(n);
        for(int i = 0; i < n; ++i) {
            used.add(i, false);
        }
        for(int i = 0; i < n; ++i) {
            min_e.add(i, INF);
        }
        Vector<Integer> sel_e = new Vector<Integer>(n);
        for(int i = 0; i < n; ++i) {
            sel_e.add(i, -1);
        }
        min_e.setElementAt(0.0, 0);
        for (int i = 0; i < n; ++i) {
            Integer v = -1;
            for (int j = 0; j < n; ++j) {
                if (!used.elementAt(j) && (v == -1 || min_e.elementAt(j) < min_e.elementAt(v)))
                    v = j;
            }
            if (min_e.elementAt(v) == INF) {
                System.out.println("No MST!");
                exit(0);
            }

            used.setElementAt(true, v);
            if (sel_e.elementAt(v) != -1) {
                System.out.println(v + " " + sel_e.elementAt(v));
                mst.getGraph().addVertex(graph.vertexSet().toArray()[v].toString());
                mst.getGraph().addVertex(graph.vertexSet().toArray()[sel_e.elementAt(v)].toString());
                mst.getGraph().addEdge(graph.vertexSet().toArray()[v].toString(), graph.vertexSet().toArray()[sel_e.elementAt(v)].toString());
                mst.getGraph().setEdgeWeight(mst.getGraph().getEdge(graph.vertexSet().toArray()[v].toString(), graph.vertexSet().toArray()[sel_e.elementAt(v)].toString()),
                        adjacencyMatrix[v][sel_e.elementAt(v)]);
            }

            for (int to = 0; to < n; ++to)
                if (adjacencyMatrix[v][to] < min_e.elementAt(to)) {
                    min_e.setElementAt(adjacencyMatrix[v][to], to);
                    sel_e.setElementAt(new Integer(v), to);
                }
        }
        return mst;
    }

    /*
    для получения всех вершин/ребер можно использовать graph.vertexSet()/graph.edgeSet();
     */

    public void printVertices() { //печать вершин графа
        Set<String> vertices = graph.vertexSet();
        System.out.print("Vertices of the graph: ");
        for(String vertex : vertices) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }

    public void printAdjMatrix() { //печать матрицы инцидентности
        for (int i = 0; i < graph.vertexSet().size(); ++i) {
            for(int j = 0; j < graph.vertexSet().size(); ++j) {
                System.out.print(this.adjacencyMatrix[i][j] + ", ");
            }
            System.out.println();
        }
    }



    /* В программе представлена тестовая визуализация,
       которая просто выводит граф, который получился
       после определенных манипуляций, она будет усовершенствована
       и дополнена до полноценной.
     */
    public static void main(String[] args) throws IOException {

        Graph g = new Graph();

        /* Чтение графа из файла,
        его вывод на экран
         */
        g.readGraph("/home/andrey/gr/inp.txt");
        g.printVertices();
        visual gr = new visual(g.getGraph());
        gr.setTitle("Input graph");
        gr.setVisible(true);

        /*Создание mst,
        вывод его на экран
         */
        g.makeAdjacencyMatrix();
        Graph mst = g.go();
        visual grr = new visual(mst.getGraph());
        grr.setTitle("MST");
        grr.setVisible(true);

    }


    public static class MyWeightedEdge extends DefaultWeightedEdge { //переопределение класса ребер

        private double weight = 0;

        public MyWeightedEdge() {
            super();
        }

        @Override
        public String toString() {
            return Double.toString(weight);
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }
    }

    class CustomUndirectedWeightedGraph<V, E> extends DefaultUndirectedWeightedGraph<V, E> { //переопределение класса графа

        public CustomUndirectedWeightedGraph(Class<? extends E> edgeClass) {
            super(edgeClass);
        }

        public void setEdgeWeight(E e, double weight) {
            super.setEdgeWeight(e, weight);
            ((MyWeightedEdge)e).setWeight(weight);
        }
    }

}