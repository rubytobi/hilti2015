package bigData;

import datatypes.Device;

public class Recommendation implements Comparable<Recommendation> {
	private double rank;
	private Device device;

	/*
	 * Anlegen einer Empfehlung mit prozentualer Wahrscheinlichkeit für ein
	 * Gerät
	 */
	public Recommendation(double rank, Device d) {
		this.rank = rank;
		this.device = d;
	}

	public String toString() {
		return rank + " - " + device.toString();
	}

	/*
	 * Überschreibt die interne Vergleichsmethode um von groß nach klein anhand
	 * der Rank-Werte zu sortieren
	 */
	@Override
	public int compareTo(Recommendation r) {
		if (rank < r.getRank()) {
			return 1;
		} else if (rank > r.getRank()) {
			return -1;
		} else {
			return 0;
		}
	}

	/*
	 * gibt den ungerundeten Rank-Wert zurück
	 */
	public double getRank() {
		return rank;
	}

	/*
	 * gibt das bewerteten Gerät zurück
	 */
	public Device getDevice() {
		return this.device;
	}

}
