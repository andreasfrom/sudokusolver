package model.tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;
import model.SAT;
import model.Grid;

public class SATTest {

//	@Test
	public void solveExample() throws IOException {
		String input = "3\n" + ".;1;.;3;.;.;8;.;." + "\n" + "5;.;9;6;.;.;7;.;." + "\n" + "7;.;4;.;9;5;.;2;." + "\n"
				+ "4;.;.;.;.;.;1;.;." + "\n" + ".;2;8;.;7;1;.;6;3" + "\n" + ".;.;.;2;.;4;9;5;." + "\n"
				+ "6;.;3;.;.;9;.;.;7" + "\n" + ".;.;.;4;2;.;5;1;6" + "\n" + ".;5;2;.;8;.;.;4;.";

		Grid grid = new Grid(input);

		Grid solved = SAT.solveWithZ3(grid);

		assertTrue(solved != null);
		assertTrue(solved.isSolved());
	}

	@Test
	public void solveTetradoku1() throws IOException {
		Grid g = new Grid(new FileReader("puzzles/tetradoku1.txt"));

		Grid solved = SAT.solveWithZ3(g);

		assertTrue(solved != null);
		assertTrue(solved.isSolved());
	}
	
//	@Test
	public void solveTop95() throws IOException {
		FileReader f = new FileReader("puzzles/top95.txt");
		BufferedReader b = new BufferedReader(f);
		
		Grid[] grids = new Grid[95];
		
		int n = 0;
		
		while (b.ready()) {
			String line = b.readLine();
			
			StringBuilder sb = new StringBuilder();
			sb.append("3\n");
			
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 8; j++) {
					sb.append(line.charAt(i*9 + j));
					sb.append(";");
				}
				sb.append(line.charAt(i*9+8));
				
				sb.append('\n');
			}

			grids[n] = new Grid(sb.toString());
			n++;
		}

		b.close();
		
		for (int i = 0; i < grids.length; i++) {
			Grid solved = SAT.solveWithZ3(grids[i]);
			assertTrue(solved != null);
			assertTrue(solved.isSolved());
		}
	}

}