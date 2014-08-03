public class Solver {

	private SearchNode result;

	private class SearchNode implements Comparable<SearchNode> {

		private final Board board;
		private final int moves;
		private final SearchNode prev;
		private final int priority;

		private SearchNode(Board board, SearchNode prev) {
			this.board = board;
			this.prev = prev;

			if (prev == null)
				moves = 0;
			else
				moves = prev.moves + 1;

			priority = board.manhattan() + moves;
		}

		@Override
		public int compareTo(SearchNode that) {
			return this.priority - that.priority;
		}

	}

	// find a solution to the initial board (using the A* algorithm)
	public Solver(Board initial) {
		if (initial.isGoal())
			result = new SearchNode(initial, null);
		else
			result = solve(initial, initial.twin());
	}

	private SearchNode step(MinPQ<SearchNode> pq) {
		SearchNode min = pq.delMin();

		for (Board child : min.board.neighbors()) {
			if (min.prev == null || !child.equals(min.prev.board))
				pq.insert(new SearchNode(child, min));
		}
		return min;
	}

	private SearchNode solve(Board initial, Board twin) {
		SearchNode current;
		MinPQ<SearchNode> mainPQ = new MinPQ<SearchNode>();
		MinPQ<SearchNode> twinPQ = new MinPQ<SearchNode>();

		mainPQ.insert(new SearchNode(initial, null));
		twinPQ.insert(new SearchNode(twin, null));

		while (true) {
			current = step(mainPQ);
			if (current.board.isGoal())
				return current;
			if (step(twinPQ).board.isGoal())
				return null;
		}
	}

	// is the initial board solvable?
	public boolean isSolvable() {
		return result != null;
	}

	// min number of moves to solve initial board; -1 if no solution
	public int moves() {
		if (result != null)
			return result.moves;
		return -1;
	}

	// sequence of boards in a shortest solution; null if no solution
	public Iterable<Board> solution() {
		if (result == null)
			return null;

		Stack<Board> stack = new Stack<Board>();

		for (SearchNode s = result; s != null; s = s.prev)
			stack.push(s.board);

		return stack;
	}

	// solve a slider puzzle (given below)
	public static void main(String[] args) {
		// create initial board from file
		In in = new In(args[0]);
		int N = in.readInt();
		int[][] blocks = new int[N][N];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				blocks[i][j] = in.readInt();
		Board initial = new Board(blocks);

		// solve the puzzle
		Solver solver = new Solver(initial);

		// print solution to standard output
		if (!solver.isSolvable())
			StdOut.println("No solution possible");
		else {
			StdOut.println("Minimum number of moves = " + solver.moves());
			for (Board board : solver.solution())
				StdOut.println(board);
		}
	}
}
