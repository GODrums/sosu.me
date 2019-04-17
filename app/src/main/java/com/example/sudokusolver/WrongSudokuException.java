package com.example.sudokusolver;

public class WrongSudokuException extends Exception {
    private String message;

    public WrongSudokuException(String message) {
        this.message=message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return getClass().getName()+": "+message;
    }
}
