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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ColorPicross {
    private final int width;
    private final int height;
    private final List<List<Span>> columns;
    private final List<List<Span>> rows;

    public ColorPicross(int width, int height, List<List<Span>> columns, List<List<Span>> rows) {
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

            List<List<Span>> columns = new ArrayList<>();
            for (int i = 0; i < width; ++i) {
                System.err.print("Column " + i + ": ");
                columns.add(readline(br));
            }

            List<List<Span>> rows = new ArrayList<>();
            for (int i = 0; i < height; ++i) {
                System.err.print("Row " + i + ": ");
                rows.add(readline(br));
            }
            System.err.println();
            new ColorPicross(width, height, columns, rows).solve();
        }
    }

    private static List<Span> readline(BufferedReader br) throws IOException {
        return Arrays.stream(br.readLine().trim().split(" ")).map(String::trim).filter(v -> !v.isEmpty()).map(v -> {
            char c = v.charAt(0);
            int n = Integer.parseInt(v.substring(1));
            return new Span(c, n);
        }).collect(Collectors.toList());
    }

    void solve() {
        List<List<char[]>> rowPossibilities = new ArrayList<>();
        List<List<char[]>> columnPossibilities = new ArrayList<>();
        for (int i = 0; i < height; ++i) {
            rowPossibilities.add(getPossibilities(rows.get(i), width));
        }
        for (int i = 0; i < width; ++i) {
            columnPossibilities.add(getPossibilities(columns.get(i), height));
        }

        Character[][] output = new Character[width][height];
        Getter rowGetter = (i, j) -> output[i][j];
        Setter rowSetter = (i1, j1, b) -> output[i1][j1] = b;

        Getter columnGetter = (i, j) -> output[j][i];
        Setter columnSetter = (i, j, b) -> output[j][i] = b;
        while (true) {
            boolean rowChanged = doit(rowPossibilities, height, width, rowGetter, rowSetter);
            print(output);

            boolean columnChanged = doit(columnPossibilities, width, height, columnGetter, columnSetter);
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
            } else if (!(rowChanged || columnChanged)) {
                System.err.println("STUCK!");
                print(output);
                System.exit(0);
            }
        }
    }

    interface Getter {
        Character get(int i, int j);
    }

    interface Setter {
        void set(int i, int j, Character b);
    }

    private boolean doit(List<List<char[]>> possibilities, int dim1, int dim2, Getter getter, Setter setter) {
        boolean changed = false;
        for (int i = 0; i < dim1; ++i) {
            List<char[]> list = possibilities.get(i);
            for (Iterator<char[]> iterator = list.iterator(); iterator.hasNext(); ) {
                char[] chars = iterator.next();
                for (int j = 0; j < dim2; ++j) {
                    Character character = getter.get(i, j);
                    if (character != null && character != chars[j]) {
                        iterator.remove();
                        changed = true;
                        break;
                    }
                }
            }
            for (int j = 0; j < dim2; ++j) {
                Set<Character> set = new HashSet<>();
                for (char[] chars : list) {
                    set.add(chars[j]);
                }
                if (set.size() == 1) {
                    Character existing = getter.get(i, j);
                    Character newC = set.iterator().next();
                    if (!Objects.equals(existing, newC)) {
                        setter.set(i, j, newC);
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }

    private void print(Character[][] output) {
        System.err.println("=========================================================================================");
        for (int y = 0; y < height; ++y) {
            System.err.print("|");
            System.err.println(Stream.of(output[y]).map(b -> {
                if (b == null) {
                    return " ";
                } else if (b == 0) {
                    return "_";
                } else {
                    return "" + b;
                }
            }).collect(Collectors.joining("  ")) + "|");
        }
    }

    private static List<char[]> getPossibilities(List<Span> integers, int size) {
        return getPossibilities(integers, 0, new char[size], 0);

    }

    private static List<char[]> getPossibilities(List<Span> spans,
                                                 int spanIndex,
                                                 char[] chars,
                                                 int charsOffset) {
        if (spanIndex == spans.size()) {
            return Collections.singletonList(chars.clone());
        }
        List<char[]> output = new ArrayList<>();
        Span span = spans.get(spanIndex);
        int n = span.n();
        char c = span.c();
        for (int i = charsOffset; i < chars.length; ++i) {
            if (i + n > chars.length) {
                break;
            }
            char[] copy = chars.clone();
            for (int j = 0; j < n; ++j) {
                copy[i + j] = c;
            }
            int nextOffset;
            if (spanIndex + 1 == spans.size() || spans.get(spanIndex + 1).c() == c) {
                nextOffset = i + n + 1; // same colors have to be separated by 1
            } else {
                nextOffset = i + n; // different colors can be next to each other
            }
            output.addAll(getPossibilities(spans, spanIndex + 1, copy, nextOffset));
        }
        return output;
    }

    record Span(char c, int n) {

    }
}
