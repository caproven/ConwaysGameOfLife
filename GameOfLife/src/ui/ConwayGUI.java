package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JTextField;
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
     * Default width in pixels of the simulation panel. When resizing, selecting to "Reset"
     * will bring back this value.
     */
    private static final int DEFAULT_SIM_WIDTH = 600;
    /**
     * Default height in pixels of the simulation panel. When resizing, selecting to "Reset"
     * will bring back this value.
     */
    private static final int DEFAULT_SIM_HEIGHT = 600;
    /**
     * Default grid cell size in pixels for the simulation panel. When resizing, selecting to
     * "Reset" will bring back this value.
     */
    private static final int DEFAULT_GRID_DELTA = 15;
    /**
     * Width in pixels of the simulation panel. This width is also the width of the entire GUI
     * window.
     */
    private static int simulationWidth = DEFAULT_SIM_WIDTH;
    /** Height in pixels of the simulation panel. */
    private static int simulationHeight = DEFAULT_SIM_HEIGHT;
    /** Size in pixels of each cell (square) in the simulation. */
    private static int gridDelta = DEFAULT_GRID_DELTA;
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
    private JButton btnStart, btnIncrement, btnStop, btnReset, btnRead, btnWrite, btnResize;
    /** Slider that controls the tick rate of the simulation. */
    private JSlider sldrTickSpeed;
    /** Label for the tick speed slider. */
    private JLabel lblSpeedSlider;
    /** Instance of the SimulationModel, which holds the simulation state. */
    private static SimulationModel model;
    /** Boolean dictating whether the UpdateTickThread should call a simulation update. */
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
        pnlSim.setPreferredSize(new Dimension(simulationWidth, simulationHeight));
        c.add(pnlSim, BorderLayout.CENTER);
        pnlControl = new ControlsPanel();
        pnlControl.setPreferredSize(new Dimension(simulationWidth, 80));
        c.add(pnlControl, BorderLayout.SOUTH);
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
        if (e.getX() < simulationWidth && e.getX() >= 0 && e.getY() < simulationHeight
                && e.getY() >= 0 && temp.add(pFitToGrid)) {
            if (!cells.add(pFitToGrid)) {
                cells.remove(pFitToGrid);
                model.getState()[e.getY() / gridDelta][e.getX() / gridDelta] = false;
            } else {
                model.getState()[e.getY() / gridDelta][e.getX() / gridDelta] = true;
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
        int x = p.getX() / gridDelta * gridDelta;
        int y = p.getY() / gridDelta * gridDelta;
        return new Point(x, y);
    }

    /**
     * Synchronizes the list of shaded cells to reflect the current simulation state.
     */
    private void syncCellStateToGrid() {
        cells.clear();
        for (int y = 0; y < simulationHeight / gridDelta; y++) {
            for (int x = 0; x < simulationWidth / gridDelta; x++) {
                if (model.getState()[y][x]) {
                    cells.add(new Point(x * gridDelta, y * gridDelta));
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
                    g.fillRect(p.getX(), p.getY(), gridDelta, gridDelta);
                }
            }
            // Draw grid
            g.setColor(Color.GRAY);
            for (int x = gridDelta; x < simulationWidth; x += gridDelta) {
                for (int y = gridDelta; y < simulationHeight; y += gridDelta) {
                    g.drawLine(0, y, simulationWidth, y);
                }
                g.drawLine(x, 0, x, simulationHeight);
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
                    btnStart.setEnabled(false);
                    btnStop.setEnabled(true);
                    new UpdateTickThread().start();
                }
            });
            add(btnStart);
            btnIncrement = new JButton("+1");
            btnIncrement.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    doLoop = false;
                    btnStop.setEnabled(false);
                    btnStart.setEnabled(true);
                    tick();
                }
            });
            add(btnIncrement);
            btnRead = new JButton("Read");
            btnRead.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doLoop = false;
                    btnStop.setEnabled(false);
                    btnStart.setEnabled(true);
                    try {
                        model.readFromFile(getFileName(true));
                        syncCellStateToGrid();
                        ConwayGUI.this.repaint();
                    } catch (IOException ioe) {
                        JOptionPane.showMessageDialog(ConwayGUI.this,
                                "File either does not exist or is formatted incorrectly.",
                                "File Error", JOptionPane.ERROR_MESSAGE);
                    } catch (IllegalStateException ise) {
                        // user canceled operation, do nothing
                    }
                }
            });
            add(btnRead);
            btnStop = new JButton("Stop");
            btnStop.setEnabled(false); // defaults to disabled
            btnStop.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    doLoop = false;
                    btnStop.setEnabled(false);
                    btnStart.setEnabled(true);
                }
            });
            add(btnStop);
            btnReset = new JButton("Reset");
            btnReset.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doLoop = false;
                    btnStop.setEnabled(false);
                    btnStart.setEnabled(true);
                    cells.clear();
                    model = new SimulationModel(simulationWidth / gridDelta,
                            simulationHeight / gridDelta);
                    ConwayGUI.this.repaint();
                }
            });
            add(btnReset);
            btnWrite = new JButton("Write");
            btnWrite.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doLoop = false;
                    btnStop.setEnabled(false);
                    btnStart.setEnabled(true);
                    try {
                        model.writeToFile(getFileName(false));
                    } catch (IOException ioe) {
                        JOptionPane.showMessageDialog(ConwayGUI.this,
                                "Could not write to the desired file.", "File Error",
                                JOptionPane.ERROR_MESSAGE);
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
            btnResize = new JButton("Resize");
            btnResize.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doLoop = false;
                    ConwayGUI.this.setEnabled(false);
                    btnStop.setEnabled(false);
                    btnStart.setEnabled(true);
                    new ResizeWindow();
                }
            });
            add(btnResize);
        }
    }

    /**
     * Separate frame from the main window that provides options for resizing the Game of Life
     * simulation.
     * 
     * @author caproven
     */
    private class ResizeWindow extends JFrame {
        /** ID number used for serialization. */
        private static final long serialVersionUID = 1L;
        /** Panels composing the resize options window. */
        private JPanel pnlButtons, pnlInputs, pnlWidth, pnlHeight, pnlGridSize;
        /** Buttons used to either reset values or apply the currently entered ones. */
        private JButton btnReset, btnAccept;
        /** Labels for each of the text fields. */
        private JLabel lblWidth, lblHeight, lblGridSize;
        /** Text fields for entering in new simulation dimensions. */
        private JTextField tfWidth, tfHeight, tfGridSize;
        /** Minimum size in pixels the simulation may be (applies to both width and height). */
        private final int minDimension = 250;
        /** Minimum size in pixels the grid size may be. */
        private final int minGrid = 5;

        /**
         * Constructs the new window for resize options.
         */
        public ResizeWindow() {
            // top left corner of window is ~1/4 in from corner of main window
            int xLocation = ConwayGUI.this.getLocationOnScreen().x
                    + ConwayGUI.this.getSize().width / 4;
            int yLocation = ConwayGUI.this.getLocationOnScreen().y
                    + ConwayGUI.this.getSize().height / 4;
            setLocation(xLocation, yLocation);
            setTitle("Resize Options");
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    closeResizeWindow();
                }
            });
            setResizable(false);
            Container c = getContentPane();
            c.setPreferredSize(new Dimension(300, 170));
            c.setLayout(new BorderLayout());
            // Set up pnlInputs
            pnlInputs = new JPanel();
            pnlInputs.setLayout(new GridLayout(3, 1));
            pnlWidth = new JPanel(); // composed of width label and text field
            pnlWidth.setLayout(new FlowLayout(FlowLayout.LEFT));
            lblWidth = new JLabel("Width(px):");
            tfWidth = new JTextField(Integer.toString(simulationWidth));
            tfWidth.setColumns(10);
            pnlWidth.add(lblWidth);
            pnlWidth.add(tfWidth);
            pnlInputs.add(pnlWidth);
            pnlHeight = new JPanel(); // composed of height label and text field
            pnlHeight.setLayout(new FlowLayout(FlowLayout.LEFT));
            lblHeight = new JLabel("Height(px):");
            tfHeight = new JTextField(Integer.toString(simulationHeight));
            tfHeight.setColumns(10);
            pnlHeight.add(lblHeight);
            pnlHeight.add(tfHeight);
            pnlInputs.add(pnlHeight);
            pnlGridSize = new JPanel(); // composed of grid size label and text field
            pnlGridSize.setLayout(new FlowLayout(FlowLayout.LEFT));
            lblGridSize = new JLabel("Grid Size(px):");
            tfGridSize = new JTextField(Integer.toString(gridDelta));
            tfGridSize.setColumns(10);
            pnlGridSize.add(lblGridSize);
            pnlGridSize.add(tfGridSize);
            pnlInputs.add(pnlGridSize);
            // Set up pnlButtons
            pnlButtons = new JPanel();
            pnlButtons.setLayout(new BorderLayout());
            btnReset = new JButton("Reset");
            btnReset.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    tfWidth.setText(Integer.toString(DEFAULT_SIM_HEIGHT));
                    tfHeight.setText(Integer.toString(DEFAULT_SIM_HEIGHT));
                    tfGridSize.setText(Integer.toString(DEFAULT_GRID_DELTA));
                }
            });
            pnlButtons.add(btnReset, BorderLayout.WEST);
            btnAccept = new JButton("Accept");
            btnAccept.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int[] formattedInputs = formattedInputs(tfWidth.getText(), tfHeight.getText(),
                            tfGridSize.getText());
                    if (formattedInputs == null) { // had invalid inputs
                        JOptionPane.showMessageDialog(ResizeWindow.this,
                                "Entered an invalid input.\n"
                                        + "Width and Height must be integers >= 250 while\n"
                                        + "Grid Size must be >= 5. Width and Height must also\n"
                                        + "be divisible by the Grid Size.",
                                "Input Error", JOptionPane.ERROR_MESSAGE);
                    } else if (formattedInputs[0] == -1) {
                        JOptionPane.showMessageDialog(ResizeWindow.this,
                                "Entered an invalid input.\nResulting grid cannot be smaller than 3x3.",
                                "Input Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        doLoop = false;
                        simulationWidth = formattedInputs[0];
                        simulationHeight = formattedInputs[1];
                        gridDelta = formattedInputs[2];
                        ConwayGUI.this.dispose();
                        dispose();
                        createNewGUIInstance();
                    }
                }
            });
            pnlButtons.add(btnAccept, BorderLayout.EAST);
            c.add(pnlInputs, BorderLayout.CENTER);
            c.add(pnlButtons, BorderLayout.SOUTH);
            pack();
            setVisible(true);
        }

        /**
         * Formats input strings into an array of three integers in order: width, height,
         * grid_size.
         * 
         * @param widthStr    String containing desired width in pixels
         * @param heightStr   String containing desired height in pixels
         * @param gridSizeStr String containing desired grid size in pixel
         * @return Integer array containing order, height, and gridSize. If resulting grid cell
         *         dimensions would have been less than 3x3, returns the first index as -1.
         */
        private int[] formattedInputs(String widthStr, String heightStr, String gridSizeStr) {
            int widthInt, heightInt, gridSizeInt;
            try {
                widthInt = Integer.parseInt(widthStr);
                heightInt = Integer.parseInt(heightStr);
                gridSizeInt = Integer.parseInt(gridSizeStr);
            } catch (NumberFormatException nfe) {
                return null;
            }
            if (widthInt < minDimension || heightInt < minDimension || gridSizeInt < minGrid
                    || widthInt % gridSizeInt != 0 || heightInt % gridSizeInt != 0) {
                return null;
            }
            int[] returnVals = { widthInt, heightInt, gridSizeInt };
            if (widthInt / gridSizeInt < 3 || heightInt / gridSizeInt < 3) {
                returnVals[0] = -1;
            }
            return returnVals;
        }
        
        private void closeResizeWindow() {
            ConwayGUI.this.setEnabled(true);
            this.dispose();
        }
    }

    /**
     * Thread class that calls the simulation to tick every _ milliseconds when
     * ConwayGUI.doLoop is set to true.
     * 
     * @author caproven
     */
    private class UpdateTickThread extends Thread {
        @Override
        public void run() {
            while (doLoop) {
                tick();
                try {
                    Thread.sleep(sldrTickSpeed.getMaximum() + 1 - sldrTickSpeed.getValue());
                } catch (InterruptedException e) {
                    break;
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
        createNewGUIInstance();
    }

    private static void createNewGUIInstance() {
        cells = Collections.synchronizedSet(new HashSet<Point>());
        temp = Collections.synchronizedSet(new HashSet<Point>());
        model = new SimulationModel(simulationWidth / gridDelta, simulationHeight / gridDelta);
        new ConwayGUI();
    }
}