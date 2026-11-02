/**
 * Checks of Mission 0. Run:  java -cp out DisjointSetTest
 * Add your own checks below: a large chain, a union of a set with itself, count after many unions.
 */
public class DisjointSetTest {

    static int failed = 0;

    static void check(boolean ok, String what) {
        System.out.println((ok ? "ok    " : "FAIL  ") + what);
        if (!ok) failed++;
    }

    public static void main(String[] args) {
        DisjointSet d = new DisjointSet(6);
        check(d.count() == 6, "6 sets at the start");
        check(!d.connected(0, 1), "0 and 1 are apart at the start");
        check(d.union(0, 1), "union(0, 1) joins two sets");
        check(d.connected(0, 1), "0 and 1 are together");
        check(!d.union(1, 0), "union(1, 0) again returns false");
        check(d.count() == 5, "5 sets after one union");
        d.union(2, 3); d.union(1, 3);
        check(d.connected(0, 2), "0 and 2 are together through 1 and 3");
        check(d.count() == 3, "3 sets: {0,1,2,3} {4} {5}");
        check(d.find(4) == 4 && d.find(5) == 5, "a single cell is its own root");

        check(!d.union(4, 4), "union(4, 4) of a set with itself returns false");
        check(d.count() == 3, "still 3 sets after union(4, 4)");
        check(d.union(4, 5) && d.union(5, 0), "4, 5 and the big set are joined");
        check(d.count() == 1, "1 set at the end");
        check(d.connected(5, 2), "5 and 2 are together");

        int n = 1_000_000;
        DisjointSet chain = new DisjointSet(n);
        for (int i = 0; i + 1 < n; i++) chain.union(i, i + 1);
        check(chain.count() == 1, "a chain of 1 000 000 cells is one set");
        check(chain.connected(0, n - 1), "the two ends of the chain are connected");

        DisjointSet many = new DisjointSet(1000);
        for (int i = 0; i < 1000; i += 2) many.union(i, i + 1);
        check(many.count() == 500, "500 sets after 500 unions of pairs");
        for (int i = 0; i < 1000; i += 2) many.union(i, i + 1);
        check(many.count() == 500, "the same unions again do not change count");
        for (int i = 1; i + 1 < 1000; i += 2) many.union(i, i + 1);
        check(many.count() == 1, "1 set after joining all the pairs");

        System.out.println(failed == 0 ? "All checks passed." : failed + " checks failed.");
    }
}
