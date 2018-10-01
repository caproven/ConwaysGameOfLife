## Synopsis

Currently in Version 0.4. See Updates section for more details.

This project is a simulation for [Conway's Game of Life](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life). The entire thing is written in Java, using the Swing/AWT libraries. The cell automation follows 4 simple rules:
1. Any live cell with fewer than two live neighbors dies, as if by under population.
2. Any live cell with two or three live neighbors lives on to the next generation.
3. Any live cell with more than three live neighbors dies, as if by over-population.
4. Any dead cell with exactly three live neighbors becomes a live cell, as if by reproduction.

## Motivation

As much as I may despise Java Swing, I'll have to use it eventually. So here I am, getting practice (also the Game of Life is just fun to watch).

## Installation

Standard Java procedures, recommend generating a .jar executable.

## Tests

Exclusively using the latest in hands-on debugging techniques (I spam click everything and see if it works).

## Updates

Version 0.4

Added
- Read and write functionality! Everything is through .txt files (who doesn't love a nice 2d array in a text file?).

Fixed
- N/A

Other
- Need to add verification of contents of input files. Honestly don't know how the program will act if any chars besides '0' and '1' are read.