package com.brandontoner.picross;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Picross extends ColorPicross {
    public Picross(int width, int height, List<List<Integer>> columns, List<List<Integer>> rows) {
        super(width, height, toSpans(columns), toSpans(rows));
    }

    private static List<List<Span>> toSpans(List<List<Integer>> lists) {
        return lists.stream().map(column -> column.stream().map(i -> new Span('x', i)).toList()).toList();
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
                columns.add(readline(br));
            }

            List<List<Integer>> rows = new ArrayList<>();
            for (int i = 0; i < height; ++i) {
                System.err.print("Row " + i + ": ");
                rows.add(readline(br));
            }
            System.err.println();
            new Picross(width, height, columns, rows).solve();
        }
    }

    private static List<Integer> readline(BufferedReader br) throws IOException {
        return Arrays.stream(br.readLine().trim().split(" "))
                     .map(String::trim)
                     .filter(v -> !v.isEmpty())
                     .map(Integer::parseInt)
                     .collect(Collectors.toList());
    }
}
