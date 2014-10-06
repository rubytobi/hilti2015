package bigData;

import datatypes.ProjectTyp;

public class Rank implements Comparable<Rank> {
	private double rank;
	private ProjectTyp pt;

	/*
	 * Anlegen einer Empfehlung mit prozentualer Wahrscheinlichkeit f�r ein Projekttyp
	 */
	public Rank(double rank, ProjectTyp pt) {
		this.rank = rank;
		this.pt = pt;
	}

	/*
	 * gibt den gerundeten Rank-Wert auf zwei Nachkommastellen zur�ck
	 */
	public double getFixedRank() {
		return Engine.round(rank * 100, 2);
	}

	public String toString() {
		return getFixedRank() + "% - " + pt.toString();
	}

	/*
	 * �berschreibt die interne Vergleichsmethode um von gro� nach klein anhand
	 * der Rank-Werte zu sortieren
	 */
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

	/*
	 * gibt den ungerundeten Rank-Wert zur�ck
	 */
	public double getRank() {
		return rank;
	}

	/*
	 * gibt den bewerteten Projekttyp zur�ck
	 */
	public ProjectTyp getProjectTyp() {
		return this.pt;
	}
}
