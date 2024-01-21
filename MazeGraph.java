import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

public class MazeGraph extends WeightedGraph<Juncture> {

    private Maze maze;
    private List<GraphAlgorithmObserver<Juncture>> observers = new ArrayList<>();

    public MazeGraph(Maze maze) {
        this.maze = maze;
        constructGraph();
    }

    private void constructGraph() {
        // adding all junctures as vertices
        for (int x = 0; x < maze.getWidth(); x++) {
            for (int y = 0; y < maze.getHeight(); y++) {
                Juncture j = new Juncture(x, y);
                addVertex(j);
            }
        }

        // connect adjacent junctures
        for (int x = 0; x < maze.getWidth(); x++) {
            for (int y = 0; y < maze.getHeight(); y++) {
                Juncture current = new Juncture(x, y);

                // check and connect right and down directions
                connectIfPossible(current, new Juncture(x + 1, y)); // right
                connectIfPossible(current, new Juncture(x, y + 1)); // down
            }
        }
    }

    private void connectIfPossible(Juncture from, Juncture to) {
        if (to.getX() < maze.getWidth() && to.getY() < maze.getHeight() && !maze.isWallBetween(from, to)) {
            addEdge(from, to); // only connect if there is no wall
        }
    }

    public void addObserver(GraphAlgorithmObserver<Juncture> observer) {
        observers.add(observer);
    }

    // notify methods for observers
    private void notifyObserversAboutVertexVisit(Juncture vertex) {
        for (GraphAlgorithmObserver<Juncture> observer : observers) {
            observer.notifyVertexVisited(vertex);
        }
    }
    @Override
    public Map<Juncture, Juncture> doBFS(Juncture start, Consumer<Juncture> visitAction, Maze maze) {
        notifyObserversAboutVertexVisit(start);
        return super.doBFS(start, visitAction, maze);
    }

    @Override
    public Map<Juncture, Juncture> doDijkstra(Juncture start, Maze maze) {
        return super.doDijkstra(start, maze);
    }
}

enum Direction {
    UP, DOWN, LEFT, RIGHT;
}
