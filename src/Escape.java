import java.util.Arrays;

/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells();
        int cols = w.cols();
        int exit = n - 1;
        int[][] open = Graphs.open(w, broken);

        int deadEnds = 0;
        for (int v = 0; v < n; v++) {
            if (open[v].length == 1) {
                deadEnds++;
            }
        }

        boolean[] visited = new boolean[n];
        int[] dir = new int[n];
        int[] stack = new int[n];
        int top = 0;
        stack[0] = 0;
        visited[0] = true;
        int marked = 1;

        while (stack[top] != exit) {
            int v = stack[top];
            if (dir[v] == 4) {
                top--;
                continue;
            }
            int u = neighbour(v, dir[v], cols, n);
            dir[v]++;
            if (u != -1 && !visited[u] && isOpen(open[v], u)) {
                visited[u] = true;
                marked++;
                top++;
                stack[top] = u;
            }
        }

        int[] path = Arrays.copyOf(stack, top + 1);
        return new Result(path, marked, deadEnds);
    }

    private static int neighbour(int v, int d, int cols, int n) {
        if (d == 0) {
            return v - cols >= 0 ? v - cols : -1;
        }
        if (d == 1) {
            return v % cols != cols - 1 ? v + 1 : -1;
        }
        if (d == 2) {
            return v + cols < n ? v + cols : -1;
        }
        return v % cols != 0 ? v - 1 : -1;
    }

    private static boolean isOpen(int[] list, int u) {
        for (int x : list) {
            if (x == u) {
                return true;
            }
        }
        return false;
    }
}
