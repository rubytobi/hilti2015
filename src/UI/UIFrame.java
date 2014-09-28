package UI;

import hilti.HILTITool;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import bigData.Cluster;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserFactory;

import datatypes.Device;
import datatypes.Project;
import datatypes.Store;

import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EtchedBorder;

import java.awt.SystemColor;

import javax.swing.JCheckBox;
import javax.swing.BoxLayout;

public class UIFrame extends JFrame implements ItemListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// public static final String PATH_TO_PROJECT_FOLDER =
	// "file:///Users/marius/Documents/AndEngineWorkspace/RecEngine/";
	public static final String PATH_TO_PROJECT_FOLDER = "E:/Dropbox/PSSE Gruppe/RecEngine/";

	private static Vector<Cluster> lstClusters = new Vector<Cluster>();
	private static Vector<Store> lstStores = new Vector<Store>();
	private JTable tableTools;

	private JCheckBox chckbxShowTools;
	private JCheckBox chckbxShowProjects;

	// BROWSER
	private final Browser browser = BrowserFactory.create();
	private JList<Store> listStores;

	public static int ZOOM_DETAILED = 18;

	public UIFrame() throws HeadlessException {
		super();

		// SET SYSTEM LOOK AND FEEL
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// SWING WINDOW SETUP
		// FRAME
		this.setSize(1280, 700);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JPanel pnlMain = new JPanel();
		pnlMain.setLayout(null);

		// MAP PANEL
		JPanel pnlMap = new JPanel(new BorderLayout());
		pnlMap.setLocation(10, 11);
		pnlMap.setSize(600, 400);
		pnlMap.add(browser.getView().getComponent(), BorderLayout.CENTER);
		this.getContentPane().add(pnlMain, BorderLayout.CENTER);
		pnlMain.add(pnlMap);

		JPanel pnlDevices = new JPanel();
		pnlDevices.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Tools at location",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlDevices.setBounds(885, 11, 369, 256);
		pnlMain.add(pnlDevices);
		pnlDevices.setLayout(null);

		JScrollPane scrollPaneTableDevices = new JScrollPane();
		scrollPaneTableDevices.setBounds(6, 16, 357, 229);
		pnlDevices.add(scrollPaneTableDevices);

		tableTools = new JTable();
		scrollPaneTableDevices.setViewportView(tableTools);
		tableTools.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { "Artikel Nr", "Kunde ID", "Projekt ID" }));
		tableTools.getColumnModel().getColumn(0).setPreferredWidth(150);
		tableTools.getColumnModel().getColumn(0).setMinWidth(150);

		JPanel pnlClusters = new JPanel();
		pnlClusters.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Located clusters",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlClusters.setBounds(614, 11, 262, 123);
		pnlMain.add(pnlClusters);
		pnlClusters.setLayout(null);

		JScrollPane scrollPaneProjects = new JScrollPane();
		scrollPaneProjects.setBounds(6, 16, 250, 100);
		pnlClusters.add(scrollPaneProjects);

		// CLUSTER LIST
		JList<Cluster> listClusters = new JList<Cluster>(lstClusters);
		scrollPaneProjects.setViewportView(listClusters);

		listClusters.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				JList<Cluster> list = (JList<Cluster>) evt.getSource();
				if (evt.getClickCount() == 2) {
					int i = list.locationToIndex(evt.getPoint());
					Cluster c = lstClusters.get(i);
					browser.executeJavaScript("map.panTo("
							+ "new google.maps.LatLng("
							+ c.getCenter().getLatitude() + ","
							+ c.getCenter().getLongitude() + ")" + ")");
					browser.executeJavaScript("map.setZoom(" + ZOOM_DETAILED
							+ ")");
					addToolsToTable(c.getDevices());
				}
			}
		});

		JPanel pnlStores = new JPanel();
		pnlStores.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Located Stores",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlStores.setBounds(614, 144, 262, 123);
		pnlMain.add(pnlStores);
		pnlStores.setLayout(null);

		JScrollPane scrollPaneStores = new JScrollPane();
		scrollPaneStores.setBounds(6, 16, 250, 100);
		pnlStores.add(scrollPaneStores);

		// STORE LIST
		listStores = new JList<Store>(lstStores);
		scrollPaneStores.setViewportView(listStores);

		JPanel pnlMapControls = new JPanel();
		pnlMapControls.setBackground(SystemColor.menu);
		pnlMapControls.setBorder(new TitledBorder(new EtchedBorder(
				EtchedBorder.LOWERED, null, null), "Map controls",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlMapControls.setBounds(10, 422, 600, 48);
		pnlMain.add(pnlMapControls);
		pnlMapControls
				.setLayout(new BoxLayout(pnlMapControls, BoxLayout.X_AXIS));

		chckbxShowProjects = new JCheckBox("show projects");
		chckbxShowProjects.addItemListener(this);
		pnlMapControls.add(chckbxShowProjects);

		JPanel pnlProjects = new JPanel();
		pnlProjects.setLayout(null);
		pnlProjects.setBorder(new TitledBorder(UIManager

		.getBorder("TitledBorder.border"), "Located projects",

		TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlProjects.setBounds(614, 279, 262, 123);
		pnlMain.add(pnlProjects);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 16, 250, 100);
		pnlProjects.add(scrollPane);

		listStores.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				JList<Project> list = (JList<Project>) evt.getSource();
				if (evt.getClickCount() == 2) {
					int i = list.locationToIndex(evt.getPoint());
					Store s = lstStores.get(i);
					browser.executeJavaScript("map.panTo("
							+ "new google.maps.LatLng("
							+ s.getLocation().getLatitude() + ","
							+ s.getLocation().getLongitude() + ")" + ")");
					browser.executeJavaScript("map.setZoom(" + ZOOM_DETAILED
							+ ")");
					addToolsToTable(s.getDevices());
				}
			}
		});

		// ADD ALL THE PROJECTS & STORES TO THE MAP
		for (Cluster c : HILTITool.clusters) {
			lstClusters.add(c);
		}
		for (Store s : HILTITool.stores) {
			lstStores.add(s);
		}

		// WRITE THE JAVASCRIPT FILE TO READ IN MAPVIEW
		try {
			writeMarkersToFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		browser.loadURL(PATH_TO_PROJECT_FOLDER + "map_full.html");

		this.setVisible(true);
	}

	@Override
	public void itemStateChanged(ItemEvent ie) {
		toggleMarkers();
	}

	private void toggleMarkers() {
		if (chckbxShowProjects.isSelected()) {
			browser.executeJavaScript("markersOn(" + ")");
		} else {
			browser.executeJavaScript("markersOff(" + ")");
		}
	}

	public void addToolsToTable(List<Device> devices) {

		System.out.println("LIST SIZE: " + devices.size());

		DefaultTableModel model = (DefaultTableModel) tableTools.getModel();
		while (model.getRowCount() > 0) {
			model.removeRow(0);
		}

		for (Device d : devices) {
			model.addRow(new String[] { d.getArtNr(),
					String.valueOf(d.getCustomer().getId()), "" });
		}

	}

	public static void writeMarkersToFile() throws IOException {
		File fout = new File("markers.js");
		FileOutputStream fos = new FileOutputStream(fout);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		bw.write("var marker, i; var markers = [];var locations = [];");
		bw.newLine();
		bw.write("function addMarkers(){");
		bw.newLine();
		bw.write("locations = [");
		bw.newLine();

		for (Cluster c : lstClusters) {
			double x = c.getCenter().getLatitude();
			double y = c.getCenter().getLongitude();

			bw.write("[" + x + "," + y + ",'project.png'],");
			bw.newLine();
		}

		for (int i = 0; i < lstStores.size(); i++) {
			double x = lstStores.get(i).getLocation().getLatitude();
			double y = lstStores.get(i).getLocation().getLongitude();

			bw.write("[" + x + "," + y + ",'store.png'],");
			bw.newLine();
		}

		bw.write("];");
		bw.newLine();

		bw.write("for (i = 0; i < locations.length; i++) {  \r\n"
				+ "    marker = new google.maps.Marker({\r\n"
				+ "        position: new google.maps.LatLng(locations[i][0], locations[i][1]), \r\n"
				+ "        map: map,\r\n"
				+ "        visible: false, // or false. Whatever you need.\r\n"
				+ "        icon: locations[i][2]\r\n" + "    }); ");
		bw.newLine();
		bw.write("markers.push(marker); // save all markers");
		bw.newLine();
		bw.write("}");
		bw.newLine();

		bw.write("}");
		bw.close();
	}

	public static void writeToolsToFile(ResultSet rs) throws IOException,
			SQLException {
		File fout = new File("tools.js");
		FileOutputStream fos = new FileOutputStream(fout);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		bw.write("function addTools(){");
		bw.newLine();

		while (rs.next()) {
			double lat1 = rs.getDouble("pos1.Latitude");
			double long1 = rs.getDouble("pos1.Longitude");
			double lat2 = rs.getDouble("pos2.Latitude");
			double long2 = rs.getDouble("pos2.Longitude");

			bw.write("var tool = [");
			bw.newLine();
			bw.write("new google.maps.LatLng(" + lat1 + "," + long1 + "),");
			bw.newLine();
			bw.write("new google.maps.LatLng(" + lat2 + "," + long2 + "),");
			bw.newLine();
			bw.write("];");
			bw.newLine();

			bw.write("var toolPath = new google.maps.Polyline({");
			bw.newLine();
			bw.write("path: tool,geodesic: true,strokeColor: '#FF0000',strokeOpacity: 1,strokeWeight: 2");
			bw.newLine();
			bw.write("});");
			bw.newLine();

			bw.write("toolPath.setMap(map);");
			bw.newLine();
		}

		bw.write("}");
		bw.close();
	}
}
