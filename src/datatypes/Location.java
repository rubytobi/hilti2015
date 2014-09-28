package datatypes;

import bigData.Clusterable;

public class Location implements Clusterable {
	private double latitude;
	private double longitude;
	private Device device;
	private Project project;
	private Store store;

	/*
	 * Radius of the earth in km
	 */
	public static final double EARTH_RADIUS = 6371;

	public static double parseLongitude(String s) {
		return Double.parseDouble(s.split(",")[1]);
	}

	public static double parseLatitude(String s) {
		return Double.parseDouble(s.split(",")[0]);
	}

	public static Location parseLocation(double latitude, double longitude) {
		return new Location(latitude, longitude);
	}

	public boolean equals() {
		return false;
	}

	public String toString() {
		return "Lat.: " + Math.round(latitude * 1000.0F) / 1000.0F + "\u00B0N "
				+ Math.round(longitude * 1000.0F) / 1000.0F + " \u00B0E";
	}

	public Location(Double latitude, Double longitude) {
		if (latitude == null || longitude == null) {
			throw new IllegalArgumentException(
					"Koordinate kann nicht null sein.");
		}

		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double deg2rad(double deg) {
		return deg * (Math.PI / 180);
	}

	/*
	 * Euklidische Distanz
	 */
	public double distance(Location l) {
		double dLat = deg2rad(l.getLatitude() - getLatitude());
		double dLon = deg2rad(l.getLongitude() - getLongitude());

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(deg2rad(l.getLatitude()))
				* Math.cos(deg2rad(getLatitude())) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		double d = EARTH_RADIUS * c;

		return d;
	}

	public void linkDevice(Device d) {
		if (device == null) {
			device = d;
			device.linkLocation(this);
		} else {

		}
	}

	public void linkProject(Project p) {
		if (project == null) {
			project = p;
			project.linkLocation(this);
		} else {
		}
	}

	public void linkStore(Store s) {
		if (store == null) {
			store = s;
			store.linkLocation(this);
		} else {
		}
	}

	public Device getDevice() {
		return device;
	}

	public static Location parseLocation(String s) {
		return new Location(Location.parseLongitude(s),
				Location.parseLatitude(s));
	}

	@Override
	public double distance(Clusterable c) {
		if (c instanceof Location) {
			return distance(c);
		} else {
			return 0;
		}
	}
}
