package datatypes;

import java.util.ArrayList;
import java.util.List;

public class Device {
	private int id;
	private List<Location> locations = new ArrayList<Location>();
	private boolean zubehoer;
	private String description;
	private String artNr;
	private Store store;
	private Customer customer;
	private Project project;
	private ProjectTyp projectTyp;
	private double price;
	private double priceFM;

	public int getId() {
		return id;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public boolean isMoving() {
		Location reference = null;
		int count = 0;

		for (Location l : locations) {
			if (reference == null) {
				reference = l;
			} else {
				if (reference.distance(l) > 0) {
					count++;
				}
			}

		}

		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof Device && ((Device) arg0).getArtNr().equals(artNr)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "[" + id + "] " + artNr + " - " + description + " " + zubehoer
				+ " " + price + "€/Einh. " + priceFM + "€/FM";
	}

	public Device(int id, String artNr, String bezeichnung, boolean zubehoer,
			int projectTypId, double price, double priceFM) {
		this.id = id;
		this.artNr = artNr;
		this.description = bezeichnung;
		this.zubehoer = zubehoer;
		this.price = price;
		this.priceFM = priceFM;
	}

	public boolean isZubehoer() {
		return zubehoer;
	}

	public void linkLocation(Location l) {
		if (locations.contains(l)) {
		} else {
			locations.add(l);
			l.linkDevice(this);
		}
	}

	public void linkCustomer(Customer c) {
		if (customer == null) {
			customer = c;
			customer.linkDevice(this);
		} else {

		}
	}

	public void linkStore(Store s) {
		if (store == null) {
			store = s;
			store.linkDevice(this);
		} else {

		}
	}

	public void linkProject(Project p) {
		if (project == null) {
			project = p;
			project.linkDevice(this);
		}
	}

	public String getBezeichnung() {
		return description;
	}

	public String getArtNr() {
		return artNr;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void linkProjectTyp(ProjectTyp projectTyp) {
		if (this.projectTyp == null) {
			this.projectTyp = projectTyp;
			this.projectTyp.linkDevice(this);
		} else {

		}
	}

	public String getScope() {
		return this.description;
	}

	public double getPrice() {
		return this.price;
	}

	public double getPriceFM() {
		return this.priceFM;
	}

}
