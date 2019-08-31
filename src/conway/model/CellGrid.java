package conway.model;

import java.io.IOException;

import conway.util.FileIO;

/**
 * Model class storing the states of all cells within the simulation.
 * @author caproven
 */
public class CellGrid {

    /**
     * Arrays holding the cell states. Alternated in usage to handle processing next state.
     * A 0 represents a dead cell while a 1 represents a living cell.
     */
    private int[][] grid0, grid1;
    /** Determines which array holds the current cell states. */
    private int active;

    /**
     * Constructs the CellGrid with the given dimensions as the number of cells.
     * @param x Number of horizontal cells
     * @param y Number of vertical cells
     */
    public CellGrid(int x, int y) {
        grid0 = new int[y][x];
        grid1 = new int[y][x];
        active = 0;
    }

    /**
     * Retrieves the cell states representing the grid.
     * @return 2D array of cell states (0: dead, 1: alive)
     */
    public int[][] getGrid() {
        return getActiveGrid();
    }

    /**
     * Determines and retrieves the currently active grid. Used internally to uphold abstraction.
     * @return 2D array of the currently active grid
     */
    private int[][] getActiveGrid() {
        return (active == 0) ? (grid0) : (grid1);
    }

    /**
     * Toggles the state of the cell at the given coords in the grid.
     * @param x X-coord of the cell to be toggled
     * @param y Y-coord of the cell to be toggled
     */
    public void toggleCell(int x, int y) {
        getActiveGrid()[y][x] ^= 1;
    }

    /**
     * Performs a single update / tick to the cell automata's state.
     */
    public void updateGrid() {
        int[][] activeGrid;
        int[][] incGrid;

        if (active == 0) {
            activeGrid = grid0;
            incGrid = grid1;
        } else {
            activeGrid = grid1;
            incGrid = grid0;
        }

        // updates cells based off their neighbors
        for (int y = 0; y < activeGrid.length; y++) {
            for (int x = 0; x < activeGrid[0].length; x++) {
                int neighbors = liveNeighbors(x, y);
                if (activeGrid[y][x] == 1) { // If cell is alive
                    if (neighbors < 2 || neighbors > 3) {
                        incGrid[y][x] = 0;
                    } else {
                        incGrid[y][x] = 1;
                    }
                    // if 2 or 3, cell survives
                } else { // If cell is dead
                    if (neighbors == 3) {
                        incGrid[y][x] = 1;
                    } else {
                        incGrid[y][x] = 0;
                    }
                }
            }
        }

        active ^= 1;
    }

    /**
     * Determines the number of living neighbors a cell has, considering all eight adjacent cells.
     * @param x X-coord of the cell whose neighbors will be counted
     * @param y Y-coord of the cell whose neighbors will be counted
     * @return Number of living neighbors the specified cell has
     */
    private int liveNeighbors(int x, int y) {
        int[][] grid = getActiveGrid();

        int aliveNeighbors = 0;

        int yM1, yP1, xM1, xP1; // "Minus One, Plus One"
        if (y == 0) {
            yM1 = grid.length - 1;
            yP1 = y + 1;
        } else if (y == grid.length - 1) {
            yM1 = y - 1;
            yP1 = 0;
        } else {
            yM1 = y - 1;
            yP1 = y + 1;
        }
        if (x == 0) {
            xM1 = grid[0].length - 1;
            xP1 = x + 1;
        } else if (x == grid[0].length - 1) {
            xM1 = x - 1;
            xP1 = 0;
        } else {
            xM1 = x - 1;
            xP1 = x + 1;
        }

        aliveNeighbors += grid[yM1][xM1]; // top left
        aliveNeighbors += grid[yM1][x]; // top
        aliveNeighbors += grid[yM1][xP1]; // top right
        aliveNeighbors += grid[y][xM1]; // left
        aliveNeighbors += grid[y][xP1]; // right
        aliveNeighbors += grid[yP1][xM1]; // bottom left
        aliveNeighbors += grid[yP1][x]; // bottom
        aliveNeighbors += grid[yP1][xP1]; // bottom right

        return aliveNeighbors;
    }

    /**
     * Reads cell states from a file, updating the grid to reflect them.
     * @param fileName Name of the file whose contents will be read
     * @throws IOException if file cannot be read or cannot be located
     */
    public void readFromFile(String fileName) throws IOException {
        grid0 = FileIO.read(fileName, grid0[0].length, grid0.length);
        active = 0;
    }

    /**
     * Writes the current grid to a file.
     * @param fileName Name of the file to be written to
     * @throws IOException if file cannot be created or written to
     */
    public void writeToFile(String fileName) throws IOException {
        FileIO.write(fileName, getActiveGrid());
    }
}
