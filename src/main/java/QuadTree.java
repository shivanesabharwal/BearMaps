import java.awt.*;
import java.util.*;

/**
 * Created by shivanesabharwal on 4/15/17.
 */

/* longitude is X, latitude is Y */

public class QuadTree {
    public static class ImageNode implements Comparable<ImageNode> {


        public String name;
        public double ULLON;
        public double ULLAT;
        public double LRLON;
        public double LRLAT;
        public double LONDPP;
        public int level;



        @Override
        public String toString() {
            return name;
        }

        public ImageNode(String fileName, double ul, double ua, double lo, double la, int lvl) {
            name = fileName;
            ULLON = ul;
            ULLAT = ua;
            LRLON = lo;
            LRLAT = la;
            level = lvl;
            LONDPP = (LRLON - ULLON) / 256;
        }

        @Override
        public int compareTo(ImageNode o) {
            if (ULLON < o.ULLON) {
                return -1;
            } else if (ULLON == o.ULLON) {
                return 0;
            } else {
                return 1;
            }
        }

    }

    private static final double ROOT_ULLAT = 37.892195547244356;
    private static final double ROOT_ULLON = -122.2998046875;
    private static final double ROOT_LRLAT = 37.82280243352756;
    private static final double ROOT_LRLON = -122.2119140625;
    private static ImageNode ROOT_NODE = new ImageNode("", ROOT_ULLON, ROOT_ULLAT,
            ROOT_LRLON, ROOT_LRLAT, 0);
    public ImageNode root;
    public QuadTree NE;
    public QuadTree NW;
    public QuadTree SW;
    public QuadTree SE;

    public QuadTree(ImageNode start, int level) {
        if (level == 8) {
            return;
        }
        root = start;
        NW = new QuadTree(new ImageNode(start.name + "1", start.ULLON,
                start.ULLAT, (start.LRLON + start.ULLON) / 2.0, (start.ULLAT + start.LRLAT) / 2.0, level + 1), level + 1);
        NE = new QuadTree(new ImageNode(start.name + "2", (start.LRLON + start.ULLON) / 2.0, start.ULLAT,
                start.LRLON, (start.ULLAT + start.LRLAT) / 2.0, level + 1), level + 1);
        SW = new QuadTree(new ImageNode(start.name + "3", start.ULLON, (start.ULLAT + start.LRLAT) / 2.0,
                (start.ULLON + start. LRLON) / 2.0, start.LRLAT, level + 1), level + 1);
        SE = new QuadTree(new ImageNode(start.name + "4", (start.ULLON + start. LRLON) / 2.0,
                (start.ULLAT + start.LRLAT) / 2.0, start.LRLON, start.LRLAT, level + 1), level + 1);
    }

    public boolean intersectsTile(double qULLON, double qULLAT, double qLRLON, double qLRLAT) {
        return root.ULLON < qLRLON && root.LRLON > qULLON
                && root.ULLAT > qLRLAT && root.LRLAT < qULLAT;
    }
    public boolean lonDPPsmallerThanOrIsLeaf(double queriesLonDPP) {
        return root.LONDPP <= queriesLonDPP || root.level == 7;
    }
    public LinkedList<ImageNode> search(double qULLON, double qULLAT, double qLRLON, double qLRLAT,
                             double queriesLonDPP, LinkedList<ImageNode> imageNodes) {
        /* check NW branch */
        if (NW.intersectsTile(qULLON, qULLAT, qLRLON, qLRLAT)) {
            if (NW.lonDPPsmallerThanOrIsLeaf(queriesLonDPP)) {
                imageNodes.add(NW.root);
            } else {
                NW.search(qULLON, qULLAT, qLRLON, qLRLAT, queriesLonDPP, imageNodes);
            }
        }
        /* check NE branch */
        if (NE.intersectsTile(qULLON, qULLAT, qLRLON, qLRLAT)) {
            if (NE.lonDPPsmallerThanOrIsLeaf(queriesLonDPP)) {
                imageNodes.add(NE.root);
            } else {
                NE.search(qULLON, qULLAT, qLRLON, qLRLAT, queriesLonDPP, imageNodes);
            }
        }
        /* check SW branch */
        if (SW.intersectsTile(qULLON, qULLAT, qLRLON, qLRLAT)) {
            if (SW.lonDPPsmallerThanOrIsLeaf(queriesLonDPP)) {
                imageNodes.add(SW.root);
            } else {
                SW.search(qULLON, qULLAT, qLRLON, qLRLAT, queriesLonDPP, imageNodes);
            }
        }
        /* not in first 3, must be in SE branch */
        if (SE.intersectsTile(qULLON, qULLAT, qLRLON, qLRLAT)) {
            if (SE.lonDPPsmallerThanOrIsLeaf(queriesLonDPP)) {
                imageNodes.add(SE.root);
            } else {
                SE.search(qULLON, qULLAT, qLRLON, qLRLAT, queriesLonDPP, imageNodes);
            }
        }
        return imageNodes;
    }
//public ArrayList<ImageNode> search(double qULLON, double qULLAT, double qLRLON, double qLRLAT,
//                                    double queriesLonDPP, ArrayList<ImageNode> imageNodes) {
//        /* check NW branch */
//    if (NW.intersectsTile(qULLON, qULLAT, qLRLON, qLRLAT)) {
//        if (NW.lonDPPsmallerThanOrIsLeaf(queriesLonDPP)) {
//            imageNodes.add(NW.root);
//        } else {
//            NW.search(qULLON, qULLAT, qLRLON, qLRLAT, queriesLonDPP, imageNodes);
//        }
//    }
//        /* check NE branch */
//    if (NE.intersectsTile(qULLON, qULLAT, qLRLON, qLRLAT)) {
//        if (NE.lonDPPsmallerThanOrIsLeaf(queriesLonDPP)) {
//            imageNodes.add(NE.root);
//        } else {
//            NE.search(qULLON, qULLAT, qLRLON, qLRLAT, queriesLonDPP, imageNodes);
//        }
//    }
//        /* check SW branch */
//    if (SW.intersectsTile(qULLON, qULLAT, qLRLON, qLRLAT)) {
//        if (SW.lonDPPsmallerThanOrIsLeaf(queriesLonDPP)) {
//            imageNodes.add(SW.root);
//        } else {
//            SW.search(qULLON, qULLAT, qLRLON, qLRLAT, queriesLonDPP, imageNodes);
//        }
//    }
//        /* not in first 3, must be in SE branch */
//    if (SE.intersectsTile(qULLON, qULLAT, qLRLON, qLRLAT)) {
//        if (SE.lonDPPsmallerThanOrIsLeaf(queriesLonDPP)) {
//            imageNodes.add(SE.root);
//        } else {
//            SE.search(qULLON, qULLAT, qLRLON, qLRLAT, queriesLonDPP, imageNodes);
//        }
//    }
//    return imageNodes;
//}

//    public static ImageNode[][] sortImageNodes(ArrayList<ImageNode> imageNodes) {
//        HashSet<Double> ullats = new HashSet<>();
//        HashSet<Double> ullons = new HashSet<>();
//        for (ImageNode i : imageNodes) {
//            if (!ullats.contains(i.ULLAT)) {
//                ullats.add(i.ULLAT);
//            }
//            if (!ullons.contains(i.ULLON)) {
//                ullons.add(i.ULLON);
//            }
//        }
//        ImageNode[][] imageNodesArray = new ImageNode[ullats.size()][ullons.size()];
//        System.out.println(imageNodes.get(0).ULLON);
//        System.out.println(imageNodes.get(1).ULLON);
//        System.out.println(imageNodes.get(5).ULLON);
//        System.out.println(imageNodes);
//        Collections.sort(imageNodes);
//        System.out.println(imageNodes);
//        for (int i = 0; i < imageNodesArray.length; i++) {
//            for (int j = 0; j < imageNodesArray[0].length; j++) {
//                imageNodesArray[i][j] = imageNodes.get(i * imageNodesArray[0].length + j);
//
//            }
//        }
//        return imageNodesArray;
//    }

    public static ImageNode[][] sortImageNodes(LinkedList<ImageNode> imageNodes) {
        HashSet<Double> ullats = new HashSet<>();
        HashSet<Double> ullons = new HashSet<>();
        for (ImageNode i : imageNodes) {
            if (!ullats.contains(i.ULLAT)) {
                ullats.add(i.ULLAT);
            }
            if (!ullons.contains(i.ULLON)) {
                ullons.add(i.ULLON);
            }
        }
        ImageNode[][] imageNodesArray = new ImageNode[ullats.size()][ullons.size()];
        LinkedList<LinkedList<ImageNode>> nodeRows = new LinkedList<>();
        for (double u : ullats) {
            LinkedList<ImageNode> temp = new LinkedList<>();
            for (ImageNode i : imageNodes) {
                if (i.ULLAT == u) {
                    temp.add(i);
                }
            }
            nodeRows.add(temp);
        }

        PriorityQueue<Double> pqLATS = new PriorityQueue<>();
        for (LinkedList<ImageNode> a : nodeRows) {
            pqLATS.add(a.getFirst().ULLAT);
        }

        for (int i = pqLATS.size() - 1; i >= 0; i--) {
            double comparison = pqLATS.poll();
            LinkedList<ImageNode> toGet = new LinkedList<>();
            for (LinkedList<ImageNode> a : nodeRows) {
                if (a.getFirst().ULLAT == comparison) {
                    toGet = a;
                }
            }
            for (int j = 0; j < imageNodesArray[0].length; j++) {
                imageNodesArray[i][j] = toGet.get(j);
            }

        }

        return imageNodesArray;
    }

}

