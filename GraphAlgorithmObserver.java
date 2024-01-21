import java.util.List;

public interface GraphAlgorithmObserver<V> {

    void notifyBFSStarted();

    void notifyDFSStarted();

    void notifyDijkstraStarted();

    void notifyVertexVisited(V vertex);

    void notifyVertexFinished(V vertex, int cost);

    void notifyBFSComplete();

    void notifyDFSComplete();

    void notifyDijkstraComplete(List<V> path, int totalCost);

}