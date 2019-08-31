package conway.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Class providing file IO utility.
 * @author caproven
 */
public class FileIO {

    /**
     * Reads a file and returns a 2D array of parsed cell states.
     * @param fileName Name of the file to be read
     * @param maxX Maximum number of cells that can be read horizontally
     * @param maxY Maximum number of cells that can be read vertically
     * @throws IOException if the file is invalid or cannot be located
     */
	public static int[][] read(String fileName, int maxX, int maxY) throws IOException {
        int[][] arr = new int[maxY][maxX];

        Scanner fileScan = new Scanner(new File(fileName));

        int line = 0, length = -1;

        // Validate file while populating new arr with its contents.
        while (fileScan.hasNextLine()) {
            String currentLine = fileScan.nextLine();
            line++;
            if (line > maxY) {
                fileScan.close();
                throw new IOException("File line count exceeds grid.");
            }

            if (length == -1) {
                length = currentLine.length();
            } else if (length != currentLine.length()) {
                fileScan.close();
                throw new IOException("Inconsistent line length.");
            }
            if (length > maxX) {
                fileScan.close();
                throw new IOException("File width exceeds grid.");
            }

            for (int lineIndex = 0; lineIndex < currentLine.length(); lineIndex++) {
                char ch = currentLine.charAt(lineIndex);
                if (ch == '0' || ch == '1') {
                    arr[line - 1][lineIndex] = Character.getNumericValue(ch);
                } else {
                    fileScan.close();
                    throw new IOException("Invalid character detected.");
                }
            }
        }

        fileScan.close();
        
        return arr;
    }

    /**
     * Writes the given grid of cell states to a file.
     * @param fileName Name of the file where cell state grid will be output
     * @param grid 2D array of cell states
     * @throws IOException if file cannot be written to
     */
    public static void write(String fileName, int[][] grid) throws IOException {
        PrintStream toFile = new PrintStream(new File(fileName));
        for (int y = 0; y < grid.length; y++) { // rows
            for (int x = 0; x < grid[0].length; x++) { // columns
                if (grid[y][x] == 1) {
                    toFile.print("1");
                } else {
                    toFile.print("0");
                }
            }
            // only want to go to a new line as long as line is not the last line
            if (y < grid.length - 1) {
                toFile.print("\n");
            }
        }
        toFile.close();
    }

}