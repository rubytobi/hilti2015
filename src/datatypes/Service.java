package datatypes;

public class Service {
	private int id;
	private Customer customer;
	private Project project;
	private Device device;
	private String typ;
	private double price;

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof Service && ((Service) arg0).getId() == id) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "[" + id + "] " + typ + price + "€";
	}

	public Service(int id, double price, String typ) {
		this.id = id;
		this.price = price;
		this.typ = typ;
	}

	public void linkCustomer(Customer c) {
		if (customer == null) {
			customer = c;
			customer.linkService(this);
		} else {

		}
	}

	public void linkProject(Project p) {
		if (project == null) {
			project = p;
			project.linkService(this);
		}
	}

	public void linkDevice(Device d) {
		if (device == null) {
			device = d;
			device.linkService(this);
		}
	}

	public Customer getCustomer() {
		return customer;
	}

	public double getPrice() {
		return this.price;
	}

	public String getTyp() {
		return this.typ;
	}
}
