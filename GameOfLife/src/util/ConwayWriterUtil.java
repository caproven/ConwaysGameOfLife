package util;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Handles writing of Game of Life state to a file.
 * @author caproven
 */
public class ConwayWriterUtil {
    /**
     * Writes cell states to a given file, printing '0' if a cell is "dead" or '1' if a cell
     * is "alive".
     * @param fileName Name of the file to write to
     * @param state Cell states array that should be written to the file
     * @throws IOException if there is an error writing to the desired file
     */
    public static void writeStateToFile(String fileName, int[][] state) throws IOException {
        PrintStream toFile = new PrintStream(new File(fileName));
        for (int y = 0; y < state.length; y++) { // rows
            for (int x = 0; x < state[0].length; x++) { // columns
                if (state[y][x] == 1) {
                    toFile.print("1");
                } else {
                    toFile.print("0");
                }
            }
            // only want to go to a new line as long as line is not the last line
            if (y < state.length - 1) {
                toFile.print("\n");
            }
        }
        toFile.close();
    }
}
