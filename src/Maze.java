import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] broken = new int[w.count()];
        int count = 0;
        for (int k = 0; k < w.count(); k++) {
            if (dsu.union(w.a()[k], w.b()[k])) {
                broken[count] = k;
                count++;
            }
        }
        return Arrays.copyOf(broken, count);
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        Integer[] order = new Integer[w.count()];
        for (int k = 0; k < w.count(); k++) {
            order[k] = k;
        }
        Arrays.sort(order, (x, y) -> Integer.compare(w.cost()[x], w.cost()[y]));

        DisjointSet dsu = new DisjointSet(w.cells());
        int[] broken = new int[w.count()];
        int count = 0;
        for (int k : order) {
            if (dsu.union(w.a()[k], w.b()[k])) {
                broken[count] = k;
                count++;
            }
        }
        return Arrays.copyOf(broken, count);
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int n = w.cells();
        List<List<Integer>> wallsOf = new ArrayList<>();
        for (int v = 0; v < n; v++) {
            wallsOf.add(new ArrayList<>());
        }
        for (int k = 0; k < w.count(); k++) {
            wallsOf.get(w.a()[k]).add(k);
            wallsOf.get(w.b()[k]).add(k);
        }

        boolean[] inTree = new boolean[n];
        PriorityQueue<int[]> pq = new PriorityQueue<>((x, y) -> Integer.compare(x[0], y[0]));
        long total = 0;

        inTree[0] = true;
        for (int k : wallsOf.get(0)) {
            pq.add(new int[]{w.cost()[k], other(w, k, 0)});
        }

        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int cost = top[0];
            int v = top[1];
            if (inTree[v]) {
                continue;
            }
            inTree[v] = true;
            total += cost;
            for (int k : wallsOf.get(v)) {
                int u = other(w, k, v);
                if (!inTree[u]) {
                    pq.add(new int[]{w.cost()[k], u});
                }
            }
        }
        return total;
    }

    private static int other(Input.Walls w, int k, int v) {
        if (w.a()[k] == v) {
            return w.b()[k];
        }
        return w.a()[k];
    }
}
