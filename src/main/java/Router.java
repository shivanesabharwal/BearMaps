import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;


/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a LinkedList of <code>Long</code>s representing the shortest path from st to dest,
     * where the longs are node IDs.
     */

    public static class SearchNode implements Comparable {
        private long id;
        private long previous;
        private double priority;

        public SearchNode(long i, double p, long prev) {
            id = i;
            priority = p;
            previous = prev;
        }

        public long getPrevious() {
            return previous;
        }

        public long getId() {
            return id;
        }

        public double getPriority() {
            return priority;
        }

        public void changePriority(double newPriority) {
            priority = newPriority;
        }

        @Override
        public int compareTo(Object o) {
            SearchNode toCompare = (SearchNode) o;
            if (priority < toCompare.getPriority()) {
                return -1;
            } else if (priority > toCompare.getPriority()) {
                return 1;
            } else {
                return 0;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            SearchNode that = (SearchNode) o;

            return id == that.id;
        }

        @Override
        public int hashCode() {
            return (int) (id ^ (id >>> 32));
        }
    }


    public static LinkedList<Long> shortestPath(GraphDB g, double stlon,
                                                double stlat, double destlon, double destlat) {
        /* create empty Linked List return at end */
        LinkedList<Long> solution = new LinkedList<>();

        /* set to keep track of marked nodes */
        HashSet<SearchNode> markedNodes = new HashSet<>();

        /* create distTo and searchNode HashMaps and fringe  */
        HashMap<Long, Double> distTo = new HashMap<>();
        HashMap<Long, SearchNode> allSearchNodes = new HashMap<>();
        PriorityQueue<SearchNode> fringe = new PriorityQueue<>();

        /* get startID and endID of nodes closest to start and destination */
        Long startID = g.closest(stlon, stlat);
        Long endID = g.closest(destlon, destlat);
        SearchNode endNode = null;
        double distSoFar = 0;

        /* 1. create the start SearchNode
           2. add it to the queue
           3. change its Priority to 0
           4. add it to queue
           5. add it to distTo Hash Map
         */

        SearchNode startNode = new SearchNode(startID, 0, 0);
        startNode.changePriority(0);
        distTo.put(startID, Double.valueOf(0));
        allSearchNodes.put(startID, startNode);
        fringe.add(startNode);

        /* add all vertices to the distTo HashMap and searchNodeHashMap and enqueue them */
//        for (Long l : g.vertices()) {
//            if (!l.equals(startID)) {
//                SearchNode toAdd = new SearchNode(l, Double.POSITIVE_INFINITY, 0);
//                allSearchNodes.put(l, toAdd);
//                fringe.add(toAdd);
//            }
//        }

        /* Begin A* algorithm */
        while (!fringe.isEmpty()) {
            SearchNode min = fringe.poll();
            if (markedNodes.contains(min)) {
                continue;
            }
            markedNodes.add(min);
            if (min.getId() == endID) {
                endNode = min;
                break;
            }
            for (Long l : g.adjacent(min.getId())) {
                double distFromMin = distTo.get(min.getId()) + g.distance(min.getId(), l);
                double heuristic = g.distance(endID, l);
                SearchNode theNew = new SearchNode(l, distFromMin + heuristic, min.getId());
                if (!fringe.contains(theNew)) {
                    SearchNode toAdd = new SearchNode(l, Double.POSITIVE_INFINITY, 0);
                    allSearchNodes.put(l, toAdd);
                    fringe.add(toAdd);
                }
                if (l != min.getPrevious()) {
                    if (fringe.contains(theNew)) {
                        if (allSearchNodes.get(theNew.getId()).getPriority()
                                > theNew.getPriority()) {
                            distTo.put(l, distFromMin);
                            allSearchNodes.put(l, theNew);
                            fringe.add(theNew);
                        }
                    }
                }
            }
        }
        SearchNode goBack = endNode;
        while (goBack.getPrevious() != 0) {
            solution.addFirst(goBack.getId());
            goBack = allSearchNodes.get(goBack.getPrevious());
        }
        solution.addFirst(startID);
        return solution;
    }
//
//    public static LinkedList<Long> shortestPath(GraphDB g, double stlon,
//                                                double stlat, double destlon, double destlat) {
//        /* create empty Linked List return at end */
//        LinkedList<Long> solution = new LinkedList<>();
//
//        /* create distTo and searchNode HashMaps and fringe  */
//        HashMap<Long, Double> distTo = new HashMap<>();
//        HashMap<Long, SearchNode> allSearchNodes = new HashMap<>();
//        PriorityQueue<SearchNode> fringe = new PriorityQueue<>();
//
//        /* get startID and endID of nodes closest to start and destination */
//        Long startID = g.closest(stlon, stlat);
//        Long endID = g.closest(destlon, destlat);
//        SearchNode endNode = null;
//        double distSoFar = 0;
//
//        /* 1. create the start SearchNode
//           2. add it to the queue
//           3. change its Priority to 0
//           4. add it to queue
//           5. add it to distTo Hash Map
//         */
//        SearchNode startNode = new SearchNode(startID, 0, 0);
//        startNode.changePriority(0);
//        distTo.put(startID, Double.valueOf(0));
//        allSearchNodes.put(startID, startNode);
//        fringe.add(startNode);
//
//        /* add all vertices to the distTo HashMap and searchNodeHashMap and enqueue them */
//        for (Long l : g.vertices()) {
//            if (!l.equals(startID)) {
//                SearchNode toAdd = new SearchNode(l, Double.POSITIVE_INFINITY, 0);
//                distTo.put(l, Double.POSITIVE_INFINITY);
//                allSearchNodes.put(l, toAdd);
//                fringe.add(toAdd);
//            }
//        }
//
//        /* Begin A* algorithm */
//        while (!fringe.isEmpty()) {
//            SearchNode min = fringe.poll();
//            if (min.getId() == endID) {
//                endNode = min;
//                break;
//            }
//            for (Long l : g.adjacent(min.getId())) {
//                double distFromMin = distTo.get(min.getId()) + g.distance(min.getId(), l);
//                double heuristic = g.distance(startID, l);
//                SearchNode theNew = new SearchNode(l, distFromMin + heuristic, min.getId());
//                if (l != min.getPrevious()) {
//                    if (fringe.contains(theNew)) {
//                        if (allSearchNodes.get(theNew.getId()).getPriority()
//                                > theNew.getPriority()) {
//                            distTo.put(l, distFromMin);
//                            allSearchNodes.put(l, theNew);
//                            fringe.add(theNew);
//                        }
//                    } else {
//                        distTo.put(l, distFromMin);
//                        allSearchNodes.put(l, theNew);
//                        fringe.add(theNew);
//                    }
//                }
//            }
//        }
//        SearchNode goBack = endNode;
//        while (goBack.getPrevious() != 0) {
//            solution.addFirst(goBack.getId());
//            goBack = allSearchNodes.get(goBack.getPrevious());
//        }
//        solution.addFirst(startID);
//        return solution;
//    }
}
