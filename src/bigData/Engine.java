package bigData;

import hilti.HILTITool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

	/*
	 * Versucht die übergebenen GPS-Daten in Gruppen zu fassen
	 */
	public static List<Cluster> clusterLocations(List<Location> locations) {
		List<Cluster> clusters = new ArrayList<Cluster>();

		/*
		 * alle GPS-Daten wrden einzeln in Cluster gepackt
		 */
		for (Location l : locations) {
			List<Clusterable> ls = new ArrayList<Clusterable>();
			ls.add(l);

			Cluster c = new Cluster(clusters.size(), ls);

			clusters.add(c);
		}

		/*
		 * Gruppierung beginnt, dauerschleife bis zu einem Abbruchkriterium
		 */
		System.out.println("Start clustering of " + clusters.size()
				+ " Clusters.");

		while (!cluster(clusters)) {
			/*
			 * Gruppierung noch nicht beendet
			 */
			System.out.println("Still clustering...");
		}

		/*
		 * Gruppierung beendet
		 */
		System.out.println("Clustering done!");
		System.out.println("Clustered into " + clusters.size() + " clusters.");

		return clusters;
	}

	/*
	 * Gruppiert die einzlenen Cluster
	 */
	private static boolean cluster(List<Cluster> clusters) {
		double[][] matrix = new double[clusters.size()][clusters.size()];

		/*
		 * Füllen der Distanzmatrix
		 */
		System.out.println("Current Matrix is " + clusters.size() + "x"
				+ clusters.size());
		for (int i = 0; i < clusters.size(); i++) {
			for (int j = i; j < clusters.size(); j++) {
				matrix[i][j] = clusters.get(i).distance(clusters.get(j));
			}
		}

		/*
		 * finden der kürzesten Distanz (nur die hälfte der Matrix muss
		 * durchgegangen werden)
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
		 * sollte die kleineste Distanz größer als der maximale Projektradius
		 * sein, wird abgebrochen
		 */
		if (matrix[minI][minJ] > MAX_CLUSTER_RADIUS) {
			return true;
		} else {
			System.out.println("Current minimum distance is "
					+ matrix[minI][minJ]);
		}

		/*
		 * zusammenführen der beiden Cluster
		 */
		Cluster newCluster = Cluster.merge(clusters.get(minI),
				clusters.get(minJ));

		/*
		 * entfernen des i-Clusters
		 */
		clusters.remove(minI);

		/*
		 * nachfolgender minJ Index muss angepasst werden
		 */
		if (minI <= minJ) {
			minJ -= 1;
		}

		/*
		 * entfernen des j-Clusters
		 */
		clusters.remove(minJ);

		/*
		 * neu gebildetes Cluster hinzufügen
		 */
		clusters.add(newCluster);
		System.out.println("New cluster count is " + clusters.size());

		return false;
	}

	/*
	 * unterschiedne zwischen lager und projekt
	 */
	public static void recogniseLocations(List<Cluster> clusters,
			List<Project> projects) {
		int volitility;

		/*
		 * alle Cluster werden überprüft
		 */
		for (Cluster c : clusters) {
			volitility = 0;

			/*
			 * Alle Objekte innehralb des Clusters müssen durchgegangen werden
			 */
			for (Clusterable ca : c) {
				Device d = ((Location) ca).getDevice();

				/*
				 * sollte sich das Gerät bewegen, muss erhöht werden
				 */
				if (d.isMoving()) {
					volitility++;
				}
			}

			/*
			 * sollten sich keine Objekte bewegt haben ist es ein Lager
			 */
			if (volitility == 0) {
				/*
				 * Lager anlegen und mit GPS-Daten versehen
				 */
				Store s = new Store(c.getId());
				s.linkLocation(c.getCenter());

				/*
				 * Dem Tool ein neues Lager hinzufügen
				 */
				HILTITool.stores.add(s);

				/*
				 * alle enthaltenen Geräte einlagern
				 */
				for (Clusterable ca : c) {
					Device d = ((Location) ca).getDevice();
					s.linkDevice(d);
				}

				System.out.println("Store recognised: " + s);
			} else {
				/*
				 * falls nichts erkannt wurde
				 */
				HILTITool.clusters.add(c);
				System.out.println("Nothing recognised: " + c);
			}
		}
	}

	/*
	 * Prüfen ob der Kunde an einem Projekt vor Ort ist
	 */
	public static Project userAtProject(Customer c, List<Project> projects) {
		if (projects == null || projects.size() == 0 || c == null) {
			return null;
		}

		/*
		 * Ein Cluster mit nur dem Kunden anlegen
		 */
		List<Clusterable> lCluster = new ArrayList<Clusterable>();
		lCluster.add(c.getLocation());

		Cluster cObject = new Cluster(-1, lCluster);

		Map<Double, Project> matrix = new TreeMap<Double, Project>();

		/*
		 * Alle aktuellen Projekte durchgehen
		 */
		for (Project p : projects) {
			/*
			 * falls das Projekt bereits vorüber ist wird nicht geprüft
			 */
			if (!p.isOver()) {
				matrix.put(cObject.distance(p.toCluster()), p);
			}
		}

		/*
		 * alle Distanzen auslesen und sortieren
		 */
		List<Double> keys = new ArrayList<Double>(matrix.keySet());
		Collections.sort(keys);

		/*
		 * bei mehr als einem aktuellen Projekt wird das näheste zurück gegeben
		 */
		if (matrix.keySet().size() > 0) {
			return matrix.get(keys.get(0));
		} else {
			/*
			 * kein Projekt gefunden
			 */
			return null;
		}
	}

	/*
	 * Runden des Wertes d auf i Nachkommastellen
	 */
	public static double round(double d, int i) {
		return Math.round(Math.pow(10, i) * d) / Math.pow(10, i);
	}

	/*
	 * Prüfen ob Kunde bei einem Cluster vor Ort ist
	 */
	public static Cluster userAtCluster(Customer customer,
			List<Cluster> clusters) {
		if (clusters == null || clusters.size() == 0 || customer == null) {
			return null;
		}

		/*
		 * Kunde wird in ein eigenes Cluster gepackt
		 */
		List<Clusterable> lCluster = new ArrayList<Clusterable>();
		lCluster.add(customer.getLocation());

		Cluster cObject = new Cluster(-1, lCluster);

		Map<Double, Cluster> matrix = new TreeMap<Double, Cluster>();

		/*
		 * sammeln der Distanzen
		 */
		for (Cluster c : clusters) {
			matrix.put(cObject.distance(c), c);
		}

		/*
		 * Sortieren der Distanzen
		 */
		List<Double> keys = new ArrayList<Double>(matrix.keySet());
		Collections.sort(keys);

		/*
		 * Cluster mit der kürzesten Distanz wird zurück gegeben
		 */
		return matrix.get(keys.get(0));
	}

	/*
	 * Zusamenführen der Bewertungen bzgl. Projekthistorie und Projekttyp
	 */
	public static List<Rank> matchProjectType(Cluster cluster,
			List<ProjectTyp> typs, Customer customer) {
		/*
		 * Sammeln beider Bewrtungen
		 */
		List<Rank> rankingType = projectType(cluster, typs);
		List<Rank> rankingHistory = projectHistory(customer.getProjects(), typs);

		/*
		 * Zusammenführen
		 */
		List<Rank> ranking = mergeRanking(rankingType, rankingHistory);
		return ranking;
	}

	/*
	 * Finde anhand der Projekttyp-ID einen Rank
	 */
	public static Rank findRank(int id, List<Rank> ranks) {
		for (Rank r : ranks) {
			if (r.getProjectTyp().getId() == id) {
				return r;
			}
		}

		return null;
	}

	/*
	 * 
	 */
	private static List<Rank> mergeRanking(List<Rank> rankingType,
			List<Rank> rankingHistory) {
		List<Rank> ranking = new ArrayList<Rank>();

		/*
		 * Durchgehen aller möglichen Projekttypen
		 */
		for (ProjectTyp typ : HILTITool.projecttypes) {
			Rank type = findRank(typ.getId(), rankingType);
			Rank history = findRank(typ.getId(), rankingHistory);

			/*
			 * Ranks zusammenführen Typ (2/3) Histrie (1/3)
			 */
			double rankValue = 0;
			rankValue = type.getRank() / 3 * 2 + history.getRank() / 3;

			/*
			 * einen neuen Rank anhand der Berechnung erstellen
			 */
			ranking.add(new Rank(rankValue, typ));
		}

		return ranking;
	}

	/*
	 * Bewertung für ein Projekttyp generieren anhand vergangener Projekte
	 */
	private static List<Rank> projectHistory(List<Project> history,
			List<ProjectTyp> typs) {
		List<Rank> ranking = new ArrayList<Rank>();
		double points = 0;
		double maxPoints = 0;

		/*
		 * Datum um ein Jahr nahc hinten legen
		 */
		Date oneYear = new Date();
		oneYear.setTime((long) (oneYear.getTime() - (3.1556926 * Math.pow(10,
				10))));

		for (ProjectTyp typ : typs) {
			/*
			 * aktuelle Punktzahl und maximale Punktzahl auf 0 setzen
			 */
			points = 0;
			maxPoints = 0;

			for (Project p : history) {
				/*
				 * Maximale Punktzahl erhöhen
				 */
				maxPoints += 1;

				/*
				 * Falls die Projekttypen gleich sind
				 */
				if (typ.equals(p.getProjectTyp())) {
					/*
					 * Falls das Projekt abgeschlossen ist
					 */
					if (p.getEnd() != null) {
						/*
						 * Projekte innerhalb eines Jahres
						 */
						if (p.getEnd().after(oneYear)) {
							System.out.println("[H=1] " + p);
							points += 1;
						} else {
							/*
							 * Projekte älter als ein Jahr
							 */
							System.out.println("[H=0.5] " + p);
							points += 0.5;
						}
					} else {
						/*
						 * aktuelle Projekte!
						 */
						System.out.println("[H=1] " + p);
						points += 1;
					}
				} else {
					/*
					 * Projekttyp stimmt nicht überein
					 */
					System.out.println("[H=0] " + p);
					points += 0;
				}
			}

			/*
			 * Erzeugen einer relativen Bewertung, die gesammelten Punkte werden
			 * relativ zur maximal erreichbaren Punktezahl bewertet
			 */
			ranking.add(new Rank(points / maxPoints, typ));
		}

		return ranking;
	}

	private static List<Rank> projectType(Cluster cluster, List<ProjectTyp> typs) {
		List<Rank> ranking = new ArrayList<Rank>();
		double points = 0;
		double maxPoints = 0;

		for (ProjectTyp typ : typs) {
			/*
			 * aktuelle Punktzahl und maximale Punktzahl auf 0 setzen
			 */
			points = 0;
			maxPoints = 0;

			for (Device d : cluster.getDevices()) {
				/*
				 * Maximale Punktzahl erhöhen
				 */
				maxPoints += 1.5;

				/*
				 * Projekttyp enthält entsprehcendes Gerät
				 */
				if (typ.getDevices().contains(d)) {
					System.out.println("[T=1.5] " + d);
					points += 1.5;
				} else {
					/*
					 * Projekttyp enthält kein entsprehcendes Gerät
					 */
					System.out.println("[T=0] " + d);
					points += 0;
				}
			}

			/*
			 * Erzeugen einer relativen Bewertung, die gesammelten Punkte werden
			 * relativ zur maximal erreichbaren Punktezahl bewertet
			 */
			ranking.add(new Rank(points / maxPoints, typ));
		}

		return ranking;
	}

	/*
	 * Erkennen der Fehlenden Geräte des Projektes
	 */
	public static List<Device> detectMissingDevices(Project project) {
		List<Device> devices = new ArrayList<Device>();
		ProjectTyp type = project.getProjectTyp();

		for (Device d : type.getDevices()) {
			/*
			 * Sollte das Gerät noch nicht enthalten sein wird es empfohlen
			 */
			if (!project.getDevices().contains(d)) {
				devices.add(d);
			}
		}

		return devices;
	}

	/*
	 * Berechnen der Anzahl an gleichen Projekttypen
	 */
	public static int getNumberOfProjectsInSameCat(Project p) {
		List<Project> pList = p.getCustomer().getProjects();
		int count = 0;

		/*
		 * Durchgehen aller alten Projekttypen
		 */
		for (Project pOld : pList) {
			if (pOld.getProjectTyp().equals(p.getProjectTyp())) {
				count++;
			}
		}
		return count;
	}

	/*
	 * Anlegen einer Liste an Empfehlungen für das Projekt
	 */
	public static List<Recommendation> generateRec(Project project) {
		List<Recommendation> recs = new ArrayList<Recommendation>();

		// fehlende geräte anhand des projektes sammeln
		List<Device> missing = detectMissingDevices(project);

		for (Device d : missing) {
			recs.add(new Recommendation(1, d));

			// geräte aus der gleichen produktlinie
			List<Device> similar = devicesFromProductLine(d.getBezeichnung());

			for (Device sD : similar) {
				if (!sD.equals(d)) {
					recs.add(new Recommendation(0.5, sD));
				}
			}
		}

		/*
		 * Sortieren nach Empfehlungen
		 */
		Collections.sort(recs);

		return recs;
	}

	/*
	 * Geräte der selben Produktlinie
	 */
	private static List<Device> devicesFromProductLine(String bezeichnung) {
		List<Device> similar = new ArrayList<Device>();

		/*
		 * Prüfen aller sämtlichen Geräte auf gleiche Bezeichnung
		 */
		for (Device d : HILTITool.devices) {
			if (d.getBezeichnung().equals(bezeichnung)) {
				similar.add(d);
			}
		}

		return similar;
	}
}