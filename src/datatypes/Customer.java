package datatypes;

import java.util.ArrayList;
import java.util.List;

public class Customer {

	private int id;
	private int anzMitarbeiter;
	private boolean flottenmgmt;
	private Location location;
	private List<Device> devices = new ArrayList<Device>();
	private List<Project> projects = new ArrayList<Project>();
	private String name;

	public Customer(int id, int anzMitarbeiter, boolean flottenmgmt,
			Location location, String name) {
		this.id = id;
		this.anzMitarbeiter = anzMitarbeiter;
		this.flottenmgmt = flottenmgmt;
		this.location = location;
		this.name = name;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof Customer && ((Customer) arg0).getId() == id) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "[" + id + "] " + name + " #" + anzMitarbeiter + " "
				+ flottenmgmt;
	}

	public int getId() {
		return id;
	}

	public void link(Device d) {
		if (devices.contains(d)) {
		} else {
			devices.add(d);
			d.linkCustomer(this);
		}
	}

	public void linkDevice(Device d) {
		if (devices.contains(d)) {
		} else {
			devices.add(d);
			d.linkCustomer(this);
		}
	}

	public Location getLocation() {
		return location;
	}

	public void linkProject(Project project) {
		if (this.projects.contains(project)) {

		} else {
			this.projects.add(project);
			project.linkCustomer(this);
		}
	}

	public int getCountEmployees() {
		return this.anzMitarbeiter;
	}

	public List<Project> getProjects() {
		return this.projects;
	}

}
