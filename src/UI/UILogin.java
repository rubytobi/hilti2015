package UI;

import hilti.HILTITool;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JRadioButton;
import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;
import javax.swing.JButton;
import javax.swing.ButtonGroup;

import bigData.Cluster;
import bigData.Engine;
import datatypes.Customer;
import datatypes.Project;

/**
 * 
 * Oberfläche zur Kundenauswahl und der gewünschten Rolle
 *
 */
public class UILogin extends JFrame {

	private static final long serialVersionUID = 1L;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private ImageIcon iconManager;
	private ImageIcon iconWorker;
	private JLabel lblRole;
	public static int selectedRole = 1;
	private final JComboBox<String> comboBox;
	public static final int ROLE_WORKER = 1;
	public static final int ROLE_MANAGER = 2;

	public UILogin() throws HeadlessException {
		super();

		// SET SYSTEM LOOK AND FEEL
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		this.setTitle("LOGIN..");
		this.setSize(400, 600);

		getContentPane().setLayout(new GridLayout(2, 1, 0, 0));

		JPanel pnlImage = new JPanel();
		getContentPane().add(pnlImage);
		pnlImage.setLayout(new BorderLayout(0, 0));
		BufferedImage userManager = null;
		BufferedImage userWorker = null;
		// Rollenbilder laden
		try {
			userManager = ImageIO.read(new File("user_manager.png"));
			userWorker = ImageIO.read(new File("user_worker.png"));
		} catch (IOException e) {
			System.err.println("Rollenbilder konnten nicht geladen werden");
			e.printStackTrace();
		}

		iconManager = new ImageIcon(userManager);
		iconWorker = new ImageIcon(userWorker);

		lblRole = new JLabel(iconWorker);
		pnlImage.add(lblRole);

		JPanel pnlSetup = new JPanel();
		getContentPane().add(pnlSetup);
		pnlSetup.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		pnlSetup.add(panel, BorderLayout.NORTH);

		JLabel lblNewLabel = new JLabel("Select Customer:");
		panel.add(lblNewLabel);

		// Combobox zur Kundenauswahl
		comboBox = new JComboBox<String>();
		panel.add(comboBox);

		JPanel panel_1 = new JPanel();
		pnlSetup.add(panel_1);
		panel_1.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Available roles",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.add(panel_2);

		//RadioButton zur Rollenauswahl
		JRadioButton rdbtnWorker = new JRadioButton("Worker");
		rdbtnWorker.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent ie) {
				JRadioButton rdbtn = (JRadioButton) ie.getSource();
				if (rdbtn.isSelected()) {
					lblRole.setIcon(iconWorker);
					selectedRole = ROLE_WORKER;
				}
			}
		});
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.Y_AXIS));
		buttonGroup.add(rdbtnWorker);
		rdbtnWorker.setSelected(true);
		panel_2.add(rdbtnWorker);

		JRadioButton rdbtnManager = new JRadioButton("Financial manager");
		rdbtnManager.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent ie) {
				JRadioButton rdbtn = (JRadioButton) ie.getSource();
				if (rdbtn.isSelected()) {
					lblRole.setIcon(iconManager);
					selectedRole = ROLE_MANAGER;
				}
			}
		});
		buttonGroup.add(rdbtnManager);
		panel_2.add(rdbtnManager);

		JButton btnLogin = new JButton("Login");
		btnLogin.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// Bei Mausklick der Auswahl entsprechend Fenster öffnen
				Customer customer = HILTITool.customers.get(comboBox
						.getSelectedIndex());
				Project p = Engine.userAtProject(customer, HILTITool.projects);
				Cluster cluster = Engine.userAtCluster(customer,
						HILTITool.clusters);

				if (selectedRole == ROLE_WORKER) {
						new UIProjectRecognizedWorker(cluster, customer);
				} else if (selectedRole == ROLE_MANAGER) {
					if (p != null) {
						// TODO
						new UIProjectViewManager(p, null);
					} else if (cluster != null) {
						// TODO
					}
				}
			}
		});
		pnlSetup.add(btnLogin, BorderLayout.SOUTH);

		update();
		this.setVisible(true);
	}

	/**
	 * Combobox mit Kunden befüllen
	 */
	public void update() {
		List<String> customers = new ArrayList<String>();

		for (Customer c : HILTITool.customers) {
			customers.add(c.toString());
		}

		comboBox.setModel(new DefaultComboBoxModel<String>(customers
				.toArray(new String[customers.size()])));
	}
}
