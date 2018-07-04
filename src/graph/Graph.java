package graph;

import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.*;
import java.util.List;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Graph {
    protected CustomUndirectedWeightedGraph <String, MyWeightedEdge> graph; //сам граф

    protected double[][] interactionMatrix; //матрица инцидентности

    protected double[][] adjacencyMatrix;

    public Graph () { //конструктор класса графа

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

    public double[][] createInteractionMatrix(CustomUndirectedWeightedGraph<String, MyWeightedEdge> simpleG) { //создание матрицы инцидентности
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
            double w = simpleG.getEdgeWeight(e);
            int j = contributorIndex.get(nodeI);
            int k = contributorIndex.get(nodeF);
            matrix[j][k] = w;
            matrix[k][j] = w;
        }
        return matrix;
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
            double w = simpleG.getEdgeWeight(e);
            int j = contributorIndex.get(nodeI);
            int k = contributorIndex.get(nodeF);
            if (w > 0.0) {
                matrix[j][k] = 1;
                matrix[k][j] = 1;
            }
        }
        return matrix;
    }

    public void makeInteractionMatrix() { //ну вы поняли

        this.interactionMatrix = this.createInteractionMatrix(this.graph);
    }

    public void makeAdjacencyMatrix() { //ну вы поняли

        this.adjacencyMatrix = this.createAdjacencyMatrix(this.graph);
    }

    public double[][] getInteractionMatrix() {

        return interactionMatrix;
    }

    public double[][] getAdjacencyMatrix() {

        return adjacencyMatrix;
    }

    public CustomUndirectedWeightedGraph<String, MyWeightedEdge> getGraph() {

        return graph;
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

    public void printIntMatrix() { //печать матрицы инцидентности
        for (int i = 0; i < graph.vertexSet().size(); ++i) {
            for(int j = 0; j < graph.vertexSet().size(); ++j) {
                System.out.print(this.interactionMatrix[i][j] + ", ");
            }
            System.out.println();
        }
    }

    public void printAdjMatrix() { //печать матрицы инцидентности
        for (int i = 0; i < graph.vertexSet().size(); ++i) {
            for(int j = 0; j < graph.vertexSet().size(); ++j) {
                System.out.print(this.adjacencyMatrix[i][j] + ", ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws IOException {

        Graph g = new Graph();

        g.readGraph("/home/andrey/gr/inp.txt");
        g.printVertices();
        g.makeInteractionMatrix();
        g.makeAdjacencyMatrix();

        //g.printIntMatrix();
        //g.printAdjMatrix();

        //visual gr = new visual(g.getGraph());  визуализация графа
        //gr.setVisible(true);

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