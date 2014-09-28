package bigData;

import hilti.HILTITool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import datatypes.Customer;
import datatypes.Device;
import datatypes.Location;
import datatypes.Project;
import datatypes.ProjectTyp;
import datatypes.Store;

public class Engine {
	/*
	 * to be defined in km, shall be 100m
	 */
	private static double MAX_CLUSTER_RADIUS = 0.20;

	public static void matchKnownLocations(List<Cluster> clusters,
			List<Project> projects) {
		for (Project p : projects) {

			for (Cluster c : clusters) {

				if (!p.isLocated()) {
					System.out.println("Project " + p + " matched with " + c);
				}
			}
		}

		for (Cluster c : clusters) {
			System.out.println("Cluster unmatched: " + c.toString());
		}

		for (Project p : projects) {
			System.out.println("Project unmatched: " + p.toString());
		}
	}

	public static List<Cluster> clusterLocations(List<Location> locations) {
		List<Cluster> clusters = new ArrayList<Cluster>();

		/*
		 * put every single location (object) into one cluster
		 */
		for (Location l : locations) {
			List<Clusterable> ls = new ArrayList<Clusterable>();
			ls.add(l);

			Cluster c = new Cluster(clusters.size(), ls);

			clusters.add(c);
		}

		System.out.println("Start clustering of " + clusters.size()
				+ " Clusters.");

		while (!cluster(clusters)) {
			System.out.println("Still clustering...");
		}

		System.out.println("Clustering done!");
		System.out.println("Clustered into " + clusters.size() + " clusters.");

		return clusters;
	}

	private static boolean cluster(List<Cluster> clusters) {
		double[][] matrix = new double[clusters.size()][clusters.size()];

		/*
		 * Fill distance matrix
		 */
		System.out.println("Current Matrix is " + clusters.size() + "x"
				+ clusters.size());
		for (int i = 0; i < clusters.size(); i++) {
			for (int j = i; j < clusters.size(); j++) {
				matrix[i][j] = clusters.get(i).distance(clusters.get(j));
			}
		}

		/*
		 * find smallest distance
		 */
		int minI = 0;
		int minJ = 1;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = (i + 1); j < matrix[i].length; j++) {
				if (matrix[minI][minJ] > matrix[i][j]) {
					minI = i;
					minJ = j;
				}
			}
		}

		/*
		 * if the smallest distance between two clusters is larger than the
		 * MAX_CLUSTER_RADIUS defines, all clustering is done
		 */
		if (matrix[minI][minJ] > MAX_CLUSTER_RADIUS) {
			return true;
		} else {
			System.out.println("Current minimum distance is "
					+ matrix[minI][minJ]);
		}

		/*
		 * merge cluster i and cluster j
		 */
		Cluster newCluster = Cluster.merge(clusters.get(minI),
				clusters.get(minJ));

		clusters.remove(minI);

		/*
		 * nachfolgender minJ Index muss angepasst werden
		 */
		if (minI <= minJ) {
			minJ -= 1;
		}

		clusters.remove(minJ);

		clusters.add(newCluster);
		System.out.println("New cluster count is " + clusters.size());

		return false;
	}

	/*
	 * differentiate store or project
	 */
	public static void recogniseLocations(List<Cluster> clusters,
			List<Project> projects) {
		int volitility;

		for (Cluster c : clusters) {
			volitility = 0;

			for (Clusterable ca : c) {
				Device d = ((Location) ca).getDevice();
				if (d.isMoving()) {
					volitility++;
				}
			}

			if (volitility == 0) {
				Store s = new Store(c.getId());
				s.linkLocation(c.getCenter());

				HILTITool.stores.add(s);

				for (Clusterable ca : c) {
					Device d = ((Location) ca).getDevice();
					s.linkDevice(d);
				}
				System.out.println("Store recognised: " + s);
			} else {
				HILTITool.clusters.add(c);
				System.out.println("Nothing recognised: " + c);
			}
		}
	}

	public static void recogniseProjectTyp(Project p) {

	}

	/*
	 * Location, Projects
	 */
	public static Project userAtProject(Customer c, List<Project> projects) {
		if (projects == null || projects.size() == 0 || c == null) {
			return null;
		}

		List<Clusterable> lCluster = new ArrayList<Clusterable>();
		lCluster.add(c.getLocation());

		Cluster cObject = new Cluster(-1, lCluster);

		Map<Double, Project> matrix = new TreeMap<Double, Project>();

		for (Project p : projects) {
			if (!p.isOver()) {
				matrix.put(cObject.distance(p.toCluster()), p);
			}
		}

		List<Double> keys = new ArrayList<Double>(matrix.keySet());
		Collections.sort(keys);

		if (matrix.keySet().size() > 0) {
			return matrix.get(keys.get(0));
		} else {
			return null;
		}
	}

	public static Cluster userAtCluster(Customer customer,
			List<Cluster> clusters) {
		if (clusters == null || clusters.size() == 0 || customer == null) {
			return null;
		}

		List<Clusterable> lCluster = new ArrayList<Clusterable>();
		lCluster.add(customer.getLocation());

		Cluster cObject = new Cluster(-1, lCluster);

		Map<Double, Cluster> matrix = new TreeMap<Double, Cluster>();

		for (Cluster c : clusters) {
			matrix.put(cObject.distance(c), c);
		}

		List<Double> keys = new ArrayList<Double>(matrix.keySet());
		Collections.sort(keys);
		return matrix.get(keys.get(0));
	}

	public static List<Rank> matchProjectType(Cluster cluster,
			List<ProjectTyp> typs, Customer customer) {
		List<Rank> rankingType = projectType(cluster, typs);
		List<Rank> rankingHistory = projectHistory(customer.getProjects(), typs);

		List<Rank> ranking = mergeRanking(rankingType, rankingHistory);
		return ranking;
	}

	public static Rank findRank(int id, List<Rank> ranks) {
		for (Rank r : ranks) {
			if (r.getProjectTyp().getId() == id) {
				return r;
			}
		}

		return null;
	}

	private static List<Rank> mergeRanking(List<Rank> rankingType,
			List<Rank> rankingHistory) {
		List<Rank> ranking = new ArrayList<Rank>();

		for (ProjectTyp typ : HILTITool.projecttypes) {
			Rank type = findRank(typ.getId(), rankingType);
			Rank history = findRank(typ.getId(), rankingHistory);

			double rankValue = 0;
			rankValue = type.getRank() + history.getRank();
			ranking.add(new Rank(rankValue, typ));
		}

		return ranking;
	}

	private static List<Rank> projectHistory(List<Project> history,
			List<ProjectTyp> typs) {
		List<Rank> ranking = new ArrayList<Rank>();
		double points = 0;

		for (ProjectTyp typ : typs) {
			points = 0;

			System.out.println(typ);
			for (Project p : history) {
				if (typ.equals(p.getProjectTyp())) {
					System.out.println("[H=1] " + p);
					points += 1;
				} else {
					System.out.println("[H=0] " + p);
					points += 0;
				}
			}

			ranking.add(new Rank(points, typ));
		}

		return ranking;
	}

	private static List<Rank> projectType(Cluster cluster, List<ProjectTyp> typs) {
		List<Rank> ranking = new ArrayList<Rank>();
		double points = 0;

		for (ProjectTyp typ : typs) {
			points = 0;

			System.out.println(typ);
			for (Device d : cluster.getDevices()) {
				if (typ.getDevices().contains(d)) {
					System.out.println("[T=1.5] " + d);
					points += 1.5;
				} else {
					System.out.println("[T=0] " + d);
					points += 0;
				}
			}

			ranking.add(new Rank(points, typ));
		}

		return ranking;
	}

	public static List<Device> detectMissingDevices(Project project) {
		List<Device> devices = new ArrayList<Device>();
		ProjectTyp type = project.getProjectTyp();

		for (Device d : type.getDevices()) {
			if (!project.getDevices().contains(d)) {
				devices.add(d);
			}
		}

		return devices;
	}

	public List<Device> missingDevices(Project p, ProjectTyp typ) {
		List<Device> missing = new ArrayList<Device>();

		for (Device d : typ.getDevices()) {
			if (!p.getDevices().contains(d)) {
				missing.add(d);
			}
		}

		return missing;
	}

	public static List<Device> filterDevicesInStore(List<Store> stores,
			List<Device> missing) {
		List<Device> filtered = new ArrayList<Device>();
		filtered.addAll(missing);

		for (Device d : missing) {
			for (Store s : stores) {
				if (s.getDevices().contains(d)) {
					filtered.remove(d);
				}
			}
		}

		return filtered;
	}
}