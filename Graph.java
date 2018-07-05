package graph;

import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.*;
import java.util.List;
import javax.swing.*;
import java.awt.*;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import static java.lang.System.exit;
import static java.lang.System.in;
import static java.lang.System.setErr;

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
                //System.out.println(v + " " + sel_e.elementAt(v));
                createEdge(v, sel_e.elementAt(v), arrayEdges);
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

    ArrayList<index12> arrayEdges = new ArrayList<>();

    class index12 {
        private int index1, index2;

        index12(int index1, int index2) {
            this.index1 = index1;
            this.index2 = index2;
        }

        public int getIndex1() {
            return index1;
        }

        public int getIndex2() {
            return index2;
        }

        private void printEdge() {
            System.out.println("First edge: " + index1 + ", second edge: " + index2);
        }
    }

    public void createEdge(int first, int second, ArrayList<index12> minEdges) {
        index12 temp = new index12(first, second);
        arrayEdges.add(temp);
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
        g.readGraph("C://Users//User//Desktop//test//graph.txt");
        g.printVertices();
        Graph minimalSpanningTree = g.go();
        g.visualizationGraph();
        g.printAdjMatrix();


    }

    public void visualizationGraph() {
        JFrame frame = new JFrame("Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MyDrawPanel drawPanel = new MyDrawPanel();

        frame.getContentPane().add(drawPanel);
        frame.setSize(800, 800);
        frame.setVisible(true);
        //printVertex(drawPanel);
        Set<String> vertices = graph.vertexSet();
        Random randNumber = new Random();
        choice = 0;
        int rad = 200;
        double seg = 2*3.14/ vertices.size();
        if ( vertices.size() >= 16) {
            rad = vertices.size()*70/6;
        }
        double angle = 0;
        for (String vertex : vertices) {
            x = (int) (rad * Math.cos(angle)) + 300;
            y = (int) (rad * Math.sin(angle)) + 300;
            angle += seg;
            name = vertex;
            drawPanel.repaint();
            try{
                Thread.sleep(100);
            }catch(Exception ex){}
        }

        for (int i = 0; i < drawPanel.points.size(); i++) {
            drawPanel.points.get(i).printPoint();
        }

        choice = 1;
        ind = 0;
        System.out.println("Minimal tree: ");
        try{
            Thread.sleep(500);
        }catch(Exception ex){}
        for (int i = 0; i < arrayEdges.size(); i++) {
            arrayEdges.get(i).printEdge();
            drawPanel.repaint();
            try{
                Thread.sleep(500);
            }catch(Exception ex){}
        }

    }

    int x, y;
    String name;
    int ind = 0;
    int choice;

    class MyDrawPanel extends JPanel {

        ArrayList<Point> points = new ArrayList<>();

        class Point {
            Point(int x, int y, int index) {
                this.x = x;
                this.y = y;
                this.index = index;
            }

            public void printPoint() {
                System.out.println("x: " + x + ", y: " + y + ", index: " + index);
            }

            private int getX() {
                return x;
            }

            private int getY() {
                return y;
            }

            private int x, y;
            private int index;
        }

        public void paintComponent(Graphics g) {
            if (choice == 0) {
                Point p;
                p = new Point(x, y, ind);
                ind++;
                points.add(p);
                g.setColor(Color.black);
                g.fillOval(x, y, 10, 10);
                Graphics2D g2 = (Graphics2D) g;
                Font f = new Font("SansSerif", Font.BOLD, 15);
                g2.setFont(f);
                g2.setColor(Color.black);
                g2.drawString(name, x - 15, y - 5);
                Font f2 = new Font("SansSerif", Font.BOLD, 9);
                //System.out.println(name + "  -    x =  " + x + "  y = " + y);
            }
            else if (choice == 1) {
                g.setColor(Color.red);
                g.drawLine(points.get(arrayEdges.get(ind).getIndex1()).getX() + 5,
                           points.get(arrayEdges.get(ind).getIndex1()).getY() + 5,
                           points.get(arrayEdges.get(ind).getIndex2()).getX() + 5,
                           points.get(arrayEdges.get(ind).getIndex2()).getY() + 5);
                ind++;
            }
        }
    }

    /*public void visualizationGraph() {
        JFrame frame = new JFrame("Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MyDrawPanel drawPanel = new MyDrawPanel();

        frame.getContentPane().add(drawPanel);
        frame.setSize(800, 800);
        frame.setVisible(true);
        //printVertex(drawPanel);


    }

    class MyDrawPanel extends JPanel {

        ArrayList<Point> points;

        class Point {
            Point(int x, int y, int index) {
                this.x = x;
                this.y = y;
                this.index = index;
            }

            private void printPoint() {
                System.out.println("x: " + x + ", y: " + y + ", index: " + index);
            }

            private int getX() {
                return x;
            }

            private int getY() {
                return y;
            }

            private int x, y;
            private int index;
        }

        public void paintComponent(Graphics g) {
            ArrayList<Point> points = new ArrayList();
            Random randNumber = new Random();
            super.paintComponent(g);
            g.setColor(Color.black);
            Set<String> vertices = graph.vertexSet();
            int x, y;
            String name;
            Point p;
            int ind = 0;
            for (String vertex : vertices) {
                x = randNumber.nextInt(500) + 1;
                y = randNumber.nextInt(500) + 1;
                p = new Point(x, y, ind);
                ind++;
                points.add(p);
                name = vertex;
                g.fillOval(x, y, 10, 10);
                Graphics2D g2 = (Graphics2D) g;
                Font f = new Font("SansSerif", Font.BOLD, 15);
                g2.setFont(f);
                g2.setColor(Color.black);
                g2.drawString(name, x - 15, y - 5);
                Font f2 = new Font("SansSerif", Font.BOLD, 9);
                //System.out.println(name + "  -    x =  " + x + "  y = " + y);
            }
            for (int i = 0; i < points.size(); i++) {
                points.get(i).printPoint();
            }
            g.setColor(Color.red);
            g.drawLine(points.get(2).getX(), points.get(2).getY(), points.get(0).getX(), points.get(0).getY());
            g.setColor(Color.red);
            g.drawLine(points.get(1).getX(), points.get(1).getY(), points.get(0).getX(), points.get(0).getY());
        }
    }*/

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

