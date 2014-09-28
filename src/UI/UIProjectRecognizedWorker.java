package UI;

import hilti.HILTITool;

import java.awt.Dimension;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.SwingConstants;

import bigData.Cluster;
import bigData.Engine;
import bigData.Rank;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;

public class UIProjectRecognizedWorker extends JFrame implements LoadListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// BROWSER
	private final Browser browser = BrowserFactory.create();
	private final Cluster cluster;
	private final Customer customer;
	private JTable tableTools;
	private JComboBox comboBoxProjectType;

	public UIProjectRecognizedWorker(final Cluster cluster, final Customer customer)
			throws HeadlessException {
		super("Project recognized!");
		this.cluster = cluster;
		this.customer = customer;

		final List<Rank> match = Engine.matchProjectType(cluster,
				HILTITool.projecttypes, customer);

		Collections.sort(match);

		for (Rank r : match) {
			System.out.println(r);
		}

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

		JLabel lblANewProject = new JLabel(
				"<html>A new project location<br/>has been recognized!</html>");
		lblANewProject.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblANewProject.setForeground(SystemColor.textHighlight);
		lblANewProject.setHorizontalAlignment(SwingConstants.CENTER);
		lblANewProject.setFont(new Font("Tahoma", Font.PLAIN, 20));
		pnlInfo.add(lblANewProject);

		JPanel pnlDetail = new JPanel();
		pnlInfo.add(pnlDetail);
		pnlDetail.setLayout(new BoxLayout(pnlDetail, BoxLayout.Y_AXIS));

		JPanel pnlType = new JPanel();
		pnlDetail.add(pnlType);

		JLabel lblType = new JLabel("Project type:");
		lblType.setFont(new Font("Tahoma", Font.BOLD, 11));
		pnlType.add(lblType);

		comboBoxProjectType = new JComboBox(match.toArray());
		comboBoxProjectType.setPreferredSize(new Dimension(250, 20));
		pnlType.add(comboBoxProjectType);

		JPanel pnlLocation = new JPanel();
		pnlDetail.add(pnlLocation);

		JLabel lblLocation = new JLabel("Project location:");
		lblLocation.setFont(new Font("Tahoma", Font.BOLD, 11));
		pnlLocation.add(lblLocation);

		JLabel lblLatn = new JLabel(cluster.getCenter().toString());
		pnlLocation.add(lblLatn);

		JPanel pnlCount = new JPanel();
		pnlDetail.add(pnlCount);

		JLabel lblCount = new JLabel("Device count:");
		lblCount.setFont(new Font("Tahoma", Font.BOLD, 11));
		pnlCount.add(lblCount);

		JLabel label_1 = new JLabel(cluster.getDevices().size() + "");
		pnlCount.add(label_1);

		tableTools = new JTable();
		tableTools.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { "ArtNr", "Bezeichnung" }));
		tableTools.getColumnModel().getColumn(0).setPreferredWidth(99);
		addToolsToTable(cluster.getDevices());

		JScrollPane scrollPane = new JScrollPane(tableTools);
		pnlInfo.add(scrollPane);

		JPanel pnlSouth = new JPanel();
		getContentPane().add(pnlSouth, BorderLayout.SOUTH);

		JButton btnAccept = new JButton("Create project");
		btnAccept.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				Project p = new Project(HILTITool.projects.size(), "", customer
						.getCountEmployees(), null);

				p.linkLocation(cluster.getCenter());
				p.linkCustomer(customer);

				for (Device d : cluster.getDevices()) {
					p.linkDevice(d);
				}

				int i = comboBoxProjectType.getSelectedIndex();
				Rank rank = match.get(i);
				ProjectTyp pt = rank.getProjectTyp();

				p.linkProjectTyp(pt);

				HILTITool.projects.add(p);
				System.out.println("Project added");
				new UIProjectViewWorker(p, rank);
				dispose();
			}
		});
		pnlSouth.add(btnAccept);

		JButton btnCancel = new JButton("Cancel process");
		pnlSouth.add(btnCancel);
		btnCancel.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				new UILogin();
				dispose();
			}
		});

		this.setVisible(true);
	}

	@Override
	public void onDocumentLoadedInFrame(FrameLoadEvent arg0) {
	}

	@Override
	public void onDocumentLoadedInMainFrame(LoadEvent arg0) {

	}

	@Override
	public void onFailLoadingFrame(FailLoadingEvent arg0) {

	}

	@Override
	public void onFinishLoadingFrame(FinishLoadingEvent arg0) {
		browser.executeJavaScript("map.panTo(" + "new google.maps.LatLng("
				+ (cluster.getCenter().getLatitude() + 0.00025f) + ","
				+ (cluster.getCenter().getLongitude() - 0.001f) + ")" + ")");
		browser.executeJavaScript("map.setZoom(" + (UIFrame.ZOOM_DETAILED)
				+ ")");
	}

	@Override
	public void onProvisionalLoadingFrame(ProvisionalLoadingEvent arg0) {

	}

	@Override
	public void onStartLoadingFrame(StartLoadingEvent arg0) {

	}

	public void addToolsToTable(List<Device> devices) {

		System.out.println("LIST SIZE: " + devices.size());

		DefaultTableModel model = (DefaultTableModel) tableTools.getModel();
		while (model.getRowCount() > 0) {
			model.removeRow(0);
		}

		for (Device d : devices) {
			model.addRow(new String[] { d.getArtNr(), d.getScope() });
		}

	}

	public void update() {
		// TODO Auto-generated method stub

	}

}