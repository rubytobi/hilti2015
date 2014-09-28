package datatypes;

import java.util.ArrayList;
import java.util.List;

public class ProjectTyp {
	private int id;
	private String description;
	private String scope;
	private int countEmployees;
	private List<Device> devices = new ArrayList<Device>();
	private List<Project> projects = new ArrayList<Project>();

	public ProjectTyp(int id, String description, String scope,
			int countEmployees) {
		this.id = id;
		this.description = description;
		this.scope = scope;
		this.countEmployees = countEmployees;
	}

	public List<Device> getDevices() {
		return devices;
	}

	public String getDescription() {
		return description;
	}

	public String toString() {
		return "[" + id + "] " + scope + " #" + countEmployees;
	}

	public void linkDevice(Device d) {
		if (devices.contains(d)) {

		} else {
			devices.add(d);
			d.linkProjectTyp(this);
		}
	}

	public int getId() {
		return this.id;
	}

	public void linkProject(Project project) {
		if (this.projects.contains(project)) {

		} else {
			this.projects.add(project);
			project.linkProjectTyp(this);
		}
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof ProjectTyp
				&& ((ProjectTyp) arg0).getDescription().equals(description)) {
			return true;
		} else {
			return false;
		}
	}
}