package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SATSolver extends Solver {
	
	public SATSolver(Grid g) {
		super(g);
	}

	public Process process;
	
	public Grid solve() {
		try {
			return solveHelper();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void cancel() {
		super.cancel();
		
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				while (process == null) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				process.destroyForcibly();
				process = null;
			}
			
		});
		
		t.setDaemon(true);
		t.start();
	}

	public Grid solveHelper() throws IOException {
		File tmp = File.createTempFile("sudoku", "smt2");
		tmp.deleteOnExit();

		FileWriter f = new FileWriter(tmp);

		generateRules(grid.k(), f);
		generateGiven(f);
		f.write("(check-sat)\n");
		f.write("(get-model)");

		f.close();

		ProcessBuilder pb = new ProcessBuilder("z3", tmp.getAbsolutePath());
		process = pb.start();

		InputStream in = process.getInputStream();
		BufferedReader b = new BufferedReader(new InputStreamReader(in));

		String out = b.lines().collect(Collectors.joining("\n"));

		return parseModel(grid.k(), out);
	}

	public void generateRules(final int k, Writer w) throws IOException {
		declareConstants(k, w);
		if (!run) return;

		// Necessary constraints
		atLeastOneInEachField(k, w);
		if (!run) return;
		onceInRows(k, w);
		if (!run) return;
		onceInCols(k, w);
		if (!run) return;
		onceInBoxes(k, w);
		if (!run) return;

		// Redundant but helpful constraints
		atMostOneInEach(k, w);
		if (!run) return;
		atLeastOnceInEachRow(k, w);
		if (!run) return;
		atLeastOnceInEachCol(k, w);
		// atLeastOnceInEachBox is not helpful
	}

	public void generateGiven(Writer w) throws IOException {
		for (int x = 0; x < grid.size(); x++) {
			for (int y = 0; y < grid.size(); y++) {
				int i = grid.get(x, y);

				if (i != 0) {
					w.write("(assert " + makeConstant(x + 1, y + 1, i) + ")\n");
				}
			}
		}
	}

	public Grid parseModel(final int k, String s) {
		/*
		 * INPUT: sat (model (define-fun s6_2_7 () Bool true) (define-fun s4_5_3
		 * () Bool true) ...)
		 */

		s = s.trim();

		final String UNSAT = "unsat";
		final String SAT = "sat";
		final String MODEL = "(model";
		
		if (s.startsWith(UNSAT)) {
			return null;
		}

		if (s.startsWith(SAT)) {
			s = s.substring(SAT.length());
			s = s.trim();
		}

		if (s.startsWith(MODEL)) {
			s = s.substring(MODEL.length());
			s = s.substring(0, s.length() - 1);
			s = s.trim();
		} else {
			System.out.println(s.substring(0, Math.min(s.length(), 100)));
			throw new IllegalArgumentException("Given string is not a model.");
		}

		Pattern p = Pattern
				.compile("\\s*\\(define-fun\\s+s(?<row>\\d+)_(?<col>\\d+)_(?<value>\\d+)\\s+\\(\\)\\s+Bool\\s+true\\)");
		Matcher m = p.matcher(s);

		Grid grid = new Grid(k);

		while (m.find()) {
			int row = Integer.parseInt(m.group("row"));
			int col = Integer.parseInt(m.group("col"));
			int val = Integer.parseInt(m.group("value"));

			grid.set(row - 1, col - 1, val);
		}

		return grid;
	}

	private void startAssert(Writer w) throws IOException {
		w.write("(assert\n");
	}

	private void endAssert(Writer w) throws IOException {
		w.write(")\n\n");
	}

	private String makeConstant(int row, int col, int val) {
		return "s" + row + "_" + col + "_" + val;
	}

	private void declareConstants(final int k, Writer w) throws IOException {
		final int n = k * k;

		for (int x = 1; x <= n; x++) {
			for (int y = 1; y <= n; y++) {
				for (int i = 1; i <= n; i++) {
					w.write("(declare-const " + makeConstant(x, y, i) + " Bool)\n");
				}
			}
		}

		w.write("\n");
	}

	private void atLeastOneInEachField(final int k, Writer w) throws IOException {
		final int n = k * k;

		startAssert(w);

		w.write("(and\n  ");
		for (int x = 1; x <= n; x++) {
			for (int y = 1; y <= n; y++) {
				w.write("(or ");
				for (int z = 1; z <= n; z++) {
					w.write(makeConstant(x, y, z) + " ");
				}
				w.write(")\n  ");
			}
		}
		w.write(")\n");

		endAssert(w);
	}

	private void onceInRows(final int k, Writer w) throws IOException {
		final int n = k * k;

		startAssert(w);

		w.write("(and\n  ");

		for (int y = 1; y <= n; y++) {
			for (int z = 1; z <= n; z++) {
				for (int x = 1; x <= n - 1; x++) {
					for (int i = x + 1; i <= n; i++) {
						w.write("(or (not " + makeConstant(x, y, z) + ") (not " + makeConstant(i, y, z) + "))\n  ");
					}
				}
			}
		}
		w.write(")\n");

		endAssert(w);
	}

	private void onceInCols(final int k, Writer w) throws IOException {
		final int n = k * k;

		startAssert(w);

		w.write("(and\n  ");

		for (int x = 1; x <= n; x++) {
			for (int z = 1; z <= n; z++) {
				for (int y = 1; y <= n - 1; y++) {
					for (int i = y + 1; i <= n; i++) {
						w.write("(or (not " + makeConstant(x, y, z) + ") (not " + makeConstant(x, i, z) + "))\n  ");
					}
				}
			}
		}
		w.write(")\n");

		endAssert(w);
	}

	private void onceInBoxes(final int k, Writer w) throws IOException {
		onceInBoxes1(k, w);
		onceInBoxes2(k, w);
	}

	private void onceInBoxes1(final int k, Writer w) throws IOException {
		final int n = k * k;

		startAssert(w);

		w.write("(and\n  ");
		for (int z = 1; z <= n; z++) {
			for (int i = 0; i < k; i++) {
				for (int j = 0; j < k; j++) {
					for (int x = 1; x <= k; x++) {
						for (int y = 1; y < k; y++) {
							for (int m = y + 1; m <= k; m++) {
								String a = makeConstant(k * i + x, k * j + y, z);
								String b = makeConstant(k * i + x, k * j + m, z);
								w.write("(or (not " + a + ") (not " + b + "))\n  ");
							}
						}
					}
				}
			}
		}
		w.write(")\n");

		endAssert(w);
	}

	private void onceInBoxes2(final int k, Writer w) throws IOException {
		final int n = k * k;

		startAssert(w);

		w.write("(and\n  ");
		for (int z = 1; z <= n; z++) {
			for (int i = 0; i < k; i++) {
				for (int j = 0; j < k; j++) {
					for (int x = 1; x < k; x++) {
						for (int y = 1; y <= k; y++) {
							for (int m = x + 1; m <= k; m++) {
								for (int l = 1; l <= k; l++) {
									String a = makeConstant(k * i + x, k * j + y, z);
									String b = makeConstant(k * i + m, k * j + l, z);
									w.write("(or (not " + a + ") (not " + b + "))\n  ");
								}
							}
						}
					}
				}
			}
		}
		w.write(")\n");

		endAssert(w);
	}

	private void atMostOneInEach(final int k, Writer w) throws IOException {
		final int n = k * k;

		startAssert(w);

		w.write("(and\n  ");
		for (int x = 1; x <= n; x++) {
			for (int y = 1; y <= n; y++) {
				for (int z = 1; z <= n - 1; z++) {
					for (int i = z + 1; i <= n; i++) {
						w.write("(or (not " + makeConstant(x, y, z) + ") (not " + makeConstant(x, y, i) + "))\n  ");
					}
				}
			}
		}
		w.write(")\n");

		endAssert(w);
	}

	private void atLeastOnceInEachRow(final int k, Writer w) throws IOException {
		final int n = k * k;

		startAssert(w);

		w.write("(and\n  ");
		for (int y = 1; y <= n; y++) {
			for (int z = 1; z <= n; z++) {
				w.write("(or ");
				for (int x = 1; x <= n; x++) {
					w.write(makeConstant(x, y, z) + " ");
				}
				w.write(")\n  ");
			}
		}
		w.write(")\n");

		endAssert(w);
	}

	private void atLeastOnceInEachCol(final int k, Writer w) throws IOException {
		final int n = k * k;

		startAssert(w);

		w.write("(and\n  ");
		for (int x = 1; x <= n; x++) {
			for (int z = 1; z <= n; z++) {
				w.write("(or ");
				for (int y = 1; y <= n; y++) {
					w.write(makeConstant(x, y, z) + " ");
				}
				w.write(")\n  ");
			}
		}
		w.write(")\n");

		endAssert(w);
	}

}