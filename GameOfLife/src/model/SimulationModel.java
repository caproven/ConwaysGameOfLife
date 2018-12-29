package model;

/**
 * Model for the Game of Life simulation. Holds states of the simulation, available to the
 * GUI.
 * 
 * @author caproven
 */
public class SimulationModel {
    /**
     * 2D boolean arrays holding the current simulation state. True indicates a "live" cell.
     * cellStateInc (increment) is used to update cellState during updates.
     */
    private boolean[][] cellState, cellStateInc;

    /**
     * Constructs the SimulationModel with the given dimensions. The X and Y dimensions here
     * represent the number of cells on each axis, not the visual size of the simulation.
     * 
     * @param x Dimension of simulation along the X axis
     * @param y Dimension of simulation along the Y axis
     */
    public SimulationModel(int x, int y) {
        cellState = new boolean[y][x];
        cellStateInc = new boolean[y][x];
    }

    /**
     * Retrieves the current state of the simulation.
     * 
     * @return 2D boolean array representing current cell states
     */
    public boolean[][] getState() {
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
                if (cellStateInc[y][x]) { // If cell is alive
                    if (neighbors < 2) {
                        cellState[y][x] = false;
                    } else if (neighbors > 3) {
                        cellState[y][x] = false;
                    }
                    // if 2 or 3, cell survives
                } else { // If cell is dead
                    if (neighbors == 3) {
                        cellState[y][x] = true;
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
     * 
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
        if (cellStateInc[yM1][xM1]) // top left
            aliveNeighbors++;
        if (cellStateInc[yM1][x]) // top
            aliveNeighbors++;
        if (cellStateInc[yM1][xP1]) // top right
            aliveNeighbors++;
        if (cellStateInc[y][xM1]) // left
            aliveNeighbors++;
        if (cellStateInc[y][xP1]) // right
            aliveNeighbors++;
        if (cellStateInc[yP1][xM1]) // bottom left
            aliveNeighbors++;
        if (cellStateInc[yP1][x]) // bottom
            aliveNeighbors++;
        if (cellStateInc[yP1][xP1]) // bottom right
            aliveNeighbors++;
        return aliveNeighbors;
    }
}
