public class Test {
    public static void main(String[] args) {
        char[][] map = {
                {'0', '0', '0', '0', '1'},
                {'1', '1', '1', '1', '0'},
                {'1', '0', '0', '0', '0'},
                {'1', '1', '1', '1', '0'},
                {'1', '1', '1', '1', '0'},
        };
        int[][] mapPaths = new int[map.length][map[0].length];
        for (int i = 0; i < mapPaths.length; i++) {
            for (int j = 0; j < mapPaths[0].length; j++) {
                if (map[i][j] == '0')
                    mapPaths[j][i] = 0;
                else
                    mapPaths[j][i] = 1;
            }
        }

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == '0') {

                }
            }
        }
    }
}
