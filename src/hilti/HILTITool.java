package hilti;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import datatypes.Customer;
import datatypes.Device;
import datatypes.ProjectTyp;
import datatypes.Service;
import datatypes.Store;
import UI.UIBackend;
import UI.UILogin;
import UI.UIProjectRecognizedWorker;
import UI.UIProjectViewWorker;
import bigData.Cluster;
import bigData.Engine;
import datatypes.Location;
import datatypes.Project;

public class HILTITool {
	public static List<Customer> customers = new ArrayList<Customer>();
	public static List<Location> locations = new ArrayList<Location>();
	public static List<Device> devices = new ArrayList<Device>();
	public static List<Project> projects = new ArrayList<Project>();
	public static List<Cluster> clusters = new ArrayList<Cluster>();
	public static List<Service> services = new ArrayList<Service>();
	public static List<Store> stores = new ArrayList<Store>();
	public static List<ProjectTyp> projecttypes = new ArrayList<ProjectTyp>();
	public static UIBackend uiframe = null;
	public static UILogin uilogin = null;
	public static UIProjectRecognizedWorker uiprojectrecognized = null;
	public static UIProjectViewWorker uiprojectview = null;
	public static final String PATH_TO_PROJECT_FOLDER = System
			.getProperty("user.dir") + "/";

	public static void main(String[] args) {
		System.out.println("Working Directory = " + PATH_TO_PROJECT_FOLDER);

		new HILTITool();
		new UIBackend();
		new UILogin();
	}

	public HILTITool() {
		/*
		 * Laden der Daten aus der SQLite Datenbank
		 */
		loadDB();

		System.out.println("\n## Cluster the locations");
		List<Cluster> clusters = Engine.clusterLocations(locations);

		System.out
				.println("\n## Try to recognise projects and stores on moving data by devices");
		Engine.recogniseLocations(clusters, projects);
	}

	/*
	 * Finde einen Kunden anhand der ID
	 */
	public static Customer findCustomer(int id) {
		for (Customer c : customers) {
			if (c.getId() == id) {
				return c;
			}
		}

		return null;
	}

	/*
	 * Finde ein Gerät anhand der ID
	 */
	public static Device findDevice(int id) {
		return findDevice(id, devices);
	}

	/*
	 * Finde einen Kunden anhand der ID in dieser Liste
	 */
	public static Device findDevice(int id, List<Device> devices) {
		for (Device d : devices) {
			if (d.getId() == id) {
				return d;
			}
		}

		return null;
	}

	/*
	 * Finde ein Projekt anhand der ID
	 */
	public static Project findProject(int id) {
		return findProject(id, projects);
	}

	/*
	 * Finde ein Projekt anhand der ID in dieser Liste
	 */
	public static Project findProject(int id, List<Project> projects) {
		for (Project p : projects) {
			if (p.getId() == id) {
				return p;
			}
		}

		return null;
	}

	/*
	 * Finde einen Projekttyp anhand der ID
	 */
	public static ProjectTyp findProjectTyp(int id) {
		return findProjectTyp(id, projecttypes);
	}

	/*
	 * Finde einen PRoejttypen anhand der ID in dieser Liste
	 */
	public static ProjectTyp findProjectTyp(int id,
			List<ProjectTyp> projecttypes) {
		for (ProjectTyp pt : projecttypes) {
			if (pt.getId() == id) {
				return pt;
			}
		}

		return null;
	}

	/*
	 * Laden der SQLite-Datenbank
	 */
	private void loadDB() {
		Connection con = null;
		try {
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:psseDB.sqlite");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		System.out.println("Opened database successfully");

		try {
			// Projekttypen
			loadProjectTyps(con);

			// Kunden
			loadCustomers(con);

			// Projekt, verknüpfen mit ProjektTyp
			loadProjects(con);

			// Gerät, verknüpfen mit Kunde, Projekt, ProjektTyp und Store
			loadDevices(con);

			// Positionsdaten
			loadLocations(con);

			// Servicedaten
			loadServices(con);

			// MAp füllen
			loadMap(con);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		System.out.println("Database fully loaded and linked.");
	}

	/*
	 * Laden der GPS-Daten und JavaScript-Datei generieren
	 */
	private void loadMap(Connection con) throws SQLException {
		// Locationpaare abfragen für Devices on map
		Statement stmt = con.createStatement();
		String sql = "SELECT pos1.Latitude, pos1.Longitude,pos2.Latitude,pos2.Longitude FROM (SELECT p1.Latitude, p1.Longitude, pg1.GeraetID  FROM Position p1, Position_Geraet pg1 WHERE p1.ID = pg1.PositionID AND pg1.Datum = '2014-09-18 10:00:00') AS pos1 INNER JOIN  (SELECT p1.Latitude, p1.Longitude, pg1.GeraetID  FROM Position p1, Position_Geraet pg1 WHERE p1.ID = pg1.PositionID AND pg1.Datum = '2014-09-18 16:00:00') AS pos2 ON pos1.GeraetID = pos2.GeraetID";
		ResultSet rs = stmt.executeQuery(sql);

		try {
			UIBackend.writeToolsToFile(rs);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Laden der vordefinierten Projekttypen
	 */
	private void loadProjectTyps(Connection con) throws SQLException {
		Statement stmt = con.createStatement();
		String sql = "SELECT ID, Bezeichnung, AnzPersonen"
				+ " FROM ProjektTyp p";
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {
			int projectTypId = rs.getInt("ID");
			String description = rs.getString("Bezeichnung");
			int countEmployees = rs.getInt("AnzPersonen");

			ProjectTyp p = new ProjectTyp(projectTypId, description,
					description, countEmployees);

			projecttypes.add(p);
		}
	}

	/*
	 * Laden der Projekte und verknüpfen mit dem Kunden, den GPS-Daten und dem
	 * Projekttyp
	 */
	private void loadProjects(Connection con) throws SQLException {
		Statement stmt = con.createStatement();
		String sql = "SELECT Latitude, Longitude, pr.ID, KundeID, ProjektTypID, Anwendungsbereich, PositionID, AnzPersonen, EndDatum"
				+ " FROM Projekt pr, Position po"
				+ " WHERE pr.PositionID = po.ID";
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {
			double latitude = rs.getDouble("Latitude");
			double longitude = rs.getDouble("Longitude");
			int customerID = rs.getInt("KundeID");
			int projectTypID = rs.getInt("ProjektTypID");
			int ID = rs.getInt("ID");
			String scope = rs.getString("Anwendungsbereich");
			int anzPersonen = rs.getInt("AnzPersonen");
			String endString = rs.getString("EndDatum");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd H:m:s");
			Date end = new Date();
			try {
				end = sdf.parse(endString);
			} catch (ParseException e) {
				end = null;
			}
			Project p = new Project(ID, scope, anzPersonen, end);

			Customer c = findCustomer(customerID);
			p.linkCustomer(c);

			Location l = new Location(latitude, longitude);
			p.linkLocation(l);

			ProjectTyp pt = findProjectTyp(projectTypID);
			p.linkProjectTyp(pt);

			projects.add(p);
		}
	}

	/*
	 * Laden der GPS-Daten und verknüpfen mit den Geräten
	 */
	private void loadLocations(Connection con) throws SQLException {
		Statement stmt = con.createStatement();
		String sql = "SELECT p.Latitude, p.Longitude, p.ID, g.ID AS GeraetID"
				+ " FROM Position p, Geraet g, Position_Geraet pg"
				+ " WHERE p.ID = pg.PositionID AND pg.GeraetID = g.ID";
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {
			double latitude = rs.getDouble("Latitude");
			double longitude = rs.getDouble("Longitude");
			int geraetID = rs.getInt("GeraetID");

			Location location = new Location(latitude, longitude);

			Device d = findDevice(geraetID);
			if (d != null) {
				d.linkLocation(location);
			}

			locations.add(location);
		}
	}

	/*
	 * Laden der Geräte und verknüpfen mit Kunde, Projekttyp und Projekt
	 */
	private void loadDevices(Connection con) throws SQLException {
		Statement stmt = con.createStatement();
		String sql = "SELECT ID, ArtNr, Bezeichnung, Zubehoer, KundeID, ProjektTypID, ProjektID, PreisEinzel, PreisFlotte FROM Geraet";
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {
			int id = rs.getInt("ID");
			String artNr = rs.getString("ArtNr");
			String bezeichnung = rs.getString("Bezeichnung");
			boolean zubehoer = rs.getBoolean("Zubehoer");
			int kundeID = rs.getInt("KundeID");
			int projectTypId = rs.getInt("ProjektTypID");
			int projectId = rs.getInt("ProjektID");
			int price = rs.getInt("PreisEinzel");
			int priceFM = rs.getInt("PreisFlotte");

			Device d = new Device(id, artNr, bezeichnung, zubehoer,
					projectTypId, price, priceFM);

			Customer c = findCustomer(kundeID);
			if (c != null) {
				c.link(d);
			}

			ProjectTyp pt = findProjectTyp(projectTypId);
			if (pt != null) {
				pt.linkDevice(d);
			}

			Project p = findProject(projectId);
			if (p != null) {
				p.linkDevice(d);
			}

			devices.add(d);
		}
	}

	/*
	 * Laden der Kunden
	 */
	private void loadCustomers(Connection con) throws SQLException {
		Statement stmt = con.createStatement();
		String sql = "SELECT Kunde.ID, AnzMitarbeiter, Flottenmgmt, Latitude, Longitude, Name FROM Kunde INNER JOIN Position ON Kunde.PositionID = Position.ID";
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {
			int anzMitarbeiter = rs.getInt("AnzMitarbeiter");
			boolean flottenmgmt = rs.getBoolean("Flottenmgmt");
			int id = rs.getInt("ID");
			double latitude = rs.getDouble("Latitude");
			double longitude = rs.getDouble("Longitude");
			String name = rs.getString("Name");

			Customer customer = new Customer(id, anzMitarbeiter, flottenmgmt,
					new Location(latitude, longitude), name);
			customers.add(customer);
		}
	}

	/*
	 * LAden der Srviceleistungen und verknüpfen mit Kunde, Projekt und Gerät
	 */
	private void loadServices(Connection con) throws SQLException {
		Statement stmt = con.createStatement();
		String sql = "SELECT ID, KundeID, ProjektID, GeraetID, Typ, Preis FROM Service";
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {
			int id = rs.getInt("ID");
			int kundeID = rs.getInt("KundeID");
			int projectId = rs.getInt("ProjektID");
			int deviceId = rs.getInt("GeraetID");
			String typ = rs.getString("Typ");
			double price = rs.getDouble("Preis");

			Service s = new Service(id, price, typ);

			Customer c = findCustomer(kundeID);
			if (c != null) {
				c.linkService(s);
			}

			Project p = findProject(projectId);
			if (p != null) {
				p.linkService(s);
			}

			Device d = findDevice(deviceId);
			if (d != null) {
				d.linkService(s);
			}

			services.add(s);
		}
	}
}