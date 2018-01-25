import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.LinkedList;
import java.util.HashMap;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */
    public class Node {
        private long id;
        private double lat;
        private double lon;
        private String name;

        public long getId() {
            return id;
        }

        public Node(long i, double l, double lo) {
            name = "";
            id = i;
            lon = l;
            lat = lo;
        }
        public void setName(String nodeName) {
            name = nodeName;
        }

    }

    private HashMap<Long, Node> nodeMap = new HashMap<>();
    private HashMap<Long, LinkedList<Node>> adjacencyMap = new HashMap<>();

    public HashMap<Long, Node> getNodeMap() {
        return nodeMap;
    }

    public HashMap<Long, LinkedList<Node>> getAdjacencyMap() {
        return adjacencyMap;
    }

    //    public LinkedList<Long> iterable = new LinkedList<>();

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputFile, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        LinkedList<Long> tBR = new LinkedList<>();
        for (Long l : vertices()) {
            if (adjacencyMap.get(l).size() == 0) {
                tBR.add(l);
            }
        }
        for (Long l : tBR) {
            adjacencyMap.remove(l);
            nodeMap.remove(l);
        }
    }

    /** Returns an iterable of all vertex IDs in the graph. */
    Iterable<Long> vertices() {
        return getAdjacencyMap().keySet();
    }

    /** Returns ids of all vertices adjacent to v. */
    Iterable<Long> adjacent(long v) {
        LinkedList<Long> allAdjacentIDs = new LinkedList<>();
        for (Node n : getAdjacencyMap().get(v)) {
            allAdjacentIDs.add(n.id);
        }
        return allAdjacentIDs;
    }

    /** Returns the Euclidean distance between vertices v and w, where Euclidean distance
     *  is defined as sqrt( (lonV - lonV)^2 + (latV - latV)^2 ). */
    double distance(long v, long w) {
        return Math.sqrt(Math.pow(lon(v) - lon(w), 2) + Math.pow(lat(v) - lat(w), 2));
    }

    /** Returns the vertex id closest to the given longitude and latitude. */
    long closest(double lon, double lat) {
        Long closest = Long.valueOf(0);
        double difference = 2131312319;
        for (HashMap.Entry<Long, Node> e : getNodeMap().entrySet()) {
            double computedDiff = Math.sqrt(Math.pow(lon(e.getKey()) - lon, 2)
                    + Math.pow(lat(e.getKey()) - lat, 2));
            if (computedDiff < difference) {
                difference = computedDiff;
                closest = e.getKey();
            }
        }
        return closest;
    }




        /** Longitude of vertex v. */
    double lon(long v) {
        return nodeMap.get(v).lon;
    }

    /** Latitude of vertex v. */
    double lat(long v) {
        return nodeMap.get(v).lat;
    }

    void addNode(long id, double lon, double lat) {
        Node toAdd = new Node(id, lon, lat);
        nodeMap.put(id, toAdd);
        adjacencyMap.put(id, new LinkedList<>());
    }

    void addEdge(long id1, long id2) {
        Node one = nodeMap.get(id1);
        Node two = nodeMap.get(id2);
        adjacencyMap.get(id1).add(two);
        adjacencyMap.get(id2).add(one);
    }

    void addWay(LinkedList<GraphBuildingHandler.PairLongs> nodeIDs) {
        for (GraphBuildingHandler.PairLongs p : nodeIDs) {
            addEdge(p.getLeft(), p.getRight());
        }
    }


}
