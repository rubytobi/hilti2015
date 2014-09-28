package UI;

import hilti.HILTITool;

import java.awt.Dimension;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.GridLayout;

import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.SwingConstants;

import bigData.Cluster;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserFactory;
import com.teamdev.jxbrowser.chromium.events.FailLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadListener;
import com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;

import datatypes.Customer;
import datatypes.Device;
import datatypes.Project;
import datatypes.ProjectTyp;

import java.awt.SystemColor;

import javax.swing.BoxLayout;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JButton;

import java.awt.FlowLayout;

import javax.swing.JComboBox;

import java.awt.Color;

public class UIProjectView extends JFrame implements LoadListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// BROWSER
	private final Browser browser = BrowserFactory.create();
	private JTable tableDevices;
	private JTable tableRec;
	private Project project;
	private JLabel valOldProjects;
	private JLabel valSameDevices;

	public UIProjectView(Project p) throws HeadlessException {
		super("Project recognized!");
		setTitle("Project Based RecEngine");

		this.project = p;

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

		JLabel valProjectType = new JLabel("Hoch- und Tiefbau");
		pnlType.add(valProjectType);

		JPanel pnlLocation = new JPanel();
		pnlDetail.add(pnlLocation);

		JLabel lblLocation = new JLabel("Project location:");
		lblLocation.setFont(new Font("Tahoma", Font.BOLD, 11));
		pnlLocation.add(lblLocation);

		JLabel valLocation = new JLabel(project.getLocation().toString());
		pnlLocation.add(valLocation);

		tableDevices = new JTable();
		tableDevices.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { "ArtNo", "Description" }));
		tableDevices.getColumnModel().getColumn(0).setPreferredWidth(80);

		JScrollPane scrollPaneDevices = new JScrollPane(tableDevices);
		scrollPaneDevices.setPreferredSize(new Dimension(400, 100));
		pnlInfo.add(scrollPaneDevices);

		JLabel lblRec = new JLabel("recommendations based on project");
		lblRec.setForeground(new Color(100, 149, 237));
		lblRec.setFont(new Font("Lucida Grande", Font.BOLD, 15));
		lblRec.setAlignmentX(Component.CENTER_ALIGNMENT);
		pnlInfo.add(lblRec);

		tableRec = new JTable();
		tableRec.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { "ArtNo", "Descritpion", "Status", "%" }));
		tableRec.getColumnModel().getColumn(0).setPreferredWidth(80);
		tableRec.getColumnModel().getColumn(1).setPreferredWidth(140);
		tableRec.getColumnModel().getColumn(3).setPreferredWidth(25);

		JScrollPane scrollPaneRec = new JScrollPane(tableRec);
		scrollPaneRec.setPreferredSize(new Dimension(400, 100));
		pnlInfo.add(scrollPaneRec);

		JPanel pnlOldProjects = new JPanel();
		pnlInfo.add(pnlOldProjects);

		JLabel lblOldProjects = new JLabel("Old projects:");
		lblOldProjects.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblOldProjects.setForeground(new Color(100, 149, 237));
		pnlOldProjects.add(lblOldProjects);

		valOldProjects = new JLabel("5/6 projects of the same type");
		pnlOldProjects.add(valOldProjects);

		JPanel pnlSameDevices = new JPanel();
		pnlInfo.add(pnlSameDevices);

		JLabel lblSameDevices = new JLabel("Devices:");
		lblSameDevices.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblSameDevices.setForeground(new Color(100, 149, 237));
		pnlSameDevices.add(lblSameDevices);

		valSameDevices = new JLabel("4/7 devices match project type");
		pnlSameDevices.add(valSameDevices);

		JLabel label = new JLabel("87%");
		label.setForeground(new Color(60, 179, 113));
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setFont(new Font("Lucida Grande", Font.BOLD, 26));
		pnlInfo.add(label);

		JPanel pnlSouth = new JPanel();
		getContentPane().add(pnlSouth, BorderLayout.SOUTH);

		setLocationRelativeTo(null);
		update();
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

	private void calculateOldProjects() {
		Customer c = project.getCustomer();
		List<Project> projects = c.getProjects();
		String var = "";
		ProjectTyp ref = project.getProjectTyp();
		int i = 0;

		for (Project p : projects) {
			if (ref.equals(p.getProjectTyp())) {
				i++;
			}
		}

		var += i + "/" + projects.size() + " projects of the same type";
		this.valOldProjects.setText(var);
	}

	public void update() {
		addToolsToTable(project.getDevices());
		calculateOldProjects();
	}

}
