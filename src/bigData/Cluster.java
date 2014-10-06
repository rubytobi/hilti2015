package bigData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import datatypes.Device;
import datatypes.Location;

public class Cluster implements Iterable<Clusterable> {
	private List<Clusterable> objects = new ArrayList<Clusterable>();
	private int id;

	/*
	 * Der Konstruktor eines Clusters, mind. ein Objekt muss in der übergebenen
	 * Liste sein, die ID beschreibt das Cluster eindeutig
	 */
	public Cluster(int id, List<Clusterable> objects) {
		if (objects == null) {
			throw new IllegalArgumentException(
					"Kann kein Cluster von null erzeugen.");
		}

		if (objects.size() <= 0) {
			throw new IllegalArgumentException(
					"Kann kein Cluster mit 0 oder weniger Objekten erzeugen.");
		}

		this.id = id;
		this.objects.addAll(objects);
	}

	public String toString() {
		return "[id=" + id + "] " + objects.size() + " Entities";
	}

	/*
	 * Im Falle eines Cluster mit Locationdaten, wird der Zentroid berechnet und
	 * zurückgegeben
	 */
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

		/*
		 * Prüfen auf  teilen durch 0
		 */
		if (anzahl != 0) {
			longitude /= anzahl;
			latitude /= anzahl;
		}

		return new Location(latitude, longitude);
	}

	/*
	 * gibt sämtliche Geräte aus dem Cluster zurück, doppelte Geräte werden nu
	 * einmal aufgelistet
	 */
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

	/*
	 * Clustergröße
	 */
	public int size() {
		return objects.size();
	}

	/*
	 * gibt ein bestimmtes Element aus dem Cluster zurück
	 */
	public Clusterable get(int i) {
		return objects.get(i);
	}

	/*
	 * Berechnung der Distanz zwischen diesem und dem übergebenen Cluster
	 */
	public double distance(Cluster cluster) {
		double distance = 0;
		int anzahl = 0;

		for (Clusterable c1 : this) {
			for (Clusterable c2 : cluster) {
				/*
				 * Nur bei Locations ist eine Berechnung bisher möglich
				 */
				if (c1 instanceof Location && c2 instanceof Location) {
					/*
					 * mitzählen der berechneten Distanzen
					 */
					anzahl++;
					
					/*
					 * Distanzen aufsummieren
					 */
					distance += ((Location) c1).distance(((Location) c2));
				}
			}
		}

		/*
		 * Prüfen auf  teilen durch 0
		 */
		if (anzahl != 0) {
			distance /= anzahl;
		}
		
		return distance;
	}

	/*
	 * Zwei CLuster werden zusammen geführt, die ID des ersten Clusters wird beibehalten
	 */
	public static Cluster merge(Cluster c1, Cluster c2) {
		List<Clusterable> merge = new ArrayList<Clusterable>();

		/*
		 * Sammeln aller Objekte des ersten Clusters
		 */
		for (Clusterable c : c1) {
			merge.add(c);
		}

		/*
		 * Sammeln aller Objekte des zweiten Clusters
		 */
		for (Clusterable c : c2) {
			merge.add(c);
		}

		/*
		 * Cluster neu erzeugen mit der ID des ersten Clusters
		 */
		return new Cluster(c1.getId(), merge);
	}

	/*
	 * gibt den internen Iterator weiter
	 */
	@Override
	public Iterator<Clusterable> iterator() {
		return this.objects.iterator();
	}

	/*
	 * gibt die Cluster ID zurück
	 */
	public Integer getId() {
		return id;
	}
}
