package controller;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import view.SudokuGridPane;

public class NumberFieldController<T> implements EventHandler<MouseEvent> {

	SudokuGridPane sudoku;
	int number;
	
	public NumberFieldController(SudokuGridPane sudoku, int number) {
		this.sudoku = sudoku;
		this.number = number;
	}

	@Override
	public void handle(MouseEvent event) {
		sudoku.inputNumber(number);
	}

}
