/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orderbook.dto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Pattern;

/**
 *
 * @author User
 */
public class TableList {
    private static final String[] BLINE = { "-", "\u2501" };
    private static final String[] CROSSING = { "-+-", "\u2548" };
    private static final String[] VERTICAL_TSEP = { "|", "\u2502" };
    private static final String[] VERTICAL_BSEP = { "|", "\u2503" };


    private String[] descriptions;
    private ArrayList<String[]> table;
    private int[] tableSizes;
    private int rows;
    private boolean ucode;
    private int spacing;
    private EnumAlignment aligns[];

    public TableList(String... descriptions) {
        
        this(descriptions.length, descriptions);
    }

    public TableList(int columns, String... descriptions) {
        if (descriptions.length != columns) {
                throw new IllegalArgumentException();
        }
        this.rows = columns;
        this.descriptions = descriptions;
        this.table = new ArrayList<>();
        this.tableSizes = new int[columns];
        this.updateSizes(descriptions);
        this.spacing = 1;
        this.aligns = new EnumAlignment[columns];
        for (int i = 0; i < aligns.length; i++) {
                aligns[i] = EnumAlignment.LEFT;
        }
}

    private void updateSizes(String[] elements) { 
        for (int i = 0; i < tableSizes.length; i++) {
                if (elements[i] != null) {
                        int j = tableSizes[i];
                        j = Math.max(j, elements[i].length());
                        tableSizes[i] = j;
                }
        }
}


    public TableList withSpacing(int spacing) {
        this.spacing = spacing;
        return this;
}

    /**
     * Adds a row to the table with the specified elements.
     */

    public TableList addRow(String... elements) {
        if (elements.length != rows) {
                throw new IllegalArgumentException();
        }
        table.add(elements);
        updateSizes(elements);
        return this;
}

    public void print() {
        StringBuilder line = null;
        // print header
        for (int i = 0; i < rows; i++) {
                if (line != null) {
                        line.append(gc(VERTICAL_TSEP));
                } else {
                        line = new StringBuilder();
                }
                String part = descriptions[i];
                while (part.length() < tableSizes[i] + spacing) {
                        part += " ";
                }
                for (int j = 0; j < spacing; j++) {
                        line.append(" ");
                }
                line.append(part);
        }
        System.out.println(line.toString());

        // print vertical seperator
        line = null;
        for (int i = 0; i < rows; i++) {
                if (line != null) {
                        line.append(gc(CROSSING));
                } else {
                        line = new StringBuilder();
                }
                for (int j = 0; j < tableSizes[i] + 2 * spacing; j++) {
                        line.append(gc(BLINE));
                }
        }

        System.out.println(line.toString());

        line = null;
        ArrayList<String[]> localTable = table;

        if (localTable.isEmpty()) {
                String[] sa = new String[rows];
                localTable.add(sa);
        }

        localTable.forEach(arr -> {
                for (int i = 0; i < arr.length; i++) {
                        if (arr[i] == null) {
                                arr[i] = "";
                        }
                }
        });

        for (String[] strings : localTable) {
                for (int i = 0; i < rows; i++) {
                        if (line != null) {
                                line.append(gc(VERTICAL_BSEP));
                        } else {
                                line = new StringBuilder();
                        }
                        String part = "";
                        for (int j = 0; j < spacing; j++) {
                                part += " ";
                        }
                        if (strings[i] != null) {
                                switch (aligns[i]) {
                                case LEFT:
                                        part += strings[i];
                                        break;
                                case RIGHT:
                                        for (int j = 0; j < tableSizes[i] - strings[i].length(); j++) {
                                                part += " ";
                                        }
                                        part += strings[i];
                                        break;
                                case CENTER:
                                        for (int j = 0; j < (tableSizes[i] - strings[i].length()) / 2; j++) {
                                                part += " ";
                                        }
                                        part += strings[i];
                                        break;
                                }
                        }
                        while (part.length() < tableSizes[i] + spacing) {
                                part += " ";
                        }
                        for (int j = 0; j < spacing; j++) {
                                part += " ";
                        }
                        line.append(part);
                }
                System.out.println(line.toString());

                line = null;
        }
    }

    private String gc(String[] src) {
            return src[ucode ? 1 : 0];

    }

    public static enum EnumAlignment {
            LEFT, CENTER, RIGHT
    }

}
