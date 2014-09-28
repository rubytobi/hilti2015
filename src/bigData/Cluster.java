package bigData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import datatypes.Device;
import datatypes.Location;

public class Cluster implements Iterable<Clusterable> {
	private List<Clusterable> objects = new ArrayList<Clusterable>();
	private int id;

	public Cluster(int i, List<Clusterable> objects) {
		if (objects == null) {
			throw new IllegalArgumentException(
					"Kann kein Cluster von null erzeugen.");
		}

		if (objects.size() <= 0) {
			throw new IllegalArgumentException(
					"Kann kein Cluster mit 0 oder weniger Objekten erzeugen.");
		}

		this.id = i;
		this.objects.addAll(objects);
	}

	public String toString() {
		return "[id=" + id + "] " + objects.size() + " Entities";
	}

	public Location getCenter() {
		double longitude = 0;
		double latitude = 0;
		int anzahl = 0;

		Iterator<Clusterable> it = this.iterator();
		while (it.hasNext()) {
			Clusterable l = it.next();

			if (l instanceof Location) {
				longitude += ((Location) l).getLongitude();
				latitude += ((Location) l).getLatitude();

				anzahl++;
			}
		}

		longitude /= anzahl;
		latitude /= anzahl;

		return new Location(latitude, longitude);
	}

	public List<Device> getDevices() {
		List<Device> devices = new ArrayList<Device>();

		for (Clusterable c : objects) {
			Device d = ((Location) c).getDevice();

			if (!devices.contains(d)) {
				devices.add(d);
			}
		}

		return devices;
	}

	public int size() {
		return objects.size();
	}

	public Clusterable get(int i) {
		return objects.get(i);
	}

	public double distance(Cluster cluster) {
		double distance = 0;
		int anzahl = 0;

		for (Clusterable c1 : this) {
			for (Clusterable c2 : cluster) {
				if (c1 instanceof Location && c2 instanceof Location) {
					anzahl++;
					distance += ((Location) c1).distance(((Location) c2));
				}
			}
		}

		distance /= anzahl;

		return distance;
	}

	public static Cluster merge(Cluster c1, Cluster c2) {
		List<Clusterable> merge = new ArrayList<Clusterable>();

		for (Clusterable c : c1) {
			merge.add(c);
		}

		for (Clusterable c : c2) {
			merge.add(c);
		}

		return new Cluster(c1.getId(), merge);
	}

	public boolean isPossiblyCluster(Location l) {
		return false;
	}

	@Override
	public Iterator<Clusterable> iterator() {
		return this.objects.iterator();
	}

	public Integer getId() {
		return id;
	}
}
