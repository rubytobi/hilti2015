package bigData;

import datatypes.ProjectTyp;

public class Rank implements Comparable<Rank> {
	private double rank;
	private ProjectTyp pt;

	public Rank(double rank, ProjectTyp pt) {
		this.rank = rank;
		this.pt = pt;
	}

	private double round(double d, int i) {
		return Math.round(Math.pow(10, i) * d) / Math.pow(10, i);
	}

	public double getFixedRank() {
		return round(rank * 100, 2);
	}

	public String toString() {
		return getFixedRank() + "% - " + pt.toString();
	}

	@Override
	public int compareTo(Rank r) {
		if (rank < r.getRank()) {
			return 1;
		} else if (rank > r.getRank()) {
			return -1;
		} else {
			return 0;
		}
	}

	public double getRank() {
		return rank;
	}

	public ProjectTyp getProjectTyp() {
		return this.pt;
	}
}
