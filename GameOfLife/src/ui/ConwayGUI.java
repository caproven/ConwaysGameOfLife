package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import model.SimulationModel;
import model.Point;

/**
 * GUI for the Game of Life simulation. Holds the simulation itself, along with controls.
 * 
 * @author caproven
 */
public class ConwayGUI extends JFrame {
    /** ID number used for serialization. */
    private static final long serialVersionUID = 1L;
    /**
     * Width in pixels of the simulation panel. This width is also the width of the entire GUI
     * window.
     */
    private static final int SIMULATION_WIDTH = 600;
    /** Height in pixels of the simulation panel. */
    private static final int SIMULATION_HEIGHT = 600;
    /** Size in pixels of each cell (square) in the simulation. */
    private static final int GRID_DELTA = 15;
    /** Maximum time interval between simulation updates / ticks in milliseconds. */
    private static final int TICKRATE_MAX = 70;
    /**
     * List of cells currently displayed as "alive" (shaded). Points contained in the list are
     * located at the top left corner of the cell in the display, used for drawing the shaded
     * cells.
     */
    private static Set<Point> cells;
    /**
     * List of cells drawn upon each mouse event. Used so that the same cell is not triggered
     * multiple times while dragging the mouse. Points contained in the list are located at
     * the top left corner of the cell in the display, used for drawing the shaded cells.
     */
    private static Set<Point> temp;
    /** Custom panels composing the GUI window. */
    private JPanel pnlSim, pnlControl;
    /** Buttons allowing control of the simulation. */
    private JButton btnStart, btnIncrement, btnStop, btnReset, btnRead, btnWrite;
    /** Slider that controls the tick rate of the simulation. */
    private JSlider sldrTickSpeed;
    /** Label for the tick speed slider. */
    private JLabel lblSpeedSlider;
    /** Instance of the SimulationModel, which holds the simulation state. */
    private static SimulationModel model;
    /**
     * Boolean dictating whether the SimulationTicker thread should call a simulation update.
     */
    boolean doLoop = false;

    /**
     * Constructs the GUI, initializing panels and adding mouse listeners.
     */
    public ConwayGUI() {
        setLocation(50, 50);
        setTitle("Conway's Game of Life");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        pnlSim = new SimulationPanel();
        pnlSim.addMouseMotionListener(new MouseMotionListener() {
            /*
             * These two methods will never be triggered by the same event. X and Y are relative to
             * the window. In all cases, the top left corner is 0,0 and the bottom right is the max
             * coords.
             */
            @Override
            public void mouseDragged(MouseEvent e) {
                // mouse is clicked (being held down)
                toggleCell(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                // do nothing
            }
        });
        pnlSim.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleCell(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                temp.clear();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // do nothing
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // do nothing
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // do nothing
            }
        });
        pnlSim.setPreferredSize(new Dimension(SIMULATION_WIDTH, SIMULATION_HEIGHT));
        c.add(pnlSim, BorderLayout.CENTER);
        pnlControl = new ControlsPanel();
        pnlControl.setPreferredSize(new Dimension(SIMULATION_WIDTH, 80));
        c.add(pnlControl, BorderLayout.SOUTH);
        SimulationTicker simThread = new SimulationTicker();
        simThread.start();
        pack(); // used along with JPanel.setPreferredSize() to dictate window size
        setVisible(true);
    }

    /**
     * Toggles the state of a cell when it is clicked or dragged over.
     * 
     * @param e MouseEvent from the mouse being clicked over the simulation panel (contains X
     *          and Y coordinates)
     */
    private void toggleCell(MouseEvent e) {
        Point pFitToGrid = fitToGrid(new Point(e.getX(), e.getY()));
        if (e.getX() < SIMULATION_WIDTH && e.getX() >= 0 && e.getY() < SIMULATION_HEIGHT && e.getY() >= 0
                && temp.add(pFitToGrid)) {
            if (!cells.add(pFitToGrid)) {
                cells.remove(pFitToGrid);
                model.getState()[e.getY() / GRID_DELTA][e.getX() / GRID_DELTA] = false;
            } else {
                model.getState()[e.getY() / GRID_DELTA][e.getX() / GRID_DELTA] = true;
            }
            repaint();
        } // skips invalid cursor locations (from dragging outside window)
    }

    /**
     * Fits a Point to the grid, altering its coordinates to match the top left corner of the
     * represented cell.
     * 
     * @param p Point with potentially unfit coordinates
     * @return Point with coordinates fit correctly to the grid
     */
    private Point fitToGrid(Point p) {
        int x = p.getX() / GRID_DELTA * GRID_DELTA;
        int y = p.getY() / GRID_DELTA * GRID_DELTA;
        return new Point(x, y);
    }

    /**
     * Synchronizes the list of shaded cells to reflect the current simulation state.
     */
    private void syncCellStateToGrid() {
        cells.clear();
        for (int y = 0; y < SIMULATION_HEIGHT / GRID_DELTA; y++) {
            for (int x = 0; x < SIMULATION_WIDTH / GRID_DELTA; x++) {
                if (model.getState()[y][x]) {
                    cells.add(new Point(x * GRID_DELTA, y * GRID_DELTA));
                }
            }
        }
    }

    /**
     * Performs a single iteration ("tick") to the system model, then calls the GUI to reflect
     * changes.
     */
    private void tick() {
        model.updateGrid();
        syncCellStateToGrid();
        repaint();
    }

    private String getFileName(boolean chooserType) throws FileNotFoundException {
        JFileChooser fc = new JFileChooser("./");
        fc.setApproveButtonText("Select");
        int returnVal = Integer.MIN_VALUE;
        if (chooserType) { // open file
            fc.setDialogTitle("Load Game State");
            returnVal = fc.showOpenDialog(this);
        } else { // save file
            fc.setDialogTitle("Save Game State");
            returnVal = fc.showSaveDialog(this);
        }
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            throw new IllegalStateException();
        }
        File catalogFile = fc.getSelectedFile();
        return catalogFile.getAbsolutePath();
    }

    /**
     * Custom JPanel enabling the painting of the grid and "live" cells.
     * 
     * @author caproven
     */
    class SimulationPanel extends JPanel {
        /** ID number used for serialization. */
        private static final long serialVersionUID = 1L;

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Resets display
            setBackground(Color.WHITE);
            // Draw live cells
            g.setColor(Color.BLACK);
            synchronized (cells) {
                for (Point p : cells) {
                    g.fillRect(p.getX(), p.getY(), GRID_DELTA, GRID_DELTA);
                }
            }
            // Draw grid
            g.setColor(Color.GRAY);
            for (int x = GRID_DELTA; x < SIMULATION_WIDTH; x += GRID_DELTA) {
                for (int y = GRID_DELTA; y < SIMULATION_HEIGHT; y += GRID_DELTA) {
                    g.drawLine(0, y, SIMULATION_WIDTH, y);
                }
                g.drawLine(x, 0, x, SIMULATION_HEIGHT);
            }
        }
    }

    /**
     * Custom JPanel containing buttons for the system controls.
     * 
     * @author caproven
     */
    class ControlsPanel extends JPanel {
        /** ID number used for serialization. */
        private static final long serialVersionUID = 1L;

        /**
         * Constructs the panel and adds buttons to it.
         */
        public ControlsPanel() {
            super(new GridLayout(3, 3));
            btnStart = new JButton("Start");
            btnStart.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    doLoop = true;
                }
            });
            add(btnStart);
            btnIncrement = new JButton("+1");
            btnIncrement.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    doLoop = false;
                    tick();
                }
            });
            add(btnIncrement);
            btnRead = new JButton("Read");
            btnRead.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doLoop = false;
                    try {
                        model.readFromFile(getFileName(true));
                        syncCellStateToGrid();
                        ConwayGUI.this.repaint();
                    } catch (IOException ioe) {
                        JOptionPane.showMessageDialog(ConwayGUI.this,
                                "File either does not exist or is formatted incorrectly.", "File Error",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (IllegalStateException ise) {
                        // user canceled operation, do nothing
                    }
                }
            });
            add(btnRead);
            btnStop = new JButton("Stop");
            btnStop.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    doLoop = false;
                }
            });
            add(btnStop);
            btnReset = new JButton("Reset");
            btnReset.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doLoop = false;
                    cells.clear();
                    model = new SimulationModel(SIMULATION_WIDTH / GRID_DELTA, SIMULATION_HEIGHT / GRID_DELTA);
                    ConwayGUI.this.repaint();
                }
            });
            add(btnReset);
            btnWrite = new JButton("Write");
            btnWrite.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doLoop = false;
                    try {
                        model.writeToFile(getFileName(false));
                    } catch (IOException ioe) {
                        JOptionPane.showMessageDialog(ConwayGUI.this, "Could not write to the desired file.",
                                "File Error", JOptionPane.ERROR_MESSAGE);
                    } catch (IllegalStateException ise) {
                        // user canceled operation, do nothing
                    }
                }
            });
            add(btnWrite);
            lblSpeedSlider = new JLabel("Speed:", SwingConstants.RIGHT);
            add(lblSpeedSlider);
            sldrTickSpeed = new JSlider(0, TICKRATE_MAX);
            add(sldrTickSpeed);
        }
    }

    /**
     * Thread class that calls the simulation to tick every _ milliseconds when
     * ConwayGUI.doLoop is set to true.
     * 
     * @author caproven
     */
    private class SimulationTicker extends Thread {
        @Override
        public void run() {
            while (true) {
                if (doLoop) {
                    tick();
                }
                try {
                    Thread.sleep(sldrTickSpeed.getMaximum() + 1 - sldrTickSpeed.getValue());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Initializes necessary fields and starts the GUI.
     * 
     * @param args Command line args (not used)
     */
    public static void main(String[] args) {
        cells = Collections.synchronizedSet(new HashSet<Point>());
        temp = Collections.synchronizedSet(new HashSet<Point>());
        model = new SimulationModel(SIMULATION_WIDTH / GRID_DELTA, SIMULATION_HEIGHT / GRID_DELTA);
        new ConwayGUI();
    }
}