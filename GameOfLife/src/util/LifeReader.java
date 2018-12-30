package util;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Handles reading of Game of Life states from a file.
 * 
 * @author caproven
 */
public class LifeReader {
    /**
     * Reads cell states from a given file, first ensuring that file contents are the correct
     * format.
     * 
     * @param fileName Name of the file to be read
     * @param x X dimension (number of cells) the simulation should contain
     * @param y Y dimension (number of cells) the simulation should contain
     * @return 2D boolean array representing states read from the input file
     * @throws IOException if file cannot be located or is formatted incorrectly
     */
    public static boolean[][] readStateFromFile(String fileName, int x, int y) throws IOException {
        Scanner fileScan = new Scanner(new File(fileName));
        // check that file contents are correct size
        int lines = 0;
        while (fileScan.hasNextLine()) {
            String currentLine = fileScan.nextLine();
            if (currentLine.length() != x) {
                fileScan.close();
                throw new IOException();
            }
            for (int lineIndex = 0; lineIndex < currentLine.length(); lineIndex++) {
                char c = currentLine.charAt(lineIndex);
                if (c != '0' && c != '1') {
                    fileScan.close();
                    throw new IOException();
                }
            }
            lines++;
        }
        if (lines != y) {
            fileScan.close();
            throw new IOException();
        }
        // reads game state
        fileScan = new Scanner(new File(fileName));
        boolean[][] state = new boolean[y][x];
        for (int row = 0; row < y; row++) {
            String currentLine = fileScan.nextLine();
            for (int col = 0; col < x; col++) {
                if (currentLine.charAt(col) == '1')
                    state[row][col] = true;
                // booleans default to false so no need to otherwise update
            }
        }
        fileScan.close();
        return state;
    }
}
