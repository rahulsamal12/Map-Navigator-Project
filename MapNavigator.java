import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

// Use java.util.List explicitly to avoid ambiguity with java.awt.List
import java.util.List;

public class MapNavigator extends JFrame {
    private Graph graph;
    private JTextArea outputArea;
    private JComboBox<String> sourceBox;
    private JComboBox<String> destinationBox;
    private JButton findPathButton;
    private JComboBox<String> themeComboBox; // For theme selection
    private JTabbedPane tabbedPane;
    private GraphPanel graphPanel; // Optional visualization panel
    private JLabel statusLabel; // Status bar

    public MapNavigator() {
        graph = new Graph();
        createMap(graph);
        graph.computeCircularLayout(); // For visualization (if needed)
        setupUI();
    }

    private void setupUI() {
        setTitle("Bhubaneswar Map Navigator");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setLayout(new BorderLayout());

        // Top control panel with dropdowns, theme selection, and button
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Path Finder"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        sourceBox = new JComboBox<>(graph.getAllVertices());
        destinationBox = new JComboBox<>(graph.getAllVertices());
        findPathButton = new JButton("Find Shortest Path");
        findPathButton.setToolTipText("Click to find the shortest path between selected locations");

        // Theme selection combo box with multiple options
        String[] themes = { "Dark", "Light", "IntelliJ", "Darcula" };
        themeComboBox = new JComboBox<>(themes);
        themeComboBox.setSelectedItem("Dark");
        themeComboBox.setToolTipText("Select the UI theme");

        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(new JLabel("Source:"), gbc);
        gbc.gridx = 1;
        topPanel.add(sourceBox, gbc);
        gbc.gridx = 2;
        topPanel.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 3;
        topPanel.add(destinationBox, gbc);
        gbc.gridx = 4;
        topPanel.add(findPathButton, gbc);
        gbc.gridx = 5;
        topPanel.add(new JLabel("Theme:"), gbc);
        gbc.gridx = 6;
        topPanel.add(themeComboBox, gbc);

        add(topPanel, BorderLayout.NORTH);

        // Tabbed pane with two tabs: one for text output and one for graph visualization
        tabbedPane = new JTabbedPane();

        // Output Tab: displays the shortest path as text
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane outputScroll = new JScrollPane(outputArea);
        tabbedPane.addTab("Output", outputScroll);

        // Visualization Tab: Optional panel to draw the graph
        graphPanel = new GraphPanel(graph);
        tabbedPane.addTab("Visualization", graphPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Status bar at the bottom
        statusLabel = new JLabel("Welcome to Bhubaneswar Map Navigator!");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        add(statusLabel, BorderLayout.SOUTH);

        // Action Listeners
        findPathButton.addActionListener(e -> findPath());
        themeComboBox.addActionListener(e -> changeTheme());
    }

    // Compute and display the shortest path as text between locations.
    private void findPath() {
        String src = (String) sourceBox.getSelectedItem();
        String dest = (String) destinationBox.getSelectedItem();
        if (src.equals(dest)) {
            outputArea.setText("Source and destination are the same!");
            statusLabel.setText("Please select different locations.");
            return;
        }
        HashMap<String, Boolean> processed = new HashMap<>();
        if (!graph.containsVertex(src) || !graph.containsVertex(dest) || !graph.hasPath(src, dest, processed)) {
            outputArea.setText("Invalid locations or no path available.");
            statusLabel.setText("No valid path found.");
            return;
        }
        List<String> path = graph.getShortestPath(src, dest);
        if (path == null) {
            outputArea.setText("No path found.");
            statusLabel.setText("No path found.");
            return;
        }
        int distance = graph.dijkstra(src, dest, false);
        String route = String.join(" -> ", path);
        outputArea.setText("Shortest Path:\n" + route + "\nDistance: " + distance + " km");
        statusLabel.setText("Path found successfully!");

        // Start the animation
        graphPanel.animatePath(path);
    }

    // Change theme based on the selection in the combo box
    private void changeTheme() {
        String selectedTheme = (String) themeComboBox.getSelectedItem();
        try {
            switch (selectedTheme) {
                case "Dark":
                    UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
                    break;
                case "Light":
                    UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
                    break;
                case "IntelliJ":
                    UIManager.setLookAndFeel("com.formdev.flatlaf.FlatIntelliJLaf");
                    break;
                case "Darcula":
                    UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");
                    break;
                default:
                    UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
            }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Create the map with your provided locations and edges.
    public static void createMap(Graph g) {
        String[] locations = {
            "Acharya Vihar", "AG Square", "AIIMS", "AMRI Hospital", "Apollo Hospital", "Baramunda",
            "Biju Pattnaik Airport", "C.V. Raman Global University", "CARE Hospital", "Collage of Engineering & Technology",
            "CUTM", "Fire Station", "GITA", "Gohiria Square", "Hi-Tech", "IIIT", "IIT", "IMMT",
            "Infocity Square", "ITER", "Jagamara", "Jaydev Vihar", "Kalinga Hospital Square", "Kalpana Square",
            "Khandagiri", "KIIMS", "KIIT", "KIIT Square", "Lingaraj Mandir", "Lingraj Station", "Master Canteen",
            "OUAT Square", "Patia Square", "Rajmahal Square", "Rasulgarh", "Silicon Institute of Technology",
            "SUM Hospital", "Tomando", "Trident Academy Of Technology", "Utkal Hospital", "Vani Vihar"
        };
        for (String loc : locations) {
            g.addVertex(loc);
        }
        
        // Adding edges with distances (rounded integers)
        g.addEdge("KIIMS", "KIIT", 1);
        g.addEdge("KIIT", "KIIT Square", 1);
        g.addEdge("KIIMS", "Silicon Institute of Technology", 2);
        g.addEdge("Silicon Institute of Technology", "Infocity Square", 1);
        g.addEdge("Infocity Square", "Trident Academy Of Technology", 0);
        g.addEdge("Trident Academy Of Technology", "Utkal Hospital", 3);
        g.addEdge("Utkal Hospital", "CARE Hospital", 4);
        g.addEdge("KIIT Square", "Patia Square", 2);
        g.addEdge("Patia Square", "Kalinga Hospital Square", 3);
        g.addEdge("CARE Hospital", "Kalinga Hospital Square", 1);
        g.addEdge("Infocity Square", "Patia Square", 2);
        g.addEdge("KIIMS", "Patia Square", 3);
        g.addEdge("Kalinga Hospital Square", "Jaydev Vihar", 3);
        g.addEdge("Kalinga Hospital Square", "Apollo Hospital", 4);
        g.addEdge("Apollo Hospital", "Acharya Vihar", 2);
        g.addEdge("Jaydev Vihar", "IMMT", 3);
        g.addEdge("IMMT", "Acharya Vihar", 1);
        g.addEdge("Acharya Vihar", "Vani Vihar", 2);
        g.addEdge("Vani Vihar", "Rasulgarh", 5);
        g.addEdge("Rasulgarh", "Hi-Tech", 2);
        g.addEdge("Master Canteen", "Vani Vihar", 4);
        g.addEdge("Master Canteen", "Acharya Vihar", 4);
        g.addEdge("Master Canteen", "Jaydev Vihar", 6);
        g.addEdge("Rasulgarh", "Kalpana Square", 5);
        g.addEdge("Kalpana Square", "Lingaraj Mandir", 35);
        g.addEdge("Master Canteen", "Rajmahal Square", 3);
        g.addEdge("Rajmahal Square", "Kalpana Square", 1);
        g.addEdge("Rajmahal Square", "AG Square", 1);
        g.addEdge("AG Square", "Jaydev Vihar", 5);
        g.addEdge("Jaydev Vihar", "Fire Station", 5);
        g.addEdge("AG Square", "Acharya Vihar", 5);
        g.addEdge("AG Square", "OUAT Square", 3);
        g.addEdge("AG Square", "Biju Pattnaik Airport", 2);
        g.addEdge("Rajmahal Square", "Lingraj Station", 5);
        g.addEdge("Biju Pattnaik Airport", "Lingraj Station", 4);
        g.addEdge("OUAT Square", "Biju Pattnaik Airport", 3);
        g.addEdge("OUAT Square", "Jagamara", 4);
        g.addEdge("Jagamara", "ITER", 1);
        g.addEdge("ITER", "Lingraj Station", 5);
        g.addEdge("Fire Station", "AG Square", 5);
        g.addEdge("OUAT Square", "Fire Station", 3);
        g.addEdge("Fire Station", "Baramunda", 2);
        g.addEdge("Fire Station", "SUM Hospital", 4);
        g.addEdge("SUM Hospital", "IIIT", 4);
        g.addEdge("Baramunda", "Khandagiri", 2);
        g.addEdge("Khandagiri", "AMRI Hospital", 2);
        g.addEdge("AMRI Hospital", "Collage of Engineering & Technology", 3);
        g.addEdge("Khandagiri", "AIIMS", 5);
        g.addEdge("Khandagiri", "Tomando", 7);
        g.addEdge("Tomando", "Gohiria Square", 3);
        g.addEdge("Gohiria Square", "GITA", 2);
        g.addEdge("Gohiria Square", "C.V. Raman Global University", 1);
        g.addEdge("Gohiria Square", "IIT", 18);
        g.addEdge("Gohiria Square", "CUTM", 14);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new MapNavigator().setVisible(true));
    }
}

// Graph class: Manages vertices, edges, computes shortest paths using Dijkstra's algorithm,
// and computes a circular layout for visualization (if needed).
class Graph {
    Map<String, Map<String, Integer>> adjList;
    Map<String, Point> coordinates;

    public Graph() {
        adjList = new HashMap<>();
        coordinates = new HashMap<>();
    }

    public void addVertex(String vertex) {
        adjList.putIfAbsent(vertex, new HashMap<>());
    }

    public void addEdge(String src, String dest, int weight) {
        addVertex(src);
        addVertex(dest);
        adjList.get(src).put(dest, weight);
        adjList.get(dest).put(src, weight);
    }

    public boolean containsVertex(String vertex) {
        return adjList.containsKey(vertex);
    }

    public String[] getAllVertices() {
        return adjList.keySet().toArray(new String[0]);
    }

    // DFS-based path check
    public boolean hasPath(String src, String dest, HashMap<String, Boolean> processed) {
        if(src.equals(dest)) return true;
        processed.put(src, true);
        for(String neighbor : adjList.get(src).keySet()){
            if(!processed.getOrDefault(neighbor, false)){
                if(hasPath(neighbor, dest, processed)) return true;
            }
        }
        return false;
    }

    // Dijkstra's algorithm to compute shortest distance.
    public int dijkstra(String src, String dest, boolean printPath) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));
        for(String vertex : adjList.keySet()){
            distances.put(vertex, Integer.MAX_VALUE);
        }
        distances.put(src, 0);
        pq.add(new Node(src, 0));
        while(!pq.isEmpty()){
            Node current = pq.poll();
            String currentVertex = current.vertex;
            if(currentVertex.equals(dest)) break;
            for(Map.Entry<String, Integer> entry: adjList.get(currentVertex).entrySet()){
                String neighbor = entry.getKey();
                int weight = entry.getValue();
                int newDist = distances.get(currentVertex) + weight;
                if(newDist < distances.get(neighbor)){
                    distances.put(neighbor, newDist);
                    previous.put(neighbor, currentVertex);
                    pq.add(new Node(neighbor, newDist));
                }
            }
        }
        return distances.get(dest) == Integer.MAX_VALUE ? -1 : distances.get(dest);
    }

    // Returns the shortest path as a list of vertices from src to dest.
    public List<String> getShortestPath(String src, String dest) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));
        for(String vertex : adjList.keySet()){
            distances.put(vertex, Integer.MAX_VALUE);
        }
        distances.put(src, 0);
        pq.add(new Node(src, 0));
        while(!pq.isEmpty()){
            Node current = pq.poll();
            String currentVertex = current.vertex;
            if(currentVertex.equals(dest)) break;
            for(Map.Entry<String, Integer> entry: adjList.get(currentVertex).entrySet()){
                String neighbor = entry.getKey();
                int weight = entry.getValue();
                int newDist = distances.get(currentVertex) + weight;
                if(newDist < distances.get(neighbor)){
                    distances.put(neighbor, newDist);
                    previous.put(neighbor, currentVertex);
                    pq.add(new Node(neighbor, newDist));
                }
            }
        }
        if(distances.get(dest) == Integer.MAX_VALUE)
            return null;
        LinkedList<String> path = new LinkedList<>();
        for(String at = dest; at != null; at = previous.get(at))
            path.addFirst(at);
        return path;
    }

    static class Node {
        String vertex;
        int distance;
        public Node(String vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }

    // Compute a circular layout for vertices (if needed for visualization)
    public void computeCircularLayout() {
        int n = adjList.size();
        int centerX = 400;
        int centerY = 300;
        int radius = 250;
        int i = 0;
        for(String vertex : adjList.keySet()){
            double angle = 2 * Math.PI * i / n;
            int x = centerX + (int)(radius * Math.cos(angle));
            int y = centerY + (int)(radius * Math.sin(angle));
            coordinates.put(vertex, new Point(x, y));
            i++;
        }
    }
    
    public Map<String, Point> getCoordinates() {
        return coordinates;
    }
}

// GraphPanel: Optional visualization panel (not used in output).
// This panel shows a circular layout of vertices with colorful markers and arrowheads on edges.
class GraphPanel extends JPanel {
    private Graph graph;
    private Map<String, Color> vertexColors;
    private List<Point> pathPoints; // Points of the path to animate
    private int currentIndex = 0; // Current index in the path
    private javax.swing.Timer animationTimer; // Timer for animation
    private final int TIMER_DELAY = 100; // Delay in milliseconds

    public GraphPanel(Graph graph) {
        this.graph = graph;
        setBackground(Color.WHITE);
        assignRandomColors();
    }
    
    private void assignRandomColors() {
        vertexColors = new HashMap<>();
        Color[] palette = { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.CYAN,
                              new Color(128, 0, 128), new Color(0, 128, 128) };
        Random rand = new Random();
        for (String vertex : graph.getAllVertices()) {
            vertexColors.put(vertex, palette[rand.nextInt(palette.length)]);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Map<String, Point> coords = graph.getCoordinates();
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        
        // Draw edges once per pair (avoid duplicate arrows for undirected graph)
        Set<String> drawn = new HashSet<>();
        for (String vertex : graph.getAllVertices()) {
            Point p1 = coords.get(vertex);
            for (Map.Entry<String, Integer> entry : graph.adjList.get(vertex).entrySet()) {
                String neighbor = entry.getKey();
                String edgeKey = vertex + "-" + neighbor;
                String reverseKey = neighbor + "-" + vertex;
                if (drawn.contains(edgeKey) || drawn.contains(reverseKey))
                    continue;
                drawn.add(edgeKey);
                Point p2 = coords.get(neighbor);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
        
        // Draw vertices with colorful circles
        for (String vertex : graph.getAllVertices()) {
            Point p = coords.get(vertex);
            Color c = vertexColors.get(vertex);
            g2.setColor(c);
            g2.fillOval(p.x - 12, p.y - 12, 24, 24);
            g2.setColor(Color.BLACK);
            g2.drawOval(p.x - 12, p.y - 12, 24, 24);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString(vertex, p.x + 15, p.y + 5);
        }

        // Draw the animated path point
        if (pathPoints != null && currentIndex < pathPoints.size()) {
            Point currentPoint = pathPoints.get(currentIndex);
            g2.setColor(Color.RED);
            g2.fillOval(currentPoint.x - 5, currentPoint.y - 5, 10, 10); // Draw a small circle
        }
    }

    public void animatePath(List<String> path) {
        // Convert path to points
        pathPoints = new ArrayList<>();
        for (String vertex : path) {
            pathPoints.add(graph.getCoordinates().get(vertex));
        }
        currentIndex = 0; // Reset index

        // Stop any existing animation
        if (animationTimer != null) {
            animationTimer.stop();
        }

        // Start the animation
        animationTimer = new javax.swing.Timer(TIMER_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentIndex < pathPoints.size()) {
                    currentIndex++;
                    repaint(); // Update the display
                } else {
                    animationTimer.stop(); // Stop the timer when the end is reached
                }
            }
        });
        animationTimer.start(); // Start the animation
    }
}