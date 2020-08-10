import java.util.*;

public class Sudoku {

    private static boolean isValid(final String board) {
        HashSet[] rows = new HashSet[9];
        HashSet[] cols = new HashSet[9];

        int [][] first = new int[][] {
            {0, 0}, {0, 3}, {0, 6}, 
            {3, 0}, {3, 3}, {3, 6}, 
            {6, 0}, {6, 3}, {6, 6}
        };

        for (int i = 0; i < first.length; i++) {
            if (i != 0 && i % 3 == 0) {
                for (int j = i - 3; j < i; j++) {
                    if (rows[j].size() != 9) {
                        return false;
                    }
                }
            }

            HashSet nums = new HashSet<>();
            for (int x = first[i][1]; x < first[i][1] + 3; x++) {
                for (int y = first[i][0]; y < first[i][0] + 3; y++) {
                    char c = board.charAt(y*9+x);
                    nums.add(c);

                    if (rows[y] == null) {
                        rows[y] = new HashSet();
                    }

                    if (cols[x] == null) {
                        cols[x] = new HashSet();
                    }

                    rows[y].add(c);
                    cols[x].add(c);
                }
            }

            if (nums.size() != 9) {
                return false;
            }
        }

        for (int i = 0; i < 9; i++) {
            if (cols[i].size() != 9) {
                return false;
            }
        }

        for (int i = 6; i < 9; i++) {
            if (rows[i].size() != 9) {
                return false;
            }
        }

        return true;
    }

    public static String prettyString(final String board) {
        String hor = "-".repeat(25);
        String vert = "|";

        StringBuilder sb = new StringBuilder(1000);
        for (int i = 0; i < 9; i++) {
            if (i % 3 == 0) {
                sb.append(hor);
                sb.append("\n");
            }
            for (int j = 0; j < 9; j++) {
                if (j % 3 == 0) {
                    sb.append("| ");
                }

                sb.append(board.charAt(i*9+j));
                sb.append(" ");
            }

            sb.append("|\n");
        }

        sb.append(hor);

        return sb.toString();
    }

    private static void test(String inp, boolean expected) {
        boolean result = isValid(inp);
        String msg = result == expected ? "PASS" : "FAIL";
        System.out.println(String.format("[%s] expected: %6s, inp = %s", msg, expected, inp));
    }

    public static void tests() {
        test(".".repeat(81), false);
        test("483921657967345821251876493548132976729564138136798245372689514814253769695417382", true);
        test("483921657967345821251876493548132976729564138136798245372689514814253769695417381", false);
        test("123456789456789123789123456214365897365897214897214365531642978648971532972538641", true);
    }

    private static final char [] elements = new char [] {'1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static String solve(String sudoku) {
        final HashSet[] rows = new HashSet[9];
        final HashSet[] cols = new HashSet[9];
        final HashMap<Integer, HashSet> squares = new HashMap<>(81*3);
        final StringBuilder sb = new StringBuilder(81*3);

        rows[0] = new HashSet(9*3);
        for (int i = 0; i < 9; i++) {
            cols[i] = new HashSet();
            HashSet square = null;
            if (i % 3 == 0) {
                square = new HashSet();
            } else {
                square = squares.get((i/3)*3);
            }

            if (sudoku.charAt(i) != '.') {
                square.add(sudoku.charAt(i));
                rows[0].add(sudoku.charAt(i));
                cols[i].add(sudoku.charAt(i));
            }
            
            squares.put(i, square);
            sb.append(sudoku.charAt(i));
        }

        for (int i = 9; i < 81; i++) {
            final int row = i/9;
            final int col = i%9;

            if (col == 0) {
                rows[row] = new HashSet(9*3);
            }

            HashSet square = null;
            if (col % 3 == 0 && row % 3 == 0) {
                square = new HashSet();
            } else {
                square = squares.get((row/3)*3*9 + (col/3)*3);
            }
            
            if (sudoku.charAt(i) != '.') {
                rows[row].add(sudoku.charAt(i));
                cols[col].add(sudoku.charAt(i));
                square.add(sudoku.charAt(i));
            }

            squares.put(i, square);
            sb.append(sudoku.charAt(i));
        }

        iterations = 0;
        return solve(sb, 0, rows, cols, squares);
    }

    private static int iterations = 0;
    private static String solve(StringBuilder sb, int i, HashSet[] rows, HashSet[] cols, HashMap<Integer, HashSet> squares) {
        iterations++;
        if (i == 81) {
            return sb.toString();
        }

        if (sb.charAt(i) != '.') {
            return solve(sb, i+1, rows, cols, squares);
        }

        for (int k = 0; k < elements.length; k++) {
            final int row = i/9;
            final int col = i%9;

            if (rows[row].contains(elements[k]) || cols[col].contains(elements[k])) {
                continue;
            }

            final HashSet square = squares.get(i);
            
            if (square.contains(elements[k])) {
                continue;
            }

            rows[row].add(elements[k]);
            cols[col].add(elements[k]);
            square.add(elements[k]);
            sb.setCharAt(i, elements[k]);

            final String res = solve(sb, i+1, rows, cols, squares);

            if (res != null) {
                return res;
            }

            rows[row].remove(elements[k]);
            cols[col].remove(elements[k]);
            square.remove(elements[k]);
            sb.setCharAt(i, '.');
        }

        return null;
    }

    public static void main(String [] args) {
        
        final String [] cases = new String [] {
            "..3.2.6..9..3.5..1..18.64....81.29..7..........67.82....26.95..8..2.3..9..5.1.3..",
            ".".repeat(80) + "1",
        };
        System.out.println();
        System.out.println("=".repeat(37) + " STARTING CASES " + "=".repeat(37));
        System.out.println();
        
        long totalTime = 0;
        for (final String acase : cases) {
            System.out.println("=".repeat(81));
            System.out.println();
            System.out.println("Case");
            System.out.println(acase);
            System.out.println(prettyString(acase));
            
            final long start = System.nanoTime();
            final String solu = solve(acase);
            final long end = System.nanoTime();
            
            System.out.println();
            System.out.println("Solu");
            System.out.println(solu);
            System.out.println(prettyString(solu));
            System.out.println();
            System.out.println(String.format("time_elapsed: %.2fms, iterations: %d", (end-start)*1e-6, iterations));
            System.out.println();
            
            totalTime += (end - start);
        }
        
        // 90 - 6 = 84 / 2 = 42
        // 90 - 13 = 74 / 2
        System.out.println("=".repeat(42) + " DONE " + "=".repeat(42));
        System.out.println();
        System.out.println(String.format("Avg. time: %.2fms", (totalTime/(double)cases.length)*1e-6));
        System.out.println();

        System.out.println("=".repeat(36) + " IS_VALID TESTING " + "=".repeat(36));
        System.out.println();
        tests();
        System.out.println();
    }
}