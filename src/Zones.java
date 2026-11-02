import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Missions 4 and 5. The slides: a directed graph. Cells are 0 .. n-1, slide i goes from s.from()[i] to s.to()[i]
 * and takes s.time()[i]. There are no slides from a cell to itself, and the exit can always be reached from the start.
 *
 * Ready for you: Graphs.slidesFrom(s)[u] is the list of the slides that leave cell u, and Graphs.slidesInto(s)[v] is the list
 * of the slides that enter cell v (reversed graph). The lists hold slide numbers i; the cell at the other end is s.to()[i]
 * or s.from()[i].
 */
public class Zones {

    /**
     * Mission 4. Kosaraju. Returns zoneOf: zoneOf[cell] = number of the zone, numbers 0 .. zones-1.
     * Number the zones in the order in which the second pass finds them. That order is a topological order
     * of the zone graph: every slide between two zones goes from a smaller number to a larger number.
     *
     * Plan: 1) DFS on the slides, write the cells in the order in which they are finished (use a stack, not recursion);
     * 2) go through the cells from the last finished to the first; if a cell has no zone yet, DFS from it on the reversed
     * slides (slidesInto) and give every cell it reaches the next zone number.
     * Is DAG in Main is 1 when the number of zones equals the number of cells (no cycle), so you do not need a separate check.
     */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] out = Graphs.slidesFrom(s);
        int[][] in = Graphs.slidesInto(s);

        boolean[] visited = new boolean[n];
        int[] next = new int[n];
        int[] stack = new int[n];
        int[] order = new int[n];
        int finished = 0;

        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }
            int top = 0;
            stack[0] = start;
            visited[start] = true;
            while (top >= 0) {
                int v = stack[top];
                if (next[v] < out[v].length) {
                    int u = s.to()[out[v][next[v]]];
                    next[v]++;
                    if (!visited[u]) {
                        visited[u] = true;
                        top++;
                        stack[top] = u;
                    }
                } else {
                    order[finished] = v;
                    finished++;
                    top--;
                }
            }
        }

        int[] zoneOf = new int[n];
        Arrays.fill(zoneOf, -1);
        int zones = 0;
        for (int i = n - 1; i >= 0; i--) {
            int root = order[i];
            if (zoneOf[root] != -1) {
                continue;
            }
            int top = 0;
            stack[0] = root;
            zoneOf[root] = zones;
            while (top >= 0) {
                int v = stack[top];
                top--;
                for (int slide : in[v]) {
                    int u = s.from()[slide];
                    if (zoneOf[u] == -1) {
                        zoneOf[u] = zones;
                        top++;
                        stack[top] = u;
                    }
                }
            }
            zones++;
        }
        return zoneOf;
    }

    /**
     * Mission 5. The graph of zones: the edge X to Y costs the cheapest slide from X to Y, walking inside a zone is free.
     * Go through the zones in the order of their numbers (no priority queue). Returns two numbers:
     * [0] flow stages: the number of zones on the longest chain of zones;
     * [1] the cost of the cheapest route from the zone of cell 0 to the zone of the last cell.
     */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        List<List<int[]>> edges = new ArrayList<>();
        for (int z = 0; z < zoneCount; z++) {
            edges.add(new ArrayList<>());
        }
        for (int i = 0; i < s.count(); i++) {
            int x = zoneOf[s.from()[i]];
            int y = zoneOf[s.to()[i]];
            if (x != y) {
                edges.get(x).add(new int[]{y, s.time()[i]});
            }
        }

        long[] stages = new long[zoneCount];
        long[] dist = new long[zoneCount];
        Arrays.fill(stages, 1);
        Arrays.fill(dist, Long.MAX_VALUE);
        dist[zoneOf[0]] = 0;

        for (int x = 0; x < zoneCount; x++) {
            for (int[] e : edges.get(x)) {
                int y = e[0];
                int time = e[1];
                stages[y] = Math.max(stages[y], stages[x] + 1);
                if (dist[x] != Long.MAX_VALUE) {
                    dist[y] = Math.min(dist[y], dist[x] + time);
                }
            }
        }

        long flowStages = 0;
        for (long st : stages) {
            flowStages = Math.max(flowStages, st);
        }
        return new long[]{flowStages, dist[zoneOf[s.n() - 1]]};
    }
}
