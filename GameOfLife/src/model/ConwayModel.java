package model;

import java.io.IOException;

import util.ConwayReaderUtil;
import util.ConwayWriterUtil;

/**
 * Model for the Game of Life simulation. Holds states of the simulation, available to the
 * GUI.
 * @author caproven
 */
public class ConwayModel {
    /**
     * 2D int arrays holding the current simulation state. True indicates a "live" cell.
     * cellStateInc (increment) is used to update cellState during updates.
     */
    private int[][] cellState, cellStateInc;

    /**
     * Constructs the SimulationModel with the given dimensions. The X and Y dimensions here
     * represent the number of cells on each axis, not the visual size of the simulation.
     * @param x Dimension of simulation along the X axis
     * @param y Dimension of simulation along the Y axis
     */
    public ConwayModel(int x, int y) {
        cellState = new int[y][x];
        cellStateInc = new int[y][x];
    }

    /**
     * Retrieves the current state of the simulation.
     * @return 2D int array representing current cell states
     */
    public int[][] getState() {
        return cellState;
    }

    /**
     * Performs a single update iteration to the simulation's cell states.
     */
    public void updateGrid() {
        // Sets cellStateInc to the current cellState
        for (int y = 0; y < cellState.length; y++) {
            for (int x = 0; x < cellState[0].length; x++) {
                cellStateInc[y][x] = cellState[y][x];
            }
        }
        // updates cells based off their neighbors
        for (int y = 0; y < cellState.length; y++) {
            for (int x = 0; x < cellState[0].length; x++) {
                int neighbors = liveNeighbors(y, x);
                if (cellStateInc[y][x] == 1) { // If cell is alive
                    if (neighbors < 2 || neighbors > 3) {
                        cellState[y][x] = 0;
                    }
                    // if 2 or 3, cell survives
                } else { // If cell is dead
                    if (neighbors == 3) {
                        cellState[y][x] = 1;
                    }
                }
            } // inner for
        }
    }

    /**
     * Calculates the number of "live" adjacent cells a single cell has, including diagonals
     * for a total number of 8 surrounding cells to be checked. When checking cells at the
     * border of the cell grid, a "wrap around" effect is applied (i.e. a cell on the left
     * border will check the right border when checking cells to the left of itself).
     * @param y Y coordinate of the center cell for calculating neighbors.
     * @param x X coordinate of the center cell for calculating neighbors.
     * @return Number of "live" cells. Range is [0,8].
     */
    private int liveNeighbors(int y, int x) {
        int aliveNeighbors = 0;
        // used for common code for all cells, regardless of wrap around or not
        int yM1, yP1, xM1, xP1;
        if (y == 0) {
            yM1 = cellState.length - 1;
            yP1 = y + 1;
        } else if (y == cellState.length - 1) {
            yM1 = y - 1;
            yP1 = 0;
        } else {
            yM1 = y - 1;
            yP1 = y + 1;
        }
        if (x == 0) {
            xM1 = cellState[0].length - 1;
            xP1 = x + 1;
        } else if (x == cellState[0].length - 1) {
            xM1 = x - 1;
            xP1 = 0;
        } else {
            xM1 = x - 1;
            xP1 = x + 1;
        }

        aliveNeighbors += cellStateInc[yM1][xM1]; // top left
        aliveNeighbors += cellStateInc[yM1][x]; // top
        aliveNeighbors += cellStateInc[yM1][xP1]; // top right
        aliveNeighbors += cellStateInc[y][xM1]; // left
        aliveNeighbors += cellStateInc[y][xP1]; // right
        aliveNeighbors += cellStateInc[yP1][xM1]; // bottom left
        aliveNeighbors += cellStateInc[yP1][x]; // bottom
        aliveNeighbors += cellStateInc[yP1][xP1]; // bottom right

        return aliveNeighbors;
    }

    /**
     * Reads a simulation state from the given file.
     * @param fileName Name of the file to be read
     * @throws IOException if file does not exist or is formatted incorrectly
     */
    public void readFromFile(String fileName) throws IOException {
        cellState = ConwayReaderUtil.readStateFromFile(fileName, cellState[0].length,
                cellState.length);
    }

    /**
     * Writes the current simulation state to the given file.
     * @param fileName Name of the file to be written to
     * @throws IOException if there is an error writing to the desired file
     */
    public void writeToFile(String fileName) throws IOException {
        ConwayWriterUtil.writeStateToFile(fileName, cellState);
    }
}
