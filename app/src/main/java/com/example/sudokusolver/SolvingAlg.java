package com.example.sudokusolver;

import android.util.Log;

import java.util.stream.IntStream;

public class SolvingAlg {

    // dimension of input
    private static int N = 9;
    private static int[][] arr;

    public static int[][] completeSudoku(int[][] arr) {
        SolvingAlg.arr = arr;
        solve(new Cell(0, 0));
        return arr;
    }

    public static void testOnErrors(int[][] arr) throws WrongSudokuException {
        for (int k=0;k<9;k++)
            for (int i = 0; i < 9; i++) {
                if(arr[k][i]==0) continue;
                for (int j = i + 1; j < 9; j++)
                    if (arr[k][i] == arr[k][j])
                        throw new WrongSudokuException("Invalid row found in the Sudoku");
            }
        for (int k=0;k<9;k++)
            for (int i = 0; i < 9; i++) {
                if(arr[i][k]==0) continue;
                for (int j = i + 1; j < 9; j++)
                    if (arr[i][k] == arr[j][k])
                        throw new WrongSudokuException("Invalid column found in the Sudoku");
            }
        //fields y axis
        for (int y=0;y<3;y++)
            //fields x axis
            for (int x=0;x<3;x++)
                //checking the field
                for (int i=0;i<9;i++) {
                    if (arr[y*3+i/3][x*3+i%3]==0) continue;
                    for (int j=i+1;j<9;j++)
                        if(arr[y*3+j/3][x*3+j%3]==arr[y*3+i/3][x*3+i%3])
                            throw new WrongSudokuException("Invalid field found in the Sudoku");
                }

    }

    /**
     * Class to abstract the representation of a cell. Cell => (x, y)
     */
    private static class Cell {

        int row, col;

        public Cell(int row, int col) {
            super();
            this.row = row;
            this.col = col;
        }

        @Override
        public String toString() {
            return "Cell [row=" + row + ", col=" + col + "]";
        }
    };

    /**
     * Utility function to check whether @param value is valid for @param cell
     */
    private static boolean isValid(Cell cell, int value) {

        if (arr[cell.row][cell.col] != 0) {
            throw new RuntimeException(
                    "Cannot call for cell which already has a value");
        }

        // if v present row, return false
        for (int c = 0; c < 9; c++) {
            if (arr[cell.row][c] == value)
                return false;
        }

        // if v present in col, return false
        for (int r = 0; r < 9; r++) {
            if (arr[r][cell.col] == value)
                return false;
        }

        // if v present in grid, return false

        // to get the grid we should calculate (x1,y1) (x2,y2)
        int x1 = 3 * (cell.row / 3);
        int y1 = 3 * (cell.col / 3);
        int x2 = x1 + 2;
        int y2 = y1 + 2;

        for (int x = x1; x <= x2; x++)
            for (int y = y1; y <= y2; y++)
                if (arr[x][y] == value)
                    return false;

        // if value not present in row, col and bounding box, return true
        return true;
    }

    /**
     * simple function to get the next cell
    * read for yourself, very simple and straight forward
     */
    private static Cell getNextCell(Cell cur) {

        int row = cur.row;
        int col = cur.col;

        // next cell => col++
        col++;

        // if col > 8, then col = 0, row++
        // reached end of row, got to next row
        if (col > 8) {
            // goto next line
            col = 0;
            row++;
        }

        // reached end of matrix, return null
        if (row > 8)
            return null; // reached end

        Cell next = new Cell(row, col);
        return next;
    }

    /**
     * everything is put together here
     * very simple solution
     * must return true, if the soduku is solved, return false otherwise
     */
    private static boolean solve(Cell cur) {

        // if the cell is null, we have reached the end
        if (cur == null)
            return true;

        // if grid[cur] already has a value, there is nothing to solve here,
        // continue on to next cell
        if (arr[cur.row][cur.col] != 0) {
            // return whatever is being returned by solve(next)
            // i.e the state of soduku's solution is not being determined by
            // this cell, but by other cells
            return solve(getNextCell(cur));
        }

        // this is where each possible value is being assigned to the cell, and
        // checked if a solutions could be arrived at.

        // if grid[cur] doesn't have a value
        // try each possible value
        for (int i = 1; i <= 9; i++) {
            // check if valid, if valid, then update
            boolean valid = isValid(cur, i);

            if (!valid)
                continue;
                // i not valid for this cell, try other values

            // assign here
            arr[cur.row][cur.col] = i;

            // continue with next cell
            boolean solved = solve(getNextCell(cur));
            // if solved, return, else try other values
            if (solved)
                return true;
            else
                arr[cur.row][cur.col] = 0; // reset
            // continue with other possible values
        }

        // if you reach here, then no value from 1 - 9 for this cell can solve
        // return false
        return false;
    }
}
