## Synopsis

This project is a simulation for [Conway's Game of Life](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life). The entire thing is written in Java, using the Swing/AWT libraries. The cell automation follows 4 simple rules:
1. Any live cell with fewer than two live neighbors dies, as if by under population.
2. Any live cell with two or three live neighbors lives on to the next generation.
3. Any live cell with more than three live neighbors dies, as if by over-population.
4. Any dead cell with exactly three live neighbors becomes a live cell, as if by reproduction.

## Motivation

I find cellular automata to be fascinating, and Conway's Game of Life seemed like a great starting point. I had seen the Game of Life before, but wanted to make my own implementation of it for practice with Java Swing. It is a rather simple simulation, but I decided that features could slowly be implemented, giving further experience with the software design cycle.

## Installation

The latest update can be found as an executable .jar in the [GameOfLife/executables/](https://github.com/caproven/GameOfLife/tree/master/executables) directory. Either double click GoL.jar to run or enter "java -jar GoL.jar" into a console while in the same directory as the downloaded file.
