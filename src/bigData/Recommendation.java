package bigData;

import datatypes.Device;

public class Recommendation implements Comparable<Recommendation> {
	private double rank;
	private Device device;

	public Recommendation(double rank, Device d) {
		this.rank = rank;
		this.device = d;
	}

	public String toString() {
		return rank + " - " + device.toString();
	}

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

	public double getRank() {
		return rank;
	}

	public Device getDevice() {
		return this.device;
	}

}
