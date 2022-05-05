package utils;

import java.util.ArrayList;

public class LeeAlgorithm {

    public static void printMap(boolean[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                System.out.print(map[i][j] ? "#" : ".");
            }
            System.out.println();
        }
    }

    public static void printMap(int[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                System.out.printf("%3s", map[i][j] == -1 ? "##" : String.valueOf(map[i][j]));
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        final String[] strMap = {
                "##############",
                "#1#####00000##",
                "#00000#000#0##",
                "##00#000##000#",
                "######0####0##",
                "##20000#######",
                "##############",
        };

        boolean[][] map = new boolean[strMap.length][strMap[0].length()];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = strMap[i].charAt(j) == '#';
            }
        }

        printMap(map);
        var path = findPath(map, new Vector2D(1, 1), new Vector2D(5, 2));
        for (Vector2D ij : path) {
            System.out.print(ij.x + " " + ij.y + " -> ");
        }
        System.out.println("END");
    }

    public static ArrayList<Vector2D> neighbours(boolean[][] map, Vector2D vecCell) {
        int[] cell = new int[2];
        cell[0] = (int) vecCell.x;
        cell[1] = (int) vecCell.y;
        ArrayList<Vector2D> res = new ArrayList<>();
        if (cell[0] > 0 && !(map[cell[0] - 1][cell[1]]))
            res.add(new Vector2D(cell[0] - 1, cell[1]));
        if (cell[0] < map.length - 1 && !(map[cell[0] + 1][cell[1]]))
            res.add(new Vector2D(cell[0] + 1, cell[1]));
        if (cell[1] > 0 && !(map[cell[0]][cell[1] - 1]))
            res.add(new Vector2D(cell[0], cell[1] - 1));
        if (cell[1] < map[0].length - 1 && !(map[cell[0]][cell[1] + 1]))
            res.add(new Vector2D(cell[0], cell[1] + 1));
        return res;
    }

    public static ArrayList<Vector2D> findPath(boolean[][] map, Vector2D fromVec, Vector2D toVec) {
        int[] from = new int[2];
        int[] to = new int[2];
        from[0] = (int) fromVec.x;
        from[1] = (int) fromVec.y;
        to[0] = (int) toVec.x;
        to[1] = (int) toVec.y;
        int[][] fill = new int[map.length][map[0].length];
        int wave = 1;
        fill[from[0]][from[1]] = wave;
        ArrayList<Vector2D> prevWaveCells = new ArrayList<>();
        prevWaveCells.add(fromVec);
        boolean canFind = true;
        while (fill[to[0]][to[1]] == 0 && canFind) {
            ArrayList<Vector2D> curFilledCells = new ArrayList<>();
            for (Vector2D prevCell : prevWaveCells) {
                for (Vector2D n : neighbours(map, prevCell)) {
                    if (fill[(int) n.x][(int) n.y] == 0) {
                        fill[(int) n.x][(int) n.y] = wave + 1;
                        curFilledCells.add(n);
                    }
                }
            }
            prevWaveCells = curFilledCells;
            canFind = prevWaveCells.size() > 0;
            wave++;
            printMap(fill);
            System.out.println();
        }
        if (canFind) {
            ArrayList<Vector2D> res = new ArrayList<>();
            int cur = wave;
            System.out.println(cur);
            Vector2D curCell = toVec;
            res.add(curCell);
            while (cur > 1) {
                Vector2D found = null;
                for (Vector2D n : neighbours(map, curCell)) {
                    if (fill[(int) n.x][(int) n.y] == cur - 1) found = n;
                }
                cur--;
                curCell = found;
                res.add(0, curCell);
            }
            return res;
        }
        return null;
    }
}