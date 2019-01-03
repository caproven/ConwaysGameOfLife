## Synopsis

This project is a simulation for [Conway's Game of Life](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life). The entire thing is written in Java, using the Swing/AWT libraries. The cell automation follows 4 simple rules:
1. Any live cell with fewer than two live neighbors dies, as if by under population.
2. Any live cell with two or three live neighbors lives on to the next generation.
3. Any live cell with more than three live neighbors dies, as if by over-population.
4. Any dead cell with exactly three live neighbors becomes a live cell, as if by reproduction.

## Motivation

I find cellular automata to be fascinating, and Conway's Game of Life seemed like a great starting point. I had seen the Game of Life before, but wanted to make my own implementation of it for practice with Java Swing. It is a rather simple simulation, but I decided that features could slowly be implemented, giving further experience with the software design cycle.

## Installation

Standard Java procedures, recommend generating a .jar executable.

## Tests

Exclusively using the latest in hands-on debugging techniques (I spam click everything and see if it works).

## Updates

Version 0.6

Added
- Drawing system written from scratch. Previous implementation using JButtons was embarassing and I finally had some free time to mess around with new stuff. New implementation is significantly faster, but more benchmarking has to be done on better hardware than my laptop.
- Slider to change speed (tick rate)

Fixed
- Benchmarking and analysis indicated that memleak did not exist. Looking into further mem optimizations.

Other / Issues
- SOON TO ADD: variable simulation size
- Compare potential differences between ConwayGUI.SimulationTicker extending Thread vs. implementing Runnable.
