package bigData;

public interface Clusterable {
	/*
	 * Clusterable Objekt müssen eine Distanz berechnen können
	 */
	public double distance(Clusterable c);
}
