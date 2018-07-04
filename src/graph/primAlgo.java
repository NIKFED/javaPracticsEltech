package graph;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;

import java.lang.reflect.Array;
import java.util.*;

public class primAlgo<V, E>
        implements
        SpanningTreeAlgorithm<E>
{
    private final Graph<V, E> g;

    /**
     * Construct a new instance of the algorithm.
     *
     * @param graph the input graph
     */
    public primAlgo(Graph<V, E> graph)
    {
        this.g = Objects.requireNonNull(graph, "Graph cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public SpanningTree<E> getSpanningTree()
    {
        Set<E> minimumSpanningTreeEdgeSet = new HashSet<>(g.vertexSet().size());
        double spanningTreeWeight = 0d;

        final int N = g.vertexSet().size();

        /*
         * Normalize the graph map each vertex to an integer (using a HashMap) keep the reverse
         * mapping (using an ArrayList)
         */
        Map<V, Integer> vertexMap = new HashMap<>();
        List<V> indexList = new ArrayList<>();
        for (V v : g.vertexSet()) {
            vertexMap.put(v, vertexMap.size());
            indexList.add(v);
        }

        VertexInfo[] vertices = (VertexInfo[]) Array.newInstance(VertexInfo.class, N);
        FibonacciHeapNode<VertexInfo>[] fibNodes =
                (FibonacciHeapNode<VertexInfo>[]) Array.newInstance(FibonacciHeapNode.class, N);
        FibonacciHeap<VertexInfo> fibonacciHeap = new FibonacciHeap<>();

        for (int i = 0; i < N; i++) {
            vertices[i] = new VertexInfo();
            vertices[i].id = i;
            vertices[i].distance = Double.MAX_VALUE;
            fibNodes[i] = new FibonacciHeapNode<>(vertices[i]);

            fibonacciHeap.insert(fibNodes[i], vertices[i].distance);
        }

        while (!fibonacciHeap.isEmpty()) {
            FibonacciHeapNode<VertexInfo> fibNode = fibonacciHeap.removeMin();
            VertexInfo vertexInfo = fibNode.getData();

            V p = indexList.get(vertexInfo.id);
            vertexInfo.spanned = true;

            // Add the edge from its parent to the spanning tree (if it exists)
            if (vertexInfo.edgeFromParent != null) {
                minimumSpanningTreeEdgeSet.add(vertexInfo.edgeFromParent);
                spanningTreeWeight += g.getEdgeWeight(vertexInfo.edgeFromParent);
            }

            // update all (unspanned) neighbors of p
            for (E e : g.edgesOf(p)) {
                V q = Graphs.getOppositeVertex(g, e, p);
                int id = vertexMap.get(q);

                // if the vertex is not explored and we found a better edge, then update the info
                if (!vertices[id].spanned) {
                    double cost = g.getEdgeWeight(e);

                    if (cost < vertices[id].distance) {
                        vertices[id].distance = cost;
                        vertices[id].edgeFromParent = e;

                        fibonacciHeap.decreaseKey(fibNodes[id], cost);
                    }
                }
            }
        }

        return new SpanningTreeImpl<>(minimumSpanningTreeEdgeSet, spanningTreeWeight);
    }

    private class VertexInfo
    {
        public int id;
        public boolean spanned;
        public double distance;
        public E edgeFromParent;
    }
}
