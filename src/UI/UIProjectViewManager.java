package UI;

import java.awt.Dimension;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JLabel;

import java.awt.Font;

import bigData.Engine;
import bigData.Rank;
import bigData.Recommendation;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserFactory;
import com.teamdev.jxbrowser.chromium.events.FailLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadListener;
import com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;

import datatypes.Device;
import datatypes.Project;

import javax.swing.BoxLayout;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;

import java.awt.Component;
import java.util.List;
import java.awt.Color;
import java.awt.SystemColor;

public class UIProjectViewManager extends JFrame implements LoadListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// BROWSER
	private final Browser browser = BrowserFactory.create();
	private JTable tableDevices;
	private JTable tableService;
	private Project project;
	private JLabel valLocation;
	private JLabel valProjectType;
	private Rank rank;
	private JLabel valSavings;

	public UIProjectViewManager(Project p, Rank r) throws HeadlessException {
		super("Project recognized!");
		setTitle("Project Based RecEngine");

		this.project = p;
		this.rank = r;

		// SET SYSTEM LOOK AND FEEL
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setSize(400, 600);

		browser.loadURL(UIFrame.PATH_TO_PROJECT_FOLDER + "map_tools.html");
		browser.addLoadListener(this);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel pnlCenter = new JPanel();
		getContentPane().add(pnlCenter);
		pnlCenter.setLayout(new BoxLayout(pnlCenter, BoxLayout.Y_AXIS));

		// SET THE MAP VIEW AND PAN TO LOCATION
		JPanel pnlMap = new JPanel(new BorderLayout());
		pnlMap.setPreferredSize(new Dimension(400, 200));
		pnlCenter.add(pnlMap);
		pnlMap.add(browser.getView().getComponent(), BorderLayout.CENTER);

		// INFO PANEL
		JPanel pnlInfo = new JPanel();
		pnlCenter.add(pnlInfo);
		pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));

		JPanel pnlDetail = new JPanel();
		pnlInfo.add(pnlDetail);
		pnlDetail.setLayout(new BoxLayout(pnlDetail, BoxLayout.Y_AXIS));

		JPanel pnlType = new JPanel();
		pnlDetail.add(pnlType);

		JLabel lblType = new JLabel("Project type:");
		lblType.setFont(new Font("Tahoma", Font.BOLD, 11));
		pnlType.add(lblType);

		valProjectType = new JLabel("Hoch- und Tiefbau");
		pnlType.add(valProjectType);

		JPanel pnlLocation = new JPanel();
		pnlDetail.add(pnlLocation);

		JLabel lblLocation = new JLabel("Project location:");
		lblLocation.setFont(new Font("Tahoma", Font.BOLD, 11));
		pnlLocation.add(lblLocation);

		valLocation = new JLabel(project.getLocation().toString());
		pnlLocation.add(valLocation);
		
		JPanel pnlFleet = new JPanel();
		pnlFleet.setBackground(UIManager.getColor("CheckBox.select"));
		pnlDetail.add(pnlFleet);
		
		JLabel valFleet = new JLabel("no fleet user");
		valFleet.setFont(new Font("Tahoma", Font.BOLD, 11));
		pnlFleet.add(valFleet);

		tableDevices = new JTable();
		tableDevices.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { "ArtNo", "Description","PriceRetail","PriceFleet" }));
		tableDevices.getColumnModel().getColumn(0).setPreferredWidth(80);
		tableDevices.getColumnModel().getColumn(1).setPreferredWidth(150);
		addToolsToTable(project.getDevices());

		JScrollPane scrollPaneDevices = new JScrollPane(tableDevices);
		scrollPaneDevices.setPreferredSize(new Dimension(400, 100));
		pnlInfo.add(scrollPaneDevices);

		JLabel lblService = new JLabel("Device service history");
		lblService.setForeground(new Color(100, 149, 237));
		lblService.setFont(new Font("Lucida Grande", Font.BOLD, 15));
		lblService.setAlignmentX(Component.CENTER_ALIGNMENT);
		pnlInfo.add(lblService);

		tableService = new JTable();
		tableService.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { "ArtNo", "Descritpion","Type","Price" }));
		tableService.getColumnModel().getColumn(0).setPreferredWidth(80);
		tableService.getColumnModel().getColumn(1).setPreferredWidth(140);

		JScrollPane scrollPaneService = new JScrollPane(tableService);
		scrollPaneService.setPreferredSize(new Dimension(400, 100));
		pnlInfo.add(scrollPaneService);

		JPanel pnlOldProjects = new JPanel();
		pnlInfo.add(pnlOldProjects);

		JLabel lblDuration = new JLabel("If your project duration is ");
		lblDuration.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		lblDuration.setForeground(Color.BLACK);
		pnlOldProjects.add(lblDuration);
		
		JLabel valDuration = new JLabel("less than 14 days");
		valDuration.setForeground(new Color(60, 179, 113));
		valDuration.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		pnlOldProjects.add(valDuration);

		JPanel pnlSameDevices = new JPanel();
		pnlInfo.add(pnlSameDevices);

		JLabel lblSameDevices = new JLabel("you should switch to our Fleetservice");
		lblSameDevices.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		lblSameDevices.setForeground(new Color(0, 0, 0));
		pnlSameDevices.add(lblSameDevices);

		valSavings = new JLabel();
		valSavings.setForeground(new Color(60, 179, 113));
		valSavings.setAlignmentX(Component.CENTER_ALIGNMENT);
		valSavings.setFont(new Font("Lucida Grande", Font.BOLD, 26));
		pnlInfo.add(valSavings);
		
		JLabel lblSavingsForAn = new JLabel("saved for an average project length of 12 days");
		lblSavingsForAn.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblSavingsForAn.setForeground(Color.BLACK);
		lblSavingsForAn.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		pnlInfo.add(lblSavingsForAn);

		JPanel pnlSouth = new JPanel();
		getContentPane().add(pnlSouth, BorderLayout.SOUTH);

		update();

		setLocationRelativeTo(null);
		this.setVisible(true);
	}

	@Override
	public void onDocumentLoadedInFrame(FrameLoadEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDocumentLoadedInMainFrame(LoadEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFailLoadingFrame(FailLoadingEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinishLoadingFrame(FinishLoadingEvent arg0) {
		browser.executeJavaScript("map.panTo(" + "new google.maps.LatLng("
				+ project.getLocation().getLatitude() + ","
				+ project.getLocation().getLongitude() + ")" + ")");
		browser.executeJavaScript("map.setZoom(" + UIFrame.ZOOM_DETAILED + ")");
	}

	@Override
	public void onProvisionalLoadingFrame(ProvisionalLoadingEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartLoadingFrame(StartLoadingEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void addToolsToTable(List<Device> devices) {

		System.out.println("LIST SIZE: " + devices.size());

		DefaultTableModel model = (DefaultTableModel) tableDevices.getModel();
		while (model.getRowCount() > 0) {
			model.removeRow(0);
		}

		for (Device d : devices) {
			model.addRow(new String[] { d.getArtNr(), d.getScope() });
		}

	}

	public void update() {
		addToolsToTable(project.getDevices());
		calculateOldProjects();
		calculateSameDevices();

		putRecommendations();

		setPercentage(rank.getFixedRank());
		valLocation.setText(project.getLocation().toString());
		valProjectType.setText(project.getProjectTyp().getDescription());
	}

	public void setPercentage(double d) {
		valSavings.setText("2314.20\u20AC");

		if (d > 75) {
			valSavings.setForeground(new Color(60, 179, 113));
		} else if (d < 75 && d > 25) {
			// TODO
			valSavings.setForeground(new Color(60, 179, 113));
		} else {
			// TODO
			valSavings.setForeground(new Color(60, 179, 113));
		}
	}

	private void putRecommendations() {
		List<Recommendation> recs = Engine.generateRec(project);

		DefaultTableModel dtm = (DefaultTableModel) tableService.getModel();

		for (Recommendation r : recs) {
			dtm.addRow(new Object[] { r.getDevice().getArtNr(),
					r.getDevice().getBezeichnung() });
		}
	}

	private void calculateSameDevices() {
		List<Device> pDevices = project.getDevices();
		List<Device> ptDevices = project.getProjectTyp().getDevices();

		int score = 0;

		for (Device d : pDevices) {
			if (ptDevices.contains(d))
				score++;
		}

	}

	private void calculateOldProjects() {

	}
}
