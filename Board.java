import java.util.Arrays;

public class Board {

	private final short[][] board; // board representation
	private final int N; // board Dimensions

	private int blankRow, blankCol; // position of the blank tile

	// 4 directions of moves
	private short[] dx = { 1, -1, 0, 0 };
	private short[] dy = { 0, 0, 1, -1 };

	private int cachedManhattan = -1;

	// construct a board from an N-by-N array of blocks
	// (where blocks[i][j] = block in row i, column j)
	public Board(int[][] blocks) {
		N = blocks.length;
		board = new short[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				this.board[i][j] = (short) blocks[i][j];
				if (blocks[i][j] == 0) {
					blankRow = i;
					blankCol = j;
				}
			}
		}
		cachedManhattan = manhattan();
	}

	private Board(short[][] blocks) {
		N = blocks.length;
		board = new short[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				this.board[i][j] = (short) blocks[i][j];
				if (blocks[i][j] == 0) {
					blankRow = i;
					blankCol = j;
				}
			}
		}
		cachedManhattan = manhattan();
	}

	// board dimension N
	public int dimension() {
		return N;
	}

	// number of blocks out of place
	public int hamming() {
		int expected = 1;

		int hamming = 0;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++)
				if (board[i][j] != expected++)
					hamming++;
		}

		return hamming - 1;
	}

	// sum of Manhattan distances between blocks and goal
	public int manhattan() {

		if (cachedManhattan != -1)
			return cachedManhattan;

		int gRow, gCol;
		int manhattan = 0;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				int tile = board[i][j];
				

				if (tile == 0)
					continue;

				gRow = (tile - 1) / N;
				gCol = (tile - 1) % N;

				manhattan += Math.abs(gRow - i) + Math.abs(gCol - j);
			}
		}
		return manhattan;
	}

	// is this board the goal board?
	public boolean isGoal() {

		int goal = 1;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (board[i][j] != goal)
					return false;
				goal++;
				if (goal == N * N)
					goal = 0;
			}
		}
		return true;
	}

	// a board obtained by exchanging two adjacent blocks in the same row
	public Board twin() {
		int row;
		if (blankRow == N - 1)
			row = 0;
		else
			row = N - 1;

		short[][] newBlocks = swap(board, row, 0, row, 1);
		Board twin = new Board(newBlocks);
		twin.cachedManhattan = cachedManhattan + 2;

		return new Board(newBlocks);
	}

	private short[][] swap(short[][] board, int fromRow, int fromCol,
			int toRow, int toCol) {
		short[][] newBlocks = new short[N][];
		for (int j = 0; j < N; j++) {
			newBlocks[j] = new short[N];
			System.arraycopy(board[j], 0, newBlocks[j], 0, N);
		}
		short temp = newBlocks[fromRow][fromCol];
		newBlocks[fromRow][fromCol] = newBlocks[toRow][toCol];
		newBlocks[toRow][toCol] = temp;
		return newBlocks;
	}

	// does this board equal y?
	public boolean equals(Object y) {
		if (y == this)
			return true;
		if (y == null)
			return false;
		if (y.getClass() != this.getClass())
			return false;

		Board that = (Board) y;

		int N = that.dimension();
		if (N != this.N)
			return false;

		for (int i = 0; i < N; i++) {
			if (!Arrays.equals(board[i], that.board[i]))
				return false;
		}
		return true;
	}

	private boolean valid(int i, int j) {
		return i < N && i >= 0 && j < N && j >= 0;
	}

	// all neighboring boards
	public Iterable<Board> neighbors() {
		Queue<Board> neighbors = new Queue<Board>();

		int row, col;

		// loop on 4 possible directions
		for (int i = 0; i < 4; i++) {

			row = blankRow + dy[i];
			col = blankCol + dx[i];
			if (valid(row, col)) {
				short[][] newBlocks = swap(board, row, col, blankRow, blankCol);
				Board newBoard = new Board(newBlocks);
				newBoard.cachedManhattan = cachedManhattan + 2;
				neighbors.enqueue(new Board(newBlocks));
			}
		}

		return neighbors;
	}

	// string representation of the board (in the output format specified below)
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(N + "\n");
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				s.append(String.format("%2d ", board[i][j]));
			}
			s.append("\n");
		}
		return s.toString();
	}

	// test driver
	public static void main(String[] args) {
		In in = new In(args[0]);

		int N = in.readInt();

		int[][] blocks = new int[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				blocks[i][j] = in.readInt();
			}
		}
		Board b = new Board(blocks);

		StdOut.println("Board :");
		StdOut.println(b);

		StdOut.println();

		StdOut.println("Twin of Board :");
		StdOut.println(b.twin());

		StdOut.println();

		StdOut.println("children of Board :");
		for (Board child : b.neighbors()) {
			StdOut.println(child);
		}

		StdOut.println();

		StdOut.println("is Board Goal = " + b.isGoal());

		StdOut.println();

		StdOut.println("Manhattan dist of Board = " + b.manhattan());

		StdOut.println();

		StdOut.println("Manhattan dist of Twin = " + b.twin().manhattan());
	}
}
