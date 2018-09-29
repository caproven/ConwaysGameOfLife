package ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

class IterateSimulation extends Thread {
    public void run() {
        while (true) {
            if (ConwayGUI.doLoop) {
                System.out.println("<Run iteration>");
                ConwayGUI.updateGrid();
                ConwayGUI.drawSimulation();
            }
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * Using the Swing/AWT libraries, simulates Conway's Game of Life, a simple rule-set for
 * cell automation. Rules can be found at
 * https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life#Rules.
 * @author caproven
 */
public class ConwayGUI extends JFrame {
    
    static boolean doLoop = false;

    /** Number of rows the simulation will have */
    private static final int SIM_ROWS = 30;
    /** Number of columns the simulation will have */
    private static final int SIM_COLS = 60;
    /* ID used for object serialization */
    private static final long serialVersionUID = 1L;

    /** Panel holding the simulation, a 2d array of JButtons */
    private static JPanel pnlSimulation;
    /** Panel holding the application options */
    private JPanel pnlOptions;
    /** Button to start the simulation */
    private JButton btnStart;
    /** Button to stop the simulation */
    private JButton btnStop;
    /** Button to read a given simulation state from a file */
    private JButton btnReadFile;
    /** Button to write current simulation state to a file */
    private JButton btnWriteFile;

    /**
     * Grid of JButtons acting as cells in the simulation. Background color indicates state,
     * where BLACK is alive and WHITE is dead.
     */
    private static JButton[][] cellGrid;
    /**
     * Holds the states for corresponding "cells", linked by index. True indicates cell is
     * alive, False indicates cell is dead
     */
    private static boolean[][] cellState;
    /** A duplicate of cellState[][] used for updating all cells simultaneously */
    private static boolean[][] cellStateInc;

    /**
     * Constructs the GUI, setting a simulation and options panel. Buttons and arrays are
     * initialized, with ActionListeners being added to respective buttons.
     */
    public ConwayGUI() {
        // General GUI details, basic initialization
        Container c = getContentPane();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        setSize(800, 600);
        setResizable(false);
        setLocation(50, 50);
        setTitle("Conway's Game of Life");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Initialize GUI Buttons and Panels
        btnStart = new JButton("Start");
        btnStop = new JButton("Stop");
        btnReadFile = new JButton("Read from File");
        btnWriteFile = new JButton("Write to File");

        pnlSimulation = new JPanel();
        pnlSimulation.setLayout(new GridLayout(SIM_ROWS, SIM_COLS));

        // Sets up the boolean arrays, with index corresponding to the equivalent cell in the
        // JButtons array. True means cell is alive, False means cell is dead. Default state is
        // False.
        cellState = new boolean[SIM_ROWS + 2][SIM_COLS + 2];
        cellStateInc = new boolean[SIM_ROWS + 2][SIM_COLS + 2];

        // Sets up the JButton array, where each JButton represents a cell in the simulation.
        cellGrid = new JButton[SIM_ROWS + 2][SIM_COLS + 2];

        // Fills cellGrid with JButtons. In the above two declarations, "+ 2" is used to
        // effectively form a one-unit perimeter around the cells that will actually be displayed
        // to the user. This is to handle cell updates at the edge of the screen, may be replaced
        // with a different solution.
        for (int row = 0; row < cellGrid.length; row++) {
            for (int col = 0; col < cellGrid[1].length; col++) {
                cellGrid[row][col] = new JButton();

                // Adds an actionListener to cells within the one-unit border. These are the only
                // cells that will be displayed in the simulation.
                if (row > 0 && row < cellGrid.length - 1 && col > 0 && col < cellGrid[1].length) {
                    JButton localBtn = cellGrid[row][col];
                    int y = row;
                    int x = col; // had to declare these because the compiler did not agree with
                                 // the scope of row & col
                    localBtn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            cellState[y][x] = !cellState[y][x];
                            if (cellState[y][x]) {
                                localBtn.setBackground(Color.BLACK);
                            } else {
                                localBtn.setBackground(Color.WHITE);
                            }
                        }
                    }); // actionListener
                } // if
            } // inner for
        } // outer for
        drawSimulation();

        // Sets up controls at bottom of window
        pnlOptions = new JPanel();
        pnlOptions.setLayout(new GridLayout(2, 2, 0, 0));
        pnlOptions.add(btnStart);
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //updateGrid();
                //drawSimulation();
                
                doLoop = true;
            }
        });
        pnlOptions.add(btnReadFile);
        pnlOptions.add(btnStop);
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doLoop = false;
            }
        });
        pnlOptions.add(btnWriteFile);

        // Sets borders around the JPanels to make them look a lot cooler than they really are.
        Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        TitledBorder borderSimulation = BorderFactory.createTitledBorder(lowerEtched,
                "Simulation");
        pnlSimulation.setBorder(borderSimulation);
        TitledBorder borderOptions = BorderFactory.createTitledBorder(lowerEtched, "Options");
        pnlOptions.setBorder(borderOptions);
        
        // Starts the separate thread that calls a tick every 500ms
        IterateSimulation obj1 = new IterateSimulation();
        obj1.start();

        // Tells the program to actually display our panels.
        c.add(pnlSimulation);
        c.add(pnlOptions);
        setVisible(true);
    }

    /**
     * Draws the state of the simulation by clearing it and re-checking current states.
     */
    static void drawSimulation() {
        pnlSimulation.removeAll();

        // Remember that only the inside of our array "border" is drawn.
        for (int row = 1; row < cellGrid.length - 1; row++) {
            for (int col = 1; col < cellGrid[1].length - 1; col++) {
                pnlSimulation.add(cellGrid[row][col]);
                if (cellState[row][col]) { // BLACK if alive
                    cellGrid[row][col].setBackground(Color.BLACK);
                } else { // WHITE if dead
                    cellGrid[row][col].setBackground(Color.WHITE);
                }
            }
        }

        pnlSimulation.revalidate();
        pnlSimulation.repaint();

    }

    /**
     * Updates the cellGrid by comparing live neighbor counts (all 8 adjacent cells) to
     * Conway's specified rule-set.
     */
    static void updateGrid() {
        // Any live cell with fewer than two live neighbors dies, as if by under population.
        // Any live cell with two or three live neighbors lives on to the next generation.
        // Any live cell with more than three live neighbors dies, as if by over-population.
        // Any dead cell with exactly three live neighbors becomes a live cell, as if by
        // reproduction.

        // Sets cellStateInc to the current cellState
        for (int i = 1; i < cellGrid.length - 1; i++) {
            for (int j = 1; j < cellGrid[1].length - 1; j++) {
                cellStateInc[i][j] = cellState[i][j];
            }
        }

        // Updates each cell based on the states of its neighbors. Important to note that
        // cellState is updated based on cellStateInc so that all cells may be updated at once
        // instead of linearly.
        for (int i = 1; i < cellGrid.length - 1; i++) {
            for (int j = 1; j < cellGrid[1].length - 1; j++) {
                int neighbors = liveNeighbors(i, j);
                if (cellStateInc[i][j]) { // If cell is alive
                    if (neighbors < 2) {
                        cellState[i][j] = false;
                    } else if (neighbors > 3) {
                        cellState[i][j] = false;
                    }
                    // if 2 or 3, cell survives
                } else { // If cell is dead
                    if (neighbors == 3) {
                        cellState[i][j] = true;
                    }
                }
            } // for
        }
    }

    /**
     * Counts the number of live "neighbors" a cell has, checking all 8 adjacent cells.
     * @param row Row in cellGrid of specific cell to analyze
     * @param col Column in cellGrid of specific cell to analyze
     * @return Number of live neighbors, an int between 0 and 8 inclusive
     */
    private static int liveNeighbors(int row, int col) {
        int aliveNeighbors = 0;

        if (cellStateInc[row - 1][col - 1]) { // top left
            aliveNeighbors++;
        }
        if (cellStateInc[row - 1][col]) { // top
            aliveNeighbors++;
        }
        if (cellStateInc[row - 1][col + 1]) { // top right
            aliveNeighbors++;
        }
        if (cellStateInc[row][col - 1]) { // left
            aliveNeighbors++;
        }
        if (cellStateInc[row][col + 1]) { // right
            aliveNeighbors++;
        }
        if (cellStateInc[row + 1][col - 1]) { // bottom left
            aliveNeighbors++;
        }
        if (cellStateInc[row + 1][col]) { // bottom
            aliveNeighbors++;
        }
        if (cellStateInc[row + 1][col + 1]) { // bottom right
            aliveNeighbors++;
        }
        return aliveNeighbors;
    }

    /**
     * Starts the program by constructing the GUI
     * @param args command line args (not used)
     */
    public static void main(String[] args) {
        new ConwayGUI();
    }

}
