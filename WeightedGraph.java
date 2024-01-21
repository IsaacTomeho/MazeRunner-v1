import java.util.*;
import java.util.function.Consumer;

public class WeightedGraph<V> {
    private Map<V, List<V>> graph;

    public WeightedGraph() {
        graph = new HashMap<>();
    }

    public void addVertex(V vertex) {
        if (!graph.containsKey(vertex)) {
            graph.put(vertex, new ArrayList<>());
        }
    }

    public void addEdge(V from, V to) {
        if (!graph.containsKey(from) || !graph.containsKey(to)) {
            throw new IllegalArgumentException("Invalid vertex");
        }
        graph.get(from).add(to);
        graph.get(to).add(from); // Assuming undirected graph
    }

    public boolean containsVertex(V vertex) {
        return graph.containsKey(vertex);
    }

    public Map<V, V> doBFS(V start, Consumer<V> visitAction, Maze maze) {
        Map<V, V> predecessor = new HashMap<>();
        Set<V> visited = new HashSet<>();
        Queue<V> queue = new LinkedList<>();

        queue.add(start);
        visited.add(start);
        predecessor.put(start, null);
        visitAction.accept(start);

        while (!queue.isEmpty()) {
            V current = queue.poll();

            for (V neighbor : graph.get(current)) {
                if (!visited.contains(neighbor) && !maze.isWallAt((Juncture)neighbor)) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                    predecessor.put(neighbor, current);
                    visitAction.accept(neighbor);
                }
            }
        }
        return predecessor;
    }

    public Map<V, V> doDFS(V start, Consumer<V> visitAction, Maze maze) {
        Map<V, V> predecessor = new HashMap<>();
        Set<V> visited = new HashSet<>();
        dfsVisit(start, null, visited, predecessor, visitAction, maze); // passing maze object here
        return predecessor;
    }

    private void dfsVisit(V current, V parent, Set<V> visited, Map<V, V> predecessor, Consumer<V> visitAction, Maze maze) {
        visited.add(current);
        if (!maze.isWallAt((Juncture)current)) { // cast V to Juncture if V is a generic type
            predecessor.put(current, parent);
            visitAction.accept(current);

            for (V neighbor : graph.get(current)) {
                if (!visited.contains(neighbor) && !maze.isWallAt((Juncture)neighbor)) {
                    dfsVisit(neighbor, current, visited, predecessor, visitAction, maze);
                }
            }
        }
    }

    public Map<V, V> doDijkstra(V start, Maze maze) {
        Map<V, V> predecessors = new HashMap<>();
        Map<V, Integer> distances = new HashMap<>();
        PriorityQueue<V> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (V vertex : graph.keySet()) {
            distances.put(vertex, Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            V current = queue.poll();

            for (V neighbor : graph.get(current)) {
                if (!maze.isWallAt((Juncture)neighbor)) {
                    int alt = distances.get(current) + 1; // assuming all edges have a weight of 1
                    if (alt < distances.get(neighbor)) {
                        distances.put(neighbor, alt);
                        predecessors.put(neighbor, current);
                        queue.add(neighbor);
                    }
                }
            }
        }
        return predecessors;
    }

}