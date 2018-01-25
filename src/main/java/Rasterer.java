import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    // Recommended: QuadTree instance variable. You'll need to make
    //              your own QuadTree since there is no built-in quadtree in Java.

    private QuadTree quadTree = new QuadTree(new
            QuadTree.ImageNode("", -122.2998046875, 37.892195547244356,
            -122.2119140625, 37.82280243352756, 0), 0);

    public QuadTree getQuadTree() {
        return quadTree;
    }

    /** imgRoot is the name of the directory containing the images.
     *  You may not actually need this for your class. */
    public Rasterer(String imgRoot) {
        // YOUR CODE HERE
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     * </p>
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified:
     * "render_grid"   -> String[][], the files to display
     * "raster_ul_lon" -> Number, the bounding upper left longitude of the rastered image <br>
     * "raster_ul_lat" -> Number, the bounding upper left latitude of the rastered image <br>
     * "raster_lr_lon" -> Number, the bounding lower right longitude of the rastered image <br>
     * "raster_lr_lat" -> Number, the bounding lower right latitude of the rastered image <br>
     * "depth"         -> Number, the 1-indexed quadtree depth of the nodes of the rastered image.
     *                    Can also be interpreted as the length of the numbers in the image
     *                    string. <br>
     * "query_success" -> Boolean, whether the query was able to successfully complete. Don't
     *                    forget to set this to true! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        System.out.println(params);
        Map<String, Object> results = new HashMap<>();
        double rootLONDPP = (params.get("lrlon") - params.get("ullon")) / params.get("w");
        LinkedList<QuadTree.ImageNode> images = getQuadTree()
                .search(params.get("ullon"), params.get("ullat"),
                params.get("lrlon"), params.get("lrlat"),
                        rootLONDPP, new LinkedList<>());

        QuadTree.ImageNode[][] imageNodes = QuadTree.sortImageNodes(images);
        String[][] imageStrings = new String[imageNodes.length][imageNodes[0].length];
        for (int i = 0; i < imageNodes.length; i++) {
            for (int j = 0; j < imageNodes[0].length; j++) {
                imageStrings[i][j] =  "img/" + imageNodes[i][j].name + ".png";
            }
        }
        results.put("render_grid", imageStrings);
        results.put("raster_ul_lon", imageNodes[0][0].ULLON);
        results.put("raster_ul_lat", imageNodes[0][0].ULLAT);
        results.put("raster_lr_lon",
                imageNodes[imageNodes.length - 1][imageNodes[0].length - 1].LRLON);
        results.put("raster_lr_lat",
                imageNodes[imageNodes.length - 1][imageNodes[0].length - 1].LRLAT);
        results.put("depth", imageNodes[0][0].level);
        results.put("query_success", true);
        return results;
    }


}
