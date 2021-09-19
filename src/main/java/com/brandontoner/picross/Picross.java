package com.brandontoner.picross;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Picross {
    private final int width;
    private final int height;
    private final List<List<Integer>> columns;
    private final List<List<Integer>> rows;

    public Picross(int width, int height, List<List<Integer>> columns, List<List<Integer>> rows) {
        this.width = width;
        this.height = height;
        this.columns = columns;
        this.rows = rows;
    }

    public static void main(String[] args) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("Width: ");
            int width = Integer.parseInt(br.readLine());
            System.out.print("Height: ");
            int height = Integer.parseInt(br.readLine());

            List<List<Integer>> columns = new ArrayList<>();
            for (int i = 0; i < width; ++i) {
                System.err.print("Column " + i + ": ");
                columns.add(Arrays.stream(br.readLine().trim().split(" "))
                                  .map(String::trim)
                                  .filter(v -> !v.isEmpty())
                                  .map(Integer::parseInt)
                                  .collect(Collectors.toList()));
            }

            List<List<Integer>> rows = new ArrayList<>();
            for (int i = 0; i < height; ++i) {
                System.err.print("Row " + i + ": ");
                rows.add(Arrays.stream(br.readLine().trim().split(" "))
                               .map(String::trim)
                               .filter(v -> !v.isEmpty())
                               .map(Integer::parseInt)
                               .collect(Collectors.toList()));
            }
            System.err.println();
            boolean[][] output = new Picross(width, height, columns, rows).solve();
        }
    }

    private boolean[][] solve() {
        List<List<boolean[]>> rowPossibilities = new ArrayList<>();
        List<List<boolean[]>> columnPossibilities = new ArrayList<>();
        for (int i = 0; i < height; ++i) {
            rowPossibilities.add(getPossibilities(rows.get(i), width));
        }
        for (int i = 0; i < width; ++i) {
            columnPossibilities.add(getPossibilities(columns.get(i), height));
        }

        Boolean[][] output = new Boolean[width][height];
        Getter rowGetter = (i, j) -> output[i][j];
        Setter rowSetter = (i1, j1, b) -> output[i1][j1] = b;

        Getter columnGetter = (i, j) -> output[j][i];
        Setter columnSetter = (i, j, b) -> output[j][i] = b;
        while (true) {
            doit(rowPossibilities, height, width, rowGetter, rowSetter);
            print(output);

            doit(columnPossibilities, width, height, columnGetter, columnSetter);
            print(output);
            boolean done = true;
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    if (output[y][x] == null) {
                        done = false;
                        break;
                    }
                }
            }
            if (done) {
                System.err.println("DONE!");
                print(output);
                System.exit(0);
            }
        }
    }

    interface Getter {
        Boolean get(int i, int j);
    }

    interface Setter {
        void set(int i, int j, Boolean b);
    }

    private void doit(List<List<boolean[]>> possibilities, int dim1, int dim2, Getter getter, Setter setter) {
        for (int i = 0; i < dim1; ++i) {
            List<boolean[]> list = possibilities.get(i);
            for (Iterator<boolean[]> iterator = list.iterator(); iterator.hasNext(); ) {
                boolean[] booleans = iterator.next();
                for (int j = 0; j < dim2; ++j) {
                    Boolean aBoolean = getter.get(i, j);
                    if (aBoolean != null && aBoolean != booleans[j]) {
                        iterator.remove();
                        break;
                    }
                }
            }
            for (int j = 0; j < dim2; ++j) {
                Set<Boolean> set = new HashSet<>();
                for (boolean[] booleans : list) {
                    set.add(booleans[j]);
                }
                if (set.size() == 1) {
                    setter.set(i, j, set.iterator().next());
                }
            }
        }
    }

    private void print(Boolean[][] output) {
        System.err.println("=========================================================================================");
        for (int y = 0; y < height; ++y) {
            System.err.print("|");
            System.err.println(Stream.of(output[y]).map(b -> {
                if (b == null) {
                    return " ";
                } else if (b) {
                    return "x";
                } else {
                    return "_";
                }
            }).collect(Collectors.joining("  ")) + "|");
        }
    }

    private static List<boolean[]> getPossibilities(List<Integer> integers, int size) {
        return getPossibilities(integers, 0, new boolean[size], 0);

    }

    private static List<boolean[]> getPossibilities(List<Integer> integers,
                                                    int intIndex,
                                                    boolean[] booleans,
                                                    int booleansOffset) {
        if (intIndex == integers.size()) {
            return Collections.singletonList(booleans.clone());
        }
        List<boolean[]> output = new ArrayList<>();
        int v = integers.get(intIndex);
        for (int i = booleansOffset; i < booleans.length; ++i) {
            if (i + v > booleans.length) {
                break;
            }
            boolean[] copy = booleans.clone();
            for (int j = 0; j < v; ++j) {
                copy[i + j] = true;
            }
            output.addAll(getPossibilities(integers, intIndex + 1, copy, i + v + 1));
        }
        return output;
    }
}
