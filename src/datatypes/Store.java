package datatypes;

import java.util.ArrayList;
import java.util.List;

public class Store {
	private Location location = null;
	private int id;
	public static ArrayList<Store> storeList = new ArrayList<Store>();
	private List<Device> devices = new ArrayList<Device>();

	public Store() {
	}

	public Location getLocation() {
		return location;
	}

	public String toString() {
		return "[" + id + "]" + location.toString();
	}

	public int getId() {
		return this.id;
	}

	public void linkDevice(Device d) {
		if (devices.contains(d)) {
		} else {
			devices.add(d);
			d.linkStore(this);
		}
	}

	public void linkLocation(Location l) {
		if (location == null) {
			location = l;
			location.linkStore(this);
		} else {
		}

	}

	public List<Device> getDevices() {
		return devices;
	}
}
