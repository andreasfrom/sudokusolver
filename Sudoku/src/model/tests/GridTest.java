package model.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import model.Grid;
import model.UserGrid;
import model.util.Pair;

public class GridTest {

	@Test
	public void exampleIsLegal() throws IOException {
		String input =
				"3\n" +
				".;1;.;3;.;.;8;.;." + "\n" +
				"5;.;9;6;.;.;7;.;." + "\n" +
				"7;.;4;.;9;5;.;2;." + "\n" +
				"4;.;.;.;.;.;1;.;." + "\n" +
				".;2;8;.;7;1;.;6;3" + "\n" +
				".;.;.;2;.;4;9;5;." + "\n" +
				"6;.;3;.;.;9;.;.;7" + "\n" +
				".;.;.;4;2;.;5;1;6" + "\n" +
				".;5;2;.;8;.;.;4;." ;
		
		Grid g = new Grid(input);
		
		assertTrue(g.isLegal());
	}
	
	@Test
	public void twoInRowIsIllegal() throws IOException {
		String input =
				"3\n" +
				".;1;.;3;.;.;8;.;." + "\n" +
				"5;.;9;6;.;.;7;.;." + "\n" +
				"7;.;4;.;9;5;.;2;." + "\n" +
				"4;.;.;.;1;.;1;.;." + "\n" +
				".;2;8;.;7;1;.;6;3" + "\n" +
				".;.;.;2;.;4;9;5;." + "\n" +
				"6;.;3;.;.;9;.;.;7" + "\n" +
				".;.;.;4;2;.;5;1;6" + "\n" +
				".;5;2;.;8;.;.;4;." ;
		
		Grid g = new Grid(input);
		
		assertFalse(g.isLegal());
	}
	
	@Test
	public void twoInColIsIllegal() throws IOException {
		String input =
				"3\n" +
				".;1;.;3;.;.;8;.;." + "\n" +
				"5;.;9;6;.;.;7;.;." + "\n" +
				"7;.;4;.;9;5;.;2;." + "\n" +
				"4;.;.;.;.;.;1;.;." + "\n" +
				"5;2;8;.;7;1;.;6;3" + "\n" +
				".;.;.;2;.;4;9;5;." + "\n" +
				"6;.;3;.;.;9;.;.;7" + "\n" +
				".;.;.;4;2;.;5;1;6" + "\n" +
				".;5;2;.;8;.;.;4;." ;
		
		Grid g = new Grid(input);
		
		assertFalse(g.isLegal());
	}
	
	@Test
	public void twoInBoxIsIllegal() throws IOException {
		String input =
				"3\n" +
				".;1;.;3;.;.;8;.;." + "\n" +
				"5;.;9;6;.;.;7;.;." + "\n" +
				"7;.;4;.;9;5;.;2;." + "\n" +
				"4;.;.;.;.;.;1;.;." + "\n" +
				".;2;8;.;7;1;.;6;3" + "\n" +
				".;.;.;2;.;4;9;5;." + "\n" +
				"6;.;3;.;.;9;4;.;7" + "\n" +
				".;.;.;4;2;.;5;1;6" + "\n" +
				".;5;2;.;8;.;.;4;." ;
		
		Grid g = new Grid(input);
		
		assertFalse(g.isLegal());
	}

}
