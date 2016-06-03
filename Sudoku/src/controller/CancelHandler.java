package controller;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import view.View;

public class CancelHandler<T> implements EventHandler<MouseEvent> {
	
	View view;
	
	public CancelHandler(View view) {
		this.view = view;
	}

	@Override
	public void handle(MouseEvent e) {
		view.cancelSolveTask();
	}

}
