package datatypes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bigData.Cluster;
import bigData.Clusterable;

public class Project {
	private Location location = null;
	private List<Device> devices = new ArrayList<Device>();
	private List<Service> services = new ArrayList<Service>();
	private int id;
	private Customer customer;
	private ProjectTyp projectTyp;
	private String scope;
	private int countEmployees;
	private Date end;

	public Project() {
	}

	public Project(int id, String scope, int countEmployees, Date end) {
		this.id = id;
		this.scope = scope;
		this.countEmployees = countEmployees;
		this.end = end;
	}

	public boolean isOver() {
		if (end != null && end.before(new Date())) {
			return true;
		} else {
			return false;
		}
	}

	public Cluster toCluster() {
		List<Clusterable> list = new ArrayList<Clusterable>();

		for (Device d : devices) {
			for (Location l : d.getLocations()) {
				list.add(l);
			}
		}

		return new Cluster(-1, list);
	}

	public List<Device> getDevices() {
		return devices;
	}

	public Location getLocation() {
		return location;
	}

	public String toString() {
		return "[" + id + "] " + scope + " #" + countEmployees;
	}

	public boolean isLocated() {
		if (location == null) {
			return false;
		} else {
			return true;
		}
	}

	public void linkDevice(Device d) {
		if (devices.contains(d)) {

		} else {
			devices.add(d);
			d.linkProject(this);
		}
	}

	public void linkLocation(Location l) {
		if (location == null) {
			location = l;
			location.linkProject(this);
		} else {
		}
	}

	public int getId() {
		return this.id;
	}

	public void linkCustomer(Customer c) {
		if (customer == null) {
			customer = c;
			customer.linkProject(this);
		} else {
		}
	}

	public void linkProjectTyp(ProjectTyp pt) {
		if (projectTyp == null) {
			projectTyp = pt;
			projectTyp.linkProject(this);
		} else {
		}
	}

	public ProjectTyp getProjectTyp() {
		return this.projectTyp;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public Date getEnd() {
		return this.end;
	}

	public void linkService(Service service) {
		if (this.services.contains(service)) {

		} else {
			this.services.add(service);
			service.linkProject(this);
		}
	}

}
