import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneId;

class ComboItem {
    String text;
    ImageIcon icon;

    ComboItem(String text, ImageIcon icon) {
        this.text = text;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return text; // important
    }
}



public class FileBackupSquareTabs {

    private JTextField pathField;
    private JTextArea fileArea;
    private JLabel homeLabel;
    private JLabel welcomeLabel;
    private JFrame frame;   
    private JPanel Taskbarpanel;
    private JButton JBHome;
    private JButton JBBackup;
    private JButton JBHistory;	
    private JButton JBHelp;
    private JTable table;	

    private JRadioButton dailyRadio, weeklyRadio, customRadio;
    private List<String> savedSchedules = new ArrayList<>();
    private JPanel dynamicPanel;
    private JSpinner timeSpinner;
    private JCheckBox mon, tue, wed, thu, fri, sat, sun;
    private JSpinner intervalSpinner;
    private JComboBox<String> intervalUnit;

    private Thread backupThread;

    public FileBackupSquareTabs() {
        frame = new JFrame("File Backup Manager");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
	frame.setLayout(null);
        JPanel Taskbarpanel = new JPanel(null);

        ImageIcon icon1 = new ImageIcon("h1.jpg");
        JButton JBHome = new JButton(icon1);

        ImageIcon icon2 = new ImageIcon("b1.jpg");
        JButton JBBackup = new JButton(icon2);

        ImageIcon icon3 = new ImageIcon("his1.jpg");
        JButton JBHistory = new JButton(icon3);

        ImageIcon icon4 = new ImageIcon("hp2.jpg");
        JButton JBHelp = new JButton(icon4);


        JTabbedPane tabbedPane = new JTabbedPane() {
            @Override
            public void updateUI() {
                setUI(new SquareTabUI(this));
            }
        };


        JPanel homePanel = new JPanel(null);

	ImageIcon icon = new ImageIcon("Homedisplaynew2.jpg");
	JLabel homeLabel = new JLabel(icon) {

    	@Override
	    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Welcome to Auto Backup Sysem", 1, 40);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Refer the instructions in Help menu.", 1, 550);
        g2d.drawString("Go through the Buttons / Tab Menus to continue...", 1, 590);
	    }
	};
        homeLabel.setBounds(180, 0, 900, 600);
	homeLabel.setOpaque(true);
	homePanel.add(homeLabel);
	homePanel.setBackground(new Color(30,60,90));
	homeLabel.setBackground(new Color(30,60,90));

        JPanel backupPanel = createBackupPanel();

	JPanel cloudPanel = placeholderPanel("History page");


            JTable table = new JTable();
            DefaultTableModel model = new DefaultTableModel(new String[]{"File Path", "File Name", "Last Modified Time"}, 0);
            table.setModel(model);
	table.getColumnModel().getColumn(0).setPreferredWidth(700);
	table.getColumnModel().getColumn(1).setPreferredWidth(360);
	table.getColumnModel().getColumn(2).setPreferredWidth(130);

	table.setShowGrid(false);
	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            try (BufferedReader br = new BufferedReader(new FileReader("folder_contents.csv"))) {
                String line;
                boolean firstLine = true;
                while ((line = br.readLine()) != null) {
                    if (firstLine) { 
                        firstLine = false;
                        continue;
                    }
                    String[] values = line.split(",");
                    if (values.length >= 3) {
                        String fp = values[0].trim();
                        String fn = values[1].trim();
                        String ft = values[2].trim();
                        model.addRow(new Object[]{fp, fn, ft});
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Failed to load CSV file.");
            }


	DefaultTableCellRenderer stripedRenderer = new DefaultTableCellRenderer() {
	    @Override
	    public Component getTableCellRendererComponent(JTable table,
                                                  Object value,
                                                  boolean isSelected,
                                                  boolean hasFocus,
                                                  int row,
                                                  int column) {
        Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
        if (isSelected) {
            c.setBackground(table.getSelectionBackground());
        } else {
            if (row % 2 == 0) {
                c.setBackground(Color.WHITE);
            } else {
                c.setBackground(new Color(230, 230, 250)); 
            	}
        }
        return c;
	    }
	};

	for (int i = 0; i < table.getColumnCount(); i++) {
	    table.getColumnModel().getColumn(i).setCellRenderer(stripedRenderer);
	}

	JScrollPane scrollPane = new JScrollPane(table);
	cloudPanel.setLayout(new BorderLayout()); 
	cloudPanel.add(scrollPane, BorderLayout.CENTER);

	JPanel helpPanel = new JPanel(null);
	JLabel helpLabel = new JLabel()	{

    	@Override
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Instructions :", 1, 60);
	g2d.drawLine(1,62,190,62);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Key Instructions: ", 1, 100);
	g2d.drawLine(1,102,150,102);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.drawString("Press the mouse on the Buttons or Tab in the above Tool Bar to open & Operate the Menus ", 1, 130);

        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("Backup Menu :", 1, 190);
	g2d.drawLine(1,192,130,192);

        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.drawString("In Backup Schedule you can choose any one of the option in three radio buttons like Daily or Weekly or Custom.", 1, 230);
        g2d.drawString("Also you can set the Backup time wise by clicking Advanced Schedule Button to set the backup timing.", 1, 270);
        g2d.drawString("After that you should choose the Backup Destination by clikcing the dropdown box like Google Drive / Local Server.", 1, 310);
        g2d.drawString("Then Select Folder for which folderised files you want get backup by clicking browse Button.", 1, 350);
        g2d.drawString("After this setup the backup process will be stared and done automatically as per the time setting for Files in the Choosed Folder.", 1, 390);

        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("History Menu :", 1, 450);
	g2d.drawLine(1,452,130,452);

        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.drawString("By clicking the History Menu Tab or Button, you can view the backup file and path details for which you have already selected", 1, 492); 
	g2d.drawString("to get Backup.", 1, 522);

		}

	};
	
        helpLabel.setBounds(180, 0, 900, 600);
	helpLabel.setOpaque(true);
	helpPanel.add(helpLabel);
	helpPanel.setBackground(new Color(30,60,90));
	helpLabel.setBackground(new Color(30,60,90));

        tabbedPane.addTab("Home", homePanel);
        tabbedPane.addTab("Backup", backupPanel);
        tabbedPane.addTab("History", cloudPanel);
        tabbedPane.addTab("Help", helpPanel);

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = tabbedPane.getSelectedIndex();
                String title = tabbedPane.getTitleAt(selectedIndex);
                System.out.println("Switched to tab: " + title);
            }
        });
	JBHome.setBounds(5,3,40,40);
	JBBackup.setBounds(46,3,40,40);
	JBHistory.setBounds(87,3,40,40);
	JBHelp.setBounds(128,3,40,40);

    	Taskbarpanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
	Taskbarpanel.setBounds(0,0,1365,50);
	Taskbarpanel.setOpaque(true);
	Taskbarpanel.add(JBHome);
	Taskbarpanel.add(JBBackup);
	Taskbarpanel.add(JBHistory);
	Taskbarpanel.add(JBHelp);

	tabbedPane.setBounds(0,51,1200,650);
	homePanel.setBounds(0,0,1100,700);
	backupPanel.setBounds(100,130,1000,600);
	frame.add(Taskbarpanel);
        frame.add(tabbedPane);
        frame.setVisible(true);

	JBHome.setToolTipText("Home Menu");
	JBBackup.setToolTipText("Backup Menu");
	JBHistory.setToolTipText("History Menu");
	JBHelp.setToolTipText("Help Menu");

	JBHome.addActionListener(e -> {
 	tabbedPane.setSelectedIndex(0); 
	});
	JBBackup.addActionListener(e -> {
 	tabbedPane.setSelectedIndex(1); 
	});
	JBHistory.addActionListener(e -> {
 	tabbedPane.setSelectedIndex(2); 
	});

	JBHelp.addActionListener(e -> {
 	tabbedPane.setSelectedIndex(3); 
	});

    }

    private JPanel createBackupPanel() {

    	JPanel panel = new JPanel(null);
   	panel.setBounds(100, 130, 1000, 600);

    	Color primaryBlue = new Color(0, 120, 215);
    	Color successGreen = new Color(0, 153, 76);

    	// ================= BACKUP SCHEDULE PANEL =================
    	JPanel backupSchedulePanel = new JPanel(null);
    	backupSchedulePanel.setBounds(180, 40, 800, 130);
    	backupSchedulePanel.setBackground(Color.WHITE);
    	backupSchedulePanel.setBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY)
    	);

    	JLabel scheduleLabel = new JLabel("Backup Schedule");
    	scheduleLabel.setBounds(20, 10, 300, 30);
    	scheduleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    	backupSchedulePanel.add(scheduleLabel);

    	dailyRadio = new JRadioButton("Daily");
    	dailyRadio.setBounds(30, 60, 80, 25);
    	dailyRadio.setBackground(Color.WHITE);

        weeklyRadio = new JRadioButton("Weekly");
    	weeklyRadio.setBounds(130, 60, 80, 25);
    	weeklyRadio.setBackground(Color.WHITE);

        customRadio = new JRadioButton("Custom");
    	customRadio.setBounds(230, 60, 80, 25);
    	customRadio.setBackground(Color.WHITE);

   	ButtonGroup scheduleGroup = new ButtonGroup();
    	scheduleGroup.add(dailyRadio);
    	scheduleGroup.add(weeklyRadio);
    	scheduleGroup.add(customRadio);

    	ImageIcon icon5 = new ImageIcon("File Schedule.jpg");
    	JButton advBtn = new JButton("Advanced Schedule", icon5);
    	advBtn.setBounds(500, 55, 240, 40);
    	advBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
    	advBtn.setBackground(primaryBlue);
    	advBtn.setForeground(Color.WHITE);
    	advBtn.setOpaque(true);
    	advBtn.setContentAreaFilled(true);
    	advBtn.setFocusPainted(false);
    	advBtn.setBorder(BorderFactory.createLineBorder(primaryBlue, 2));

    	backupSchedulePanel.add(dailyRadio);
    	backupSchedulePanel.add(weeklyRadio);
    	backupSchedulePanel.add(customRadio);
    	backupSchedulePanel.add(advBtn);

    	panel.add(backupSchedulePanel);

    	// ================= BACKUP DESTINATION PANEL =================
    	JPanel backupdesPanel = new JPanel(null);
    	backupdesPanel.setBounds(180, 200, 800, 130);
    	backupdesPanel.setBackground(Color.WHITE);
    	backupdesPanel.setBorder(
            	BorderFactory.createLineBorder(Color.LIGHT_GRAY)
    	);

    	JLabel destLabel = new JLabel("Backup Destination");
    	destLabel.setBounds(20, 10, 200, 25);
    	destLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    	backupdesPanel.add(destLabel);
	
	ImageIcon driveIcon = new ImageIcon(
        getClass().getResource("/gdrive.png")
	);
	ImageIcon serverIcon = new ImageIcon(
        	getClass().getResource("/server.png")
	);

	// Resize icons (recommended)
	driveIcon = new ImageIcon(
        	driveIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)
	);
	serverIcon = new ImageIcon(
        	serverIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)
	);
	JComboBox<ComboItem> destinationCombo = new JComboBox<>();
	destinationCombo.addItem(new ComboItem("Google Drive", driveIcon));
	destinationCombo.addItem(new ComboItem("Local Server", serverIcon));
	
	destinationCombo.setRenderer(new DefaultListCellRenderer() {
    	@Override
    	public Component getListCellRendererComponent(
            	JList<?> list, Object value, int index,
            	boolean isSelected, boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

        if (value instanceof ComboItem) {
            ComboItem item = (ComboItem) value;
            label.setText(item.text);
            label.setIcon(item.icon);
            label.setIconTextGap(8);
        }

        return label;
    	}
     });

    	//JComboBox<String> destinationCombo =
            	//new JComboBox<>(new String[]{"Google Drive", "Local Server"});
    	destinationCombo.setBounds(30, 50, 150, 25);
    	backupdesPanel.add(destinationCombo);

    	JTextField destPathField =
            	new JTextField("Enter folder or server path");
    	destPathField.setBounds(200, 50, 350, 25);
    	backupdesPanel.add(destPathField);

    	panel.add(backupdesPanel);

    	advBtn.addActionListener(e -> openAdvancedSchedule());

    	// ================= BACKUP SOURCE PANEL =================
    	JPanel backupSrcPanel = new JPanel(null);
    	backupSrcPanel.setBounds(180, 360, 800, 130);
    	backupSrcPanel.setBackground(Color.WHITE);
    	backupSrcPanel.setBorder(
            	BorderFactory.createLineBorder(Color.LIGHT_GRAY)
    	);

    	JLabel srcLabel = new JLabel("Backup Source");
    	srcLabel.setBounds(20, 10, 200, 25);
    	srcLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    	backupSrcPanel.add(srcLabel);

    	JLabel folderLabel = new JLabel("Select Folder:");
    	folderLabel.setBounds(30, 50, 100, 25);
   	 backupSrcPanel.add(folderLabel);

    	JTextField folderField = new JTextField();
    	folderField.setBounds(140, 50, 300, 25);
    	backupSrcPanel.add(folderField);

    	ImageIcon icon6 = new ImageIcon("Open5.jpg");
    	JButton browseButton = new JButton("Browse", icon6);
    	browseButton.setBounds(460, 45, 120, 35);
    	browseButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
    	browseButton.setBackground(primaryBlue);
    	browseButton.setForeground(Color.WHITE);
    	browseButton.setOpaque(true);
    	browseButton.setContentAreaFilled(true);
    	browseButton.setFocusPainted(false);
    	browseButton.setBorder(BorderFactory.createLineBorder(primaryBlue, 2));

    	backupSrcPanel.add(browseButton);
    	panel.add(backupSrcPanel);

    	// ================= ICON LOADING =================
    	ImageIcon icon7 = new ImageIcon(getClass().getResource("/search.png"));
    	ImageIcon icon8 = new ImageIcon(getClass().getResource("/schedule.png"));
    	ImageIcon icon9 = new ImageIcon(getClass().getResource("/upload.png"));

    	Image img = icon7.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
    	Image img1 = icon8.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
    	Image img4 = icon9.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);

    	// ================= ACTION BUTTONS =================
    	JButton previewButton = new JButton("Preview", new ImageIcon(img));
    	previewButton.setBounds(180, 520, 120, 45);
    	previewButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
    	previewButton.setBackground(Color.WHITE);
    	previewButton.setForeground(Color.BLACK);
    	previewButton.setOpaque(true);
    	previewButton.setContentAreaFilled(true);
    	previewButton.setFocusPainted(false);
    	//previewButton.setBorder(BorderFactory.createLineBorder(primaryBlue, 2));

    	JButton saveButton = new JButton("Save Schedule", new ImageIcon(img1));
    	saveButton.setBounds(320, 520, 160, 45);
    	saveButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
    	saveButton.setBackground(Color.WHITE);
    	saveButton.setForeground(Color.BLACK);
    	saveButton.setOpaque(true);
    	saveButton.setContentAreaFilled(true);
    	saveButton.setFocusPainted(false);
    	//saveButton.setBorder(BorderFactory.createLineBorder(primaryBlue, 2));
	// Soft Windows light blue
	Color lightBlue2 = new Color(70, 130, 190);

    	JButton startButton = new JButton("Start Backup Now", new ImageIcon(img4));
    	startButton.setBounds(820, 520, 170, 45);
    	startButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
    	startButton.setBackground(lightBlue2);
    	startButton.setForeground(Color.WHITE);
    	startButton.setOpaque(true);
    	startButton.setContentAreaFilled(true);
    	startButton.setFocusPainted(false);
    	//startButton.setBorder(BorderFactory.createLineBorder(successGreen, 2));

    	panel.add(previewButton);
    	panel.add(saveButton);
    	panel.add(startButton);

            browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int option = chooser.showOpenDialog(panel);
            if (option == JFileChooser.APPROVE_OPTION) {
                folderField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        saveButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Schedules already saved via Advanced Schedule dialog.\nCurrent saved schedules:\n" + savedSchedules);
        });

        startButton.addActionListener(e -> {
            String folderToBackup = folderField.getText().trim();
            if (folderToBackup.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please select a folder to backup (use Browse).");
                return;
            }
            if (savedSchedules.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No schedules found. Use Advanced Schedule to add times.");
                return;
            }

            if (backupThread != null && backupThread.isAlive()) {
                backupThread.interrupt();
            }

            List<ScheduleItem> items = parseSavedSchedules(savedSchedules);

            backupThread = new Thread(() -> runScheduler(folderToBackup, items));
            backupThread.setDaemon(true);
            backupThread.start();
            System.out.println("Scheduler started. Waiting for scheduled times...");
	    logFolderContentsToCSV(folderField.getText(), "folder_contents.csv");

        });

        return panel;
    }

    private void openAdvancedSchedule() {
        JDialog dialog = new JDialog(frame, "Advanced Schedule", true);
        dialog.setSize(500, 400);
        dialog.setLayout(new BorderLayout());

        SpinnerDateModel timeModel = new SpinnerDateModel();
        timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);

        mon = new JCheckBox("Mon");
        tue = new JCheckBox("Tue");
        wed = new JCheckBox("Wed");
        thu = new JCheckBox("Thu");
        fri = new JCheckBox("Fri");
        sat = new JCheckBox("Sat");
        sun = new JCheckBox("Sun");

        intervalSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 24, 1));
        intervalUnit = new JComboBox<>(new String[]{"Hours", "Minutes"});

        dynamicPanel = new JPanel(new BorderLayout());
        updateDynamicPanel();

        ActionListener updateListener = e -> updateDynamicPanel();
        dailyRadio.addActionListener(updateListener);
        weeklyRadio.addActionListener(updateListener);
        customRadio.addActionListener(updateListener);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String s : savedSchedules) listModel.addElement(s);
        JList<String> scheduleList = new JList<>(listModel);

        JButton addBtn = new JButton("Add Schedule");
        JButton removeBtn = new JButton("Remove Selected");
        JButton saveBtn = new JButton("Save & Close");

        addBtn.addActionListener(e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm"); 
            String time = sdf.format((Date) timeSpinner.getValue());
            String schedule = "";

            if (dailyRadio.isSelected()) {
                schedule = "Daily at " + time;
            } else if (weeklyRadio.isSelected()) {
                StringBuilder days = new StringBuilder();
                if (mon.isSelected()) days.append("Mon ");
                if (tue.isSelected()) days.append("Tue ");
                if (wed.isSelected()) days.append("Wed ");
                if (thu.isSelected()) days.append("Thu ");
                if (fri.isSelected()) days.append("Fri ");
                if (sat.isSelected()) days.append("Sat ");
                if (sun.isSelected()) days.append("Sun ");
                if (days.length() == 0) days.append("No day selected");
                schedule = "Weekly at " + time + " on " + days.toString().trim();
            } else if (customRadio.isSelected()) {
                int interval = (Integer) intervalSpinner.getValue();
                String unit = (String) intervalUnit.getSelectedItem();
                schedule = "Custom: Every " + interval + " " + unit + " starting at " + time;
            } else {
                schedule = "Daily at " + time;
            }

            listModel.addElement(schedule);
        });

        removeBtn.addActionListener(e -> {
            int index = scheduleList.getSelectedIndex();
            if (index != -1) listModel.remove(index);
        });

        saveBtn.addActionListener(e -> {
            savedSchedules.clear();
            for (int i = 0; i < listModel.size(); i++) {
                savedSchedules.add(listModel.getElementAt(i));
            }
            JOptionPane.showMessageDialog(dialog, "Saved Schedules:\n" + savedSchedules);
            dialog.dispose();
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(addBtn);
        btnPanel.add(removeBtn);
        btnPanel.add(saveBtn);

        dialog.add(dynamicPanel, BorderLayout.NORTH);
        dialog.add(new JScrollPane(scheduleList), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void updateDynamicPanel() {
        dynamicPanel.removeAll();

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Select Time:"), BorderLayout.WEST);
        topPanel.add(timeSpinner, BorderLayout.CENTER);
        dynamicPanel.add(topPanel, BorderLayout.NORTH);

        if (weeklyRadio.isSelected()) {
            JPanel daysPanel = new JPanel(new GridLayout(1, 7));
            daysPanel.add(mon);
            daysPanel.add(tue);
            daysPanel.add(wed);
            daysPanel.add(thu);
            daysPanel.add(fri);
            daysPanel.add(sat);
            daysPanel.add(sun);
            dynamicPanel.add(daysPanel, BorderLayout.SOUTH);
        } else if (customRadio.isSelected()) {
            JPanel intervalPanel = new JPanel();
            intervalPanel.add(new JLabel("Run every: "));
            intervalPanel.add(intervalSpinner);
            intervalPanel.add(intervalUnit);
            dynamicPanel.add(intervalPanel, BorderLayout.SOUTH);
        }

        dynamicPanel.revalidate();
        dynamicPanel.repaint();
    }

    private JPanel placeholderPanel(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel();
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private void displayFiles(String path) {
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            welcomeLabel.setVisible(false);
            fileArea.setText("");
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        fileArea.append("[Folder] " + f.getName() + "\n");
                    } else {
                        fileArea.append("[File] " + f.getName() + "\n");
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid path! Please try again.");
        }
    }

    private static class ScheduleItem {
        enum Type { DAILY, WEEKLY, CUSTOM }
        Type type;
        LocalTime time; 
        Set<DayOfWeek> days; 
        int intervalMinutes; 

        ScheduleItem(Type type) {
            this.type = type;
            this.days = new HashSet<>();
            this.intervalMinutes = 0;
        }
    }

    private List<ScheduleItem> parseSavedSchedules(List<String> savedSchedules) {
        List<ScheduleItem> items = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");

        Pattern timePattern = Pattern.compile("(\\d{2}:\\d{2})");
        for (String s : savedSchedules) {
            try {
                if (s.startsWith("Daily")) {
                    Matcher m = timePattern.matcher(s);
                    if (m.find()) {
                        ScheduleItem it = new ScheduleItem(ScheduleItem.Type.DAILY);
                        it.time = LocalTime.parse(m.group(1), dtf);
                        items.add(it);
                    }
                } else if (s.startsWith("Weekly")) {
                    Matcher m = timePattern.matcher(s);
                    if (m.find()) {
                        ScheduleItem it = new ScheduleItem(ScheduleItem.Type.WEEKLY);
                        it.time = LocalTime.parse(m.group(1), dtf);
                        int onIndex = s.indexOf("on ");
                        if (onIndex != -1) {
                            String dayPart = s.substring(onIndex + 3).trim();
                            String[] tokens = dayPart.split("\\s+");
                            for (String t : tokens) {
                                DayOfWeek dow = mapShortDayToDayOfWeek(t);
                                if (dow != null) it.days.add(dow);
                            }
                        }
                        items.add(it);
                    }
                } else if (s.startsWith("Custom")) {
                    Matcher m = timePattern.matcher(s);
                    Matcher numMatcher = Pattern.compile("Every\\s+(\\d+)").matcher(s);
                    Matcher unitMatcher = Pattern.compile("Every\\s+\\d+\\s+(Hours|Minutes)", Pattern.CASE_INSENSITIVE).matcher(s);
                    if (m.find() && numMatcher.find()) {
                        ScheduleItem it = new ScheduleItem(ScheduleItem.Type.CUSTOM);
                        it.time = LocalTime.parse(m.group(1), dtf);
                        int num = Integer.parseInt(numMatcher.group(1));
                        if (unitMatcher.find()) {
                            String unit = unitMatcher.group(1).toLowerCase();
                            it.intervalMinutes = unit.startsWith("hour") ? num * 60 : num;
                        } else {
                            it.intervalMinutes = num * 60;
                        }
                        items.add(it);
                    }
                } else {
                    Matcher m = timePattern.matcher(s);
                    if (m.find()) {
                        ScheduleItem it = new ScheduleItem(ScheduleItem.Type.DAILY);
                        it.time = LocalTime.parse(m.group(1), dtf);
                        items.add(it);
                    }
                }
            } catch (Exception ex) {
                System.out.println("Failed to parse schedule entry: " + s + "  -> " + ex.getMessage());
            }
        }

        return items;
    }

    private static DayOfWeek mapShortDayToDayOfWeek(String shortDay) {
        shortDay = shortDay.trim().toLowerCase();
        switch (shortDay) {
            case "mon": return DayOfWeek.MONDAY;
            case "tue": return DayOfWeek.TUESDAY;
            case "wed": return DayOfWeek.WEDNESDAY;
            case "thu": return DayOfWeek.THURSDAY;
            case "fri": return DayOfWeek.FRIDAY;
            case "sat": return DayOfWeek.SATURDAY;
            case "sun": return DayOfWeek.SUNDAY;
            default: return null;
        }
    }

    private void runScheduler(String folderPath, List<ScheduleItem> items) {
        Set<String> executedRecords = Collections.synchronizedSet(new HashSet<>());

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                LocalDateTime now = LocalDateTime.now();
                LocalTime nowTime = now.withSecond(0).withNano(0).toLocalTime();
                DayOfWeek today = now.getDayOfWeek();

                for (ScheduleItem it : items) {
                    String recordKey = null;
                    boolean shouldTrigger = false;

                    if (it.type == ScheduleItem.Type.DAILY) {
                        if (nowTime.equals(it.time)) {
                            recordKey = "DAILY:" + it.time.toString() + ":" + now.toLocalDate().toString();
                            shouldTrigger = true;
                        }
                    } else if (it.type == ScheduleItem.Type.WEEKLY) {
                        if (!it.days.isEmpty() && it.days.contains(today) && nowTime.equals(it.time)) {
                            recordKey = "WEEKLY:" + it.time.toString() + ":" + now.toLocalDate().toString();
                            shouldTrigger = true;
                        }
                    } else if (it.type == ScheduleItem.Type.CUSTOM) {
                        if (it.intervalMinutes > 0) {
                            LocalDateTime startDateTime = LocalDateTime.of(now.toLocalDate(), it.time);
                            long minutesSinceStart = Duration.between(startDateTime, now).toMinutes();
                            if (minutesSinceStart >= 0 && minutesSinceStart % it.intervalMinutes == 0
                                    && nowTime.equals(now.toLocalTime().withSecond(0).withNano(0))) {
                                recordKey = "CUSTOM:" + it.time.toString() + ":" + now.toLocalDate().toString() + ":" + (minutesSinceStart / it.intervalMinutes);
                                shouldTrigger = true;
                            }
                        } else {
                            if (nowTime.equals(it.time)) {
                                recordKey = "CUSTOM_ONCE:" + it.time.toString() + ":" + now.toLocalDate().toString();
                                shouldTrigger = true;
                            }
                        }
                    }

                    if (shouldTrigger && recordKey != null && !executedRecords.contains(recordKey)) {
                        executedRecords.add(recordKey);
                        final String completionMsg = "Backup completed at " + now.format(timeFormatter);
                        final LocalDateTime startedAt = now;
                        new Thread(() -> {
                            System.out.println("Backup started at " + startedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                            runBackup(folderPath, completionMsg);
                        }).start();
                    }
                }

                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                System.out.println("Scheduler interrupted/stopped.");
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void runBackup(String folderPath, String completionMessage) {
        try {
            System.out.println("Starting backup for folder: " + folderPath);

            String command = "rclone.exe copy --update \"" + folderPath + "\" gdrive:BackupFolder";

            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            try (BufferedWriter logWriter = new BufferedWriter(new FileWriter("backup_log.txt", true))) {
                logWriter.write("\n=== Backup Run: " + java.time.LocalDateTime.now() + " ===\n");

                String line;
                while ((line = reader.readLine()) != null) {
                    logWriter.write(line + "\n");
                }
                while ((line = errorReader.readLine()) != null) {
                    logWriter.write("ERROR: " + line + "\n");
                }

                logWriter.write(completionMessage + "\n");
            }

            process.waitFor();

            System.out.println(completionMessage + "! Check backup_log.txt for details.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class SquareTabUI extends BasicTabbedPaneUI {
        private final JTabbedPane tabbedPane;
        public SquareTabUI(JTabbedPane tabbedPane) {
            this.tabbedPane = tabbedPane;
        }
        @Override
        protected void installDefaults() {
            super.installDefaults();
            tabAreaInsets = new Insets(0, 0, 0, 0);
        }
        protected void paintTabBackground(Graphics g, int tabPlacement, Rectangle rect,
                                          int tabIndex, boolean isSelected) {
            g.setColor(isSelected ? Color.GRAY : Color.LIGHT_GRAY);
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
        }
        protected void paintTabBorder(Graphics g, int tabPlacement, Rectangle rect,
                                      int tabIndex, boolean isSelected) {
            g.setColor(Color.BLACK);
            g.drawRect(rect.x, rect.y, rect.width, rect.height);
        }
        @Override
        protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics,
                                 int tabIndex, String title, Rectangle textRect, boolean isSelected) {
            g.setColor(Color.BLACK);
            g.setFont(font);
            int x = textRect.x + (textRect.width - metrics.stringWidth(title)) / 2;
            int y = textRect.y + ((textRect.height - metrics.getHeight()) / 2) + metrics.getAscent();
            g.drawString(title, x, y);
        }
        @Override
        protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex,
                                Rectangle iconRect, Rectangle textRect) {
            Rectangle tabRect = rects[tabIndex];
            paintTabBackground(g, tabPlacement, tabRect, tabIndex,
                    tabbedPane.getSelectedIndex() == tabIndex);
            paintTabBorder(g, tabPlacement, tabRect, tabIndex,
                    tabbedPane.getSelectedIndex() == tabIndex);
            String title = tabbedPane.getTitleAt(tabIndex);
            Font font = tabbedPane.getFont();
            FontMetrics metrics = g.getFontMetrics(font);
            Rectangle textR = new Rectangle(
                    tabRect.x + 5, tabRect.y + 5,
                    tabRect.width - 10, tabRect.height - 10
            );
            paintText(g, tabPlacement, font, metrics, tabIndex, title, textR,
                    tabbedPane.getSelectedIndex() == tabIndex);
        }
    }

private static void logFolderContentsToCSV(String folderPath, String csvFilePath) {
    try {
        Path dir = Paths.get(folderPath);
        File csvFile = new File(csvFilePath);
        boolean writeHeader = !csvFile.exists() || csvFile.length() == 0; 

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath, true))) {
            if (writeHeader) {
                writer.write("File Path,File Name,Last Modified Time");
                writer.newLine();
            }

            DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (Path filePath : stream) {
                if (Files.isRegularFile(filePath)) {
                    BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
                    String fullPath = filePath.toAbsolutePath().toString();
                    String fileName = filePath.getFileName().toString();
                    String lastModifiedTime = formatter.format(attrs.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault()));

                    String csvLine = String.format("\"%s\",\"%s\",\"%s\"", fullPath, fileName, lastModifiedTime);
                    writer.write(csvLine);
                    writer.newLine();
                }
            }
        }
        System.out.println("Folder contents appended to " + csvFilePath);
    } catch (IOException e) {
        e.printStackTrace();
    }
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(FileBackupSquareTabs::new);

    }
}
