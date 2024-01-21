import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MazeGUI extends JPanel implements GraphAlgorithmObserver<Juncture> {

    private JTextField startXField, startYField, endXField, endYField;
    private MazeGraph graph;
    private Maze maze;
    private Juncture start, end;
    private List<Juncture> currentPath;
    private long lastExecutionTime;

    public MazeGUI(Maze maze) {
        this.maze = maze;
        this.graph = new MazeGraph(maze);
        graph.addObserver(this);

        // initializing default start and end junctures
        start = new Juncture(0, 0);
        end = new Juncture(maze.getWidth() - 1, maze.getHeight() - 1);

        setPreferredSize(new Dimension(500, 500));
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        startXField = new JTextField(2);
        startYField = new JTextField(2);
        endXField = new JTextField(2);
        endYField = new JTextField(2);

        controlPanel.add(new JLabel("Start X:"));
        controlPanel.add(startXField);
        controlPanel.add(new JLabel("Start Y:"));
        controlPanel.add(startYField);
        controlPanel.add(new JLabel("End X:"));
        controlPanel.add(endXField);
        controlPanel.add(new JLabel("End Y:"));
        controlPanel.add(endYField);

        addButtons(controlPanel);
        add(controlPanel, BorderLayout.NORTH);
    }

    private void addButtons(JPanel panel) {
        JButton bfsButton = new JButton("Run BFS");
        bfsButton.addActionListener(e -> runBFS());
        panel.add(bfsButton);

        JButton dfsButton = new JButton("Run DFS");
        dfsButton.addActionListener(e -> runDFS());
        panel.add(dfsButton);

        JButton dijkstraButton = new JButton("Run Dijkstra");
        dijkstraButton.addActionListener(e -> runDijkstra());
        panel.add(dijkstraButton);

        JButton reloadButton = new JButton("Reload Maze");
        reloadButton.addActionListener(e -> reloadMaze());
        panel.add(reloadButton);
    }

    private void reloadMaze() {
        maze = new Maze(maze.getWidth(), maze.getHeight()); // create new maze with same dimensions
        graph = new MazeGraph(maze); // reinitialize graph with new maze
        currentPath = null; // reset current path
        repaint(); // repaint to display new maze
    }

    private void updateJunctureFromInput(JTextField xField, JTextField yField, boolean isStart) {
        try {
            int x = Integer.parseInt(xField.getText());
            int y = Integer.parseInt(yField.getText());
            if (x < 0 || y < 0 || x >= maze.getWidth() || y >= maze.getHeight()) {
                throw new IllegalArgumentException("Coordinates out of bounds");
            }
            Juncture juncture = new Juncture(x, y);
            if (isStart) {
                start = juncture;
            } else {
                end = juncture;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter numeric coordinates.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void runBFS() {
        updateJunctureFromInput(startXField, startYField, true);  // update start juncture
        updateJunctureFromInput(endXField, endYField, false);     // update end juncture

        new SwingWorker<Void, Juncture>() {
            @Override
            protected Void doInBackground() throws Exception {
                graph.doBFS(start, this::publish, maze);
                return null;
            }

            @Override
            protected void done() {
                try {
                    Map<Juncture, Juncture> predecessors = graph.doBFS(start, juncture -> {}, maze);
                    if (!predecessors.containsKey(end) || maze.isWallAt(end)) {
                        JOptionPane.showMessageDialog(MazeGUI.this, "No path found to the destination.");
                        currentPath = Collections.emptyList();
                    } else {
                        currentPath = reconstructPath(predecessors, start, end);
                    }
                    repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void runDFS() {
        updateJunctureFromInput(startXField, startYField, true);  // update start juncture
        updateJunctureFromInput(endXField, endYField, false);     // update end juncture

        new SwingWorker<Void, Juncture>() {
            @Override
            protected Void doInBackground() throws Exception {
                graph.doDFS(start, this::publish, maze);
                return null;
            }

            @Override
            protected void done() {
                try {
                    Map<Juncture, Juncture> predecessors = graph.doDFS(start, juncture -> {}, maze);
                    if (!predecessors.containsKey(end) || maze.isWallAt(end)) {
                        JOptionPane.showMessageDialog(MazeGUI.this, "No path found to the destination.");
                        currentPath = Collections.emptyList();
                    } else {
                        currentPath = reconstructPath(predecessors, start, end);
                    }
                    repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void runDijkstra() {
        updateJunctureFromInput(startXField, startYField, true);  // update start juncture
        updateJunctureFromInput(endXField, endYField, false);     // update end juncture

        new SwingWorker<Void, Juncture>() {
            @Override
            protected Void doInBackground() throws Exception {
                graph.doDijkstra(start, maze);
                return null;
            }

            @Override
            protected void done() {
                try {
                    Map<Juncture, Juncture> predecessors = graph.doDijkstra(start, maze);
                    if (!predecessors.containsKey(end) || maze.isWallAt(end)) {
                        JOptionPane.showMessageDialog(MazeGUI.this, "No path found to the destination.");
                        currentPath = Collections.emptyList();
                    } else {
                        currentPath = reconstructPath(predecessors, start, end);
                    }
                    repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private LinkedList<Juncture> reconstructPath(Map<Juncture, Juncture> predecessors, Juncture start, Juncture end) {
        LinkedList<Juncture> path = new LinkedList<>();
        Juncture step = end;

        // checking if a path exists
        if (predecessors.get(step) == null) {
            return path;
        }

        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }
        Collections.reverse(path);
        return path;
    }

    private void drawPath(Graphics g, List<Juncture> path, Maze maze) {
        g.setColor(Color.RED);
        int cellWidth = getWidth() / maze.getWidth();
        int cellHeight = getHeight() / maze.getHeight();

        for (Juncture j : path) {
            if (!maze.isWallAt(j)) { // making sure juncture is not a wall
                int cellX = j.getX() * cellWidth;
                int cellY = j.getY() * cellHeight;
                g.fillRect(cellX, cellY, cellWidth, cellHeight);
            }
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMaze(g);
        if (currentPath != null) {
            drawPath(g, currentPath, maze);
        }
    }

    private void drawMaze(Graphics g) {
        int cellWidth = getWidth() / maze.getWidth();
        int cellHeight = getHeight() / maze.getHeight();
        Font font = new Font("Arial", Font.PLAIN, 10);

        for (int x = 0; x < maze.getWidth(); x++) {
            for (int y = 0; y < maze.getHeight(); y++) {
                int cellX = x * cellWidth;
                int cellY = y * cellHeight;

                // drawing walls or paths
                if (x < maze.getWidth() - 1 && !maze.isWallBetween(new Juncture(x, y), new Juncture(x + 1, y)) ||
                        y < maze.getHeight() - 1 && !maze.isWallBetween(new Juncture(x, y), new Juncture(x, y + 1))) {
                    g.setColor(Color.WHITE);
                } else {
                    g.setColor(Color.BLACK);
                }
                g.fillRect(cellX, cellY, cellWidth, cellHeight);

                // coordinates
                g.setColor(Color.BLUE);
                g.setFont(font);
                g.drawString(x + "," + y, cellX + 5, cellY + 12);
            }
        }
    }

    // GraphAlgorithmObserver methods
    @Override
    public void notifyBFSStarted() { }
    @Override
    public void notifyDFSStarted() { }
    @Override
    public void notifyDijkstraStarted() { }
    @Override
    public void notifyVertexVisited(Juncture juncture) { }
    @Override
    public void notifyVertexFinished(Juncture vertex, int cost) { }
    @Override
    public void notifyBFSComplete() { }
    @Override
    public void notifyDFSComplete() { }
    @Override
    public void notifyDijkstraComplete(List<Juncture> path, int totalCost) { }

    public static void main(String[] args) {
        Maze maze = new Maze(10, 10);
        JFrame frame = new JFrame("Maze GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new MazeGUI(maze)); // Maze object
        frame.pack();
        frame.setVisible(true);
    }
}
