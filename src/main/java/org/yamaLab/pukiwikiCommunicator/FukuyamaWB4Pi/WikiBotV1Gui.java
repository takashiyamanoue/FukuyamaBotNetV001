
package org.yamaLab.pukiwikiCommunicator.FukuyamaWB4Pi;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.TableView.TableRow;

import org.yamaLab.TwitterConnector.Util;
import org.yamaLab.pukiwikiCommunicator.CommandReceiver;
import org.yamaLab.pukiwikiCommunicator.MainController;
import org.yamaLab.pukiwikiCommunicator.SBUtil;
import org.yamaLab.pukiwikiCommunicator.DGA.DGA;
import org.yamaLab.pukiwikiCommunicator.UdpP2P.UdpP2P;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JPasswordField;
/* 
command: tweet "#test hello!" when "*,*,*,*,5,*"
 
 */
public class WikiBotV1Gui extends JFrame implements CommandReceiver, ClassWithJTable
{
	private final JTabbedPane mainTabPane ;	
	App app;
	JTextArea messageArea;
	JTextArea tweetTextArea ;
	public Properties setting;
	String settingFileName="FukuyamaWB4Pi.properties";
	MainController mainController;
	
	private JLabel urlLabel;
	private JLabel secondaryUrlLabel;
	private JButton disConnectButton;
	private JButton clearCommandButton;
	private JScrollPane commandAreaPane;
	private JLabel resultLabel;
//	private JComboBox readIntervalCombo;
//	private JComboBox execIntervalCombo;
//	private JComboBox returnIntervalCombo;
	private JTextField readIntervalField;
	private JTextField execIntervalField;
	private JTextField sendIntervalField;
	private JLabel commandIntervalLabel;
	private JLabel execIntervalLabel;
	private JLabel sendIntervalLabel;
	private JLabel pukiwikiMessageLabel;
	private JTextArea resultArea;
	private JScrollPane resultPane;
	private JScrollPane messageAreaScrollPane;
	private JTextArea pukiwikiMessageArea;
	private JTextArea commandArea;
	private JLabel commandLabel;
	public JToggleButton wikiConnectButton;
	private JTextField wikiUrlTextField;
	private JTextField wikiSecondaryUrlTextField;
    private JRadioButton showDebuggerButton;
    public JCheckBox onlineCommandRefreshButton;
	private JCheckBox traceCheckBox;
    public JToggleButton startWatchingButton;
    private JButton endWatchingButton;
	private JTable commandTable;
	private JLabel deviceIDLabel;
	private JTextField deviceIDField;
	private JLabel timeLabel;
	private JTextField timeField;
	private JLabel controlLabel;
	private JTextArea controlArea;
	private JTextArea errorArea;
	private JTextField maxComField;
	private JButton setMaxCommandButton;
	
	private JTable oTable;
	private JScrollPane idListAreaPane;
	private JLabel idListLabel;
	
	private UdpP2P udpP2P;
	private DGA dga;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WikiBotV1Gui frame = new WikiBotV1Gui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	

	/**
	 * Create the frame.
	 */
	public void init(){
		this.mainController=new MainController(this, this, setting);	
		traceCheckBox.setSelected(false);
		mainController.parseCommand("traceCheckBox", "false");			
		udpP2P=new UdpP2P(mainController);
		udpP2P.setGui(this.mainTabPane);
		udpP2P.setSetting(setting);
		udpP2P.putApplicationTable("service", mainController);
		mainController.putApplicationTable("udpP2P", udpP2P);
		dga=new DGA(mainController);
		dga.setGui(this.mainTabPane);
	}
	
	public WikiBotV1Gui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 700);
		mainTabPane = new JTabbedPane();
		getContentPane().add(mainTabPane);
		mainTabPane.setBounds(3, 43, 800, 700);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				saveProperties();
			    System.exit(0);
			}
		});
		this.loadProperties();
//		vtraffic = new VisualTrf[256][256];
		
		initMainGui();
//  	initDeviceSettingGui();	
		initTwitterGui();
		twitterAuthSettingGui();
		initIDListGui();
		this.setProperties();	
		
		init();
	}

	public void initTwitterGui(){
		try{
		JPanel mainPanel= new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		mainPanel.setLayout(null);
		
	    mainPanel.setLayout(null);
		if(this.mainTabPane!=null) {
			this.mainTabPane.add("twitter",mainPanel);
		}
		else{
			mainPanel.setLayout(null);
			mainPanel.add(this);
		}
		{
			JButton btnConnectTwitter = new JButton("Connect Twitter");
			btnConnectTwitter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("Connect Twitter");
//					connectTwitter();
					mainController.parseCommand("twitterConnect", "");
				}
			});
			btnConnectTwitter.setBounds(6, 0, 165, 29);
			mainPanel.add(btnConnectTwitter);
			
		}
		JScrollPane tweetScrollPane = new JScrollPane();
		tweetScrollPane.setBounds(180, 0, 225, 42);
		mainPanel.add(tweetScrollPane);
		
		JButton btnTweet = new JButton("Tweet");
		btnTweet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				System.out.println("Tweet");
				String x=tweetTextArea.getText();
//				int rtn=tweet(x);
//				if(rtn==1){
				mainController.parseCommand("twitterTweet", x);
					tweetTextArea.setText("");
//				}
			}
		});
		btnTweet.setBounds(432, 0, 90, 42);
		mainPanel.add(btnTweet);
		
		tweetTextArea = new JTextArea();
		tweetScrollPane.setViewportView(tweetTextArea);
		

		
		JButton savePropertiesButton = new JButton("SaveProperties");
		savePropertiesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Save Properties");
//				connectTwitter();
				reflectProperties();
				saveProperties();
			}
		});
		savePropertiesButton.setBounds(530, 0, 165, 29);
		mainPanel.add(savePropertiesButton);
		
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
//			this.setTitle("PukiwikiCommunicator");
		
	}

	public void initIDListGui(){
		JPanel idListPanel= new JPanel();
		idListPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		idListPanel.setLayout(null);
		mainTabPane.add("idListPanel",idListPanel);
        int x1=1;
        int x2=95;
		

		{
			int h=30;
			idListLabel = new JLabel();
			idListPanel.add(idListLabel);
			idListLabel.setText("url-id list:");
			idListLabel.setBounds(x1, h, 109, 33);
			idListAreaPane = new JScrollPane();
			idListPanel.add(idListAreaPane);
			idListAreaPane.setBounds(x2, h, 550, 200);
			{
				/*
				commandArea = new JTextArea();
				commandAreaPane.setViewportView(commandArea);
				*/
				initIdListTable(maxID);
			}
		}


		JButton savePropertiesButton = new JButton("SaveProperties");
		savePropertiesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Save Properties");
//				connectTwitter();
				reflectProperties();
				saveProperties();
			}
		});
		savePropertiesButton.setBounds(530, 0, 165, 29);
		idListPanel.add(savePropertiesButton);
		
		this.setVisible(false);
//		this.setSize(804, 700);
		
	}


	public void initMainGui(){
		JPanel mainPanel= new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		mainPanel.setLayout(null);
		mainTabPane.add("mainPanel",mainPanel);
        int x1=1;
        int x2=95;
		
		{
			int h=5;
			deviceIDLabel = new JLabel();
			mainPanel.add(deviceIDLabel);
			deviceIDLabel.setText("device ID:");
			deviceIDLabel.setBounds(x1, h, 80, 30);
			deviceIDField = new JTextField();
			mainPanel.add(deviceIDField);
			deviceIDField.setBounds(x2, h, 150, 30);
		    JButton setDeviceIDButton = new JButton("set");
		    setDeviceIDButton.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) {
				 System.out.println("set device ID");
				 reflectProperties();
				 saveProperties();
			  }
		    });
		    setDeviceIDButton.setBounds(250, h, 80, 30);
		    mainPanel.add(setDeviceIDButton);
			timeLabel = new JLabel();
			mainPanel.add(timeLabel);
			timeLabel.setText("Time:");
			timeLabel.setBounds(340, h, 80, 30);
			timeField = new JTextField();
			mainPanel.add(timeField);
			timeField.setBounds(420, h, 100, 30);
			
		    JButton exitButton = new JButton("exit");
		    exitButton.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) {
				 exitSystem();
			  }
		    });
		    exitButton.setBounds(700, h, 80, 30);
		    mainPanel.add(exitButton);
			
		}
		{
		   int h=170;
		   JLabel lblCommand = new JLabel("exec com:");
		   lblCommand.setBounds(x1, h, 150, 24);
		   mainPanel.add(lblCommand);
		
		   JScrollPane commandScrollPane = new JScrollPane();
		   commandScrollPane.setBounds(x2, h, 250, 40);
		   mainPanel.add(commandScrollPane);
		
		   JTextArea commandTextArea = new JTextArea();
		   commandScrollPane.setViewportView(commandTextArea);
		
		   JButton btnEnter = new JButton("enter");
		   btnEnter.setBounds(350, h, 90, 40);
		   mainPanel.add(btnEnter);
		   btnEnter.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				System.out.println("enter");
			 }
		   });
			traceCheckBox = new JCheckBox();
			mainPanel.add(traceCheckBox);
			traceCheckBox.setText("trace");
			traceCheckBox.setBounds(629, h, 135, 25);
			traceCheckBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
//					onlineCommandRefreshButtonActionPerformed(evt);
					mainController.parseCommand("traceCheckBox", ""+(traceCheckBox.isSelected()));
				}
			});	
		}
		
//		tweetTextArea.setBounds(108, 219, -57, -32);
//		contentPane.add(tweetTextArea);
//  ---------------------------------------
//
		{
			int h=40;
			urlLabel = new JLabel();
			mainPanel.add(urlLabel);
			urlLabel.setText("manager url:");
			urlLabel.setBounds(x1, h, 105, 24);
			wikiUrlTextField = new JTextField();
			mainPanel.add(wikiUrlTextField);
			wikiUrlTextField.setBounds(x2, h, 446, 30);

			disConnectButton = new JButton();
			mainPanel.add(disConnectButton);
			disConnectButton.setText("Disconnect");
			disConnectButton.setBounds(651, h, 120, 30);
			disConnectButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					disConnectButtonActionPerformed(evt);
				}
			});
		}
		
		{
			int h=90;
			secondaryUrlLabel = new JLabel();
			mainPanel.add(secondaryUrlLabel);
			secondaryUrlLabel.setText("2nd. url:");
			secondaryUrlLabel.setBounds(x1, h, 105, 24);
			wikiSecondaryUrlTextField = new JTextField();
			mainPanel.add(wikiSecondaryUrlTextField);
			wikiSecondaryUrlTextField.setBounds(x2, h, 446, 30);
			showDebuggerButton = new JRadioButton();
			mainPanel.add(showDebuggerButton);
			showDebuggerButton.setText("show debugger");
			showDebuggerButton.setBounds(540, h, 150, 30);
			showDebuggerButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
//					debuggerButtonActionPerformed(evt);
					mainController.parseCommand("wikiDebuggerButton Clicked", "");
				}
			});

		}
		
		{
			int h=210;
			commandLabel = new JLabel();
			mainPanel.add(commandLabel);
			commandLabel.setText("command list:");
			commandLabel.setBounds(x1, h, 109, 33);
			commandAreaPane = new JScrollPane();
			mainPanel.add(commandAreaPane);
			commandAreaPane.setBounds(x2, h, 550, 200);
			{
				/*
				commandArea = new JTextArea();
				commandAreaPane.setViewportView(commandArea);
				*/
				initCommandTable(maxCommands);
			}
		}
		{
			int h=410;
			resultLabel = new JLabel();
		    mainPanel.add(resultLabel);
			resultLabel.setText("result:");
			resultLabel.setBounds(x1, h, 105, 29);
			resultPane = new JScrollPane();
			mainPanel.add(resultPane);
			resultPane.setBounds(x2, h, 550, 80);
			{
				resultArea = new JTextArea();
				resultPane.setViewportView(resultArea);
//				resultArea.setPreferredSize(new java.awt.Dimension(547, 79));
			}
		}

		{		
//			int h=540;
			int h=500;
		    messageAreaScrollPane = new JScrollPane();
		    messageAreaScrollPane.setBounds(x2, h, 550, 120);
		    mainPanel.add(messageAreaScrollPane);
		
		    messageArea = new JTextArea();
		    messageAreaScrollPane.setViewportView(messageArea);		
		
		    JLabel lblMessage = new JLabel("Message:");
		    lblMessage.setBounds(x1, h, 101, 16);
		    mainPanel.add(lblMessage);
		}
		
		{
			int h=142;
			commandIntervalLabel = new JLabel();
			mainPanel.add(commandIntervalLabel);
			commandIntervalLabel.setText("read interval:");
			commandIntervalLabel.setBounds(x1, h, 110, 29);
			readIntervalField=new JTextField();
			mainPanel.add(readIntervalField);
			readIntervalField.setText("60000");
			readIntervalField.setBounds(x2, h, 100, 29);
			/*
			String[] interval={"10-sec","30-sec","1-min","5-min","10-min","30-min","1-h","12-h","24-h",""};
			readIntervalCombo = new JComboBox(interval);
			mainPanel.add(readIntervalCombo);
			readIntervalCombo.setBounds(x2, h, 100, 24);
			readIntervalCombo.setSelectedIndex(0);
			readIntervalCombo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					readIntervalComboActionPerformed(evt);
				}
			});
			*/
			execIntervalLabel = new JLabel();
			mainPanel.add(execIntervalLabel);
			execIntervalLabel.setText("exec interval:");
			execIntervalLabel.setBounds(210, h, 100, 29);
			execIntervalField=new JTextField();
			mainPanel.add(execIntervalField);
			execIntervalField.setText("60000");
			execIntervalField.setBounds(310, h, 100, 29);

			sendIntervalLabel = new JLabel();
			mainPanel.add(sendIntervalLabel);
			sendIntervalLabel.setText("send interval:");
			sendIntervalLabel.setBounds(410, h, 100, 29);
			sendIntervalField=new JTextField();
			mainPanel.add(sendIntervalField);
			sendIntervalField.setText("0");
			sendIntervalField.setBounds(510, h, 100, 29);
			
/*			
			String[] exInterval={"0.1-sec","0.5-sec","1-sec","2-sec","5-sec","10-sec","30-sec","1-min","5-min","10-min","30-min","1-h","12-h","24-h",""};
			execIntervalCombo = new JComboBox(exInterval);
			mainPanel.add(execIntervalCombo);
			execIntervalCombo.setBounds(310, h, 100, 24);
			execIntervalCombo.setSelectedIndex(0);
			execIntervalCombo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					execIntervalComboActionPerformed(evt);
				}
			});
			*/
			/*
			returnIntervalLabel = new JLabel();
			mainPanel.add(returnIntervalLabel);
			returnIntervalLabel.setText("return interval:");
			returnIntervalLabel.setBounds(410, h, 100, 26);
			String[] rInterval={"10-sec","30-sec","1-min","5-min","10-min","30-min","1-h","12-h","24-h",""};
			returnIntervalCombo = new JComboBox(rInterval);
			mainPanel.add(returnIntervalCombo);
			returnIntervalCombo.setBounds(520, h, 100, 25);
			returnIntervalCombo.setSelectedIndex(0);
			returnIntervalCombo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					returnIntervalComboActionPerformed(evt);
				}
			});
			*/
			onlineCommandRefreshButton = new JCheckBox();
			mainPanel.add(onlineCommandRefreshButton);
			onlineCommandRefreshButton.setText("online refresh");
			onlineCommandRefreshButton.setBounds(629, h, 135, 25);
			onlineCommandRefreshButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
//					onlineCommandRefreshButtonActionPerformed(evt);
					mainController.parseCommand("onlineCommandRefresh", ""+(onlineCommandRefreshButton.isSelected()));
				}
			});			
		}
		{
			startWatchingButton = new JToggleButton();
			mainPanel.add(startWatchingButton);
			startWatchingButton.setText("Start");
			startWatchingButton.setBounds(662, 220, 80, 25);
			startWatchingButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
//					startWatchingButtonActionPerformed(evt);
					mainController.parseCommand("wikiStartWatching", "");
				}
			});
		}
		{
			endWatchingButton = new JButton();
			mainPanel.add(endWatchingButton);
			endWatchingButton.setText("End");
			endWatchingButton.setBounds(662, 245, 80, 25);
			endWatchingButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
//					endWatchingButtonActionPerformed(evt);
					mainController.parseCommand("wikiEndWatching", "");
				}
			});
		}
		{
			JLabel maxLabel=new JLabel();
			mainPanel.add(maxLabel);
			maxLabel.setText("max com");			
			maxLabel.setBounds(665, 290, 80, 25);
			maxComField=new JTextField();
			mainPanel.add(maxComField);			
			maxComField.setText("2000");
			maxComField.setBounds(662, 315, 100, 29);
			
		}

		{
			clearCommandButton = new JButton();
			mainPanel.add(clearCommandButton);
			clearCommandButton.setText("Clear");
			clearCommandButton.setBounds(662, 270, 80, 25);
			clearCommandButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					clearCommandButtonActionPerformed(evt);
				}
			});
		}
		JButton savePropertiesButton = new JButton("SaveProperties");
		savePropertiesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Save Properties");
//				connectTwitter();
				reflectProperties();
				saveProperties();
			}
		});
		savePropertiesButton.setBounds(530, 0, 165, 29);
		mainPanel.add(savePropertiesButton);
		
		{
			int h=68;
			JLabel auth1Label = new JLabel("Basic Auth.");
			auth1Label.setBounds(x1, h, 80, 24);
			mainPanel.add(auth1Label);
			
			auth1ID = new JTextField();
			auth1ID.setBounds(x2, h, 160, 24);
			mainPanel.add(auth1ID);
			auth1ID.setColumns(10);
			
    		JLabel auth1Pass = new JLabel("Pass.:");
	    	auth1Pass.setBounds(260, h, 70, 24);
		    mainPanel.add(auth1Pass);
		
    		password1Field = new JPasswordField();
	    	password1Field.setBounds(330, h, 105, 24);
		    mainPanel.add(password1Field);
		}		
		{
			int h=120;			
		auth2Label = new JLabel("Auth2.");
		auth2Label.setBounds(x1, h, 61, 24);
		mainPanel.add(auth2Label);
		
		auth2ID = new JTextField();
		auth2ID.setBounds(x2, h, 160, 24);
		mainPanel.add(auth2ID);
		auth2ID.setColumns(10);
		
		auth2PassLabel = new JLabel("Pass.:");
		auth2PassLabel.setBounds(260, h, 61, 24);
		mainPanel.add(auth2PassLabel);
		
		password2Field = new JPasswordField();
		password2Field.setBounds(330, h, 105, 24);
		mainPanel.add(password2Field);
		}
		
		{
//		   mainPanel.setSize(600, 700);
//		   mainPanel.setPreferredSize(new java.awt.Dimension(788, 700));
		}
		this.setVisible(false);
//		this.setSize(804, 700);
		
	}
	void exitSystem(){
		mainController.exit();
		this.dispose();
	}
	
	JLabel consumerKeyLabel;
	JLabel consumerSecretLabel;
	JLabel accessTokenLabel;
	JLabel accessTokenSecretLabel;
	JTextField consumerKeyTextField;
	JTextField consumerSecretTextField;
	JTextField accessTokenTextField;
	JTextField accessTokenSecretTextField;
	
	public void twitterAuthSettingGui(){
		try{
		JPanel mainPanel= new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		mainPanel.setLayout(null);
		
	    mainPanel.setLayout(null);
		if(this.mainTabPane!=null) {
			this.mainTabPane.add("TwitterAuth",mainPanel);
		}
		else{
			mainPanel.setLayout(null);
			mainPanel.add(this);
		}
		{
			consumerKeyLabel = new JLabel();
			mainPanel.add(consumerKeyLabel);
			consumerKeyLabel.setText("Comsumer Key:");
			consumerKeyLabel.setBounds(1, 30, 105, 24);
		}
		{
			consumerKeyTextField = new JTextField();
			mainPanel.add(consumerKeyTextField);
			consumerKeyTextField.setBounds(120, 30, 446, 30);
		}
		{
			consumerSecretLabel = new JLabel();
			mainPanel.add(consumerSecretLabel);
			consumerSecretLabel.setText("Comsumer Secret:");
			consumerSecretLabel.setBounds(1, 55, 120, 24);
		}
		{
			consumerSecretTextField = new JTextField();
			mainPanel.add(consumerSecretTextField);
			consumerSecretTextField.setBounds(120, 55, 446, 30);
		}
		{
			accessTokenLabel = new JLabel();
			mainPanel.add(accessTokenLabel);
			accessTokenLabel.setText("Access Token:");
			accessTokenLabel.setBounds(1, 80, 105, 24);
		}
		{
			accessTokenTextField = new JTextField();
			mainPanel.add(accessTokenTextField);
			accessTokenTextField.setBounds(120, 80, 446, 30);
		}
		{
			accessTokenSecretLabel = new JLabel();
			mainPanel.add(accessTokenSecretLabel);
			accessTokenSecretLabel.setText("AccessToken Secret:");
			accessTokenSecretLabel.setBounds(1, 105, 120, 24);
		}
		{
			accessTokenSecretTextField = new JTextField();
			mainPanel.add(accessTokenSecretTextField);
			accessTokenSecretTextField.setBounds(120, 105, 446, 30);
		}
		JButton savePropertiesButton = new JButton("SaveProperties");
		savePropertiesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Save Properties");
//				connectTwitter();
				reflectProperties();
				saveProperties();
			}
		});
		savePropertiesButton.setBounds(530, 0, 165, 29);
		mainPanel.add(savePropertiesButton);
		
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
//			this.setTitle("PukiwikiCommunicator");
		
	}
	
/*
	private void readIntervalComboActionPerformed(ActionEvent evt) {
//		System.out.println("commandIntervalCombo.actionPerformed, event="+evt);
		//TODO add your code for commandIntervalCombo.actionPerformed
		this.setting.setProperty("readInterval", (String)(this.readIntervalCombo.getSelectedItem()));
	}
	private void execIntervalComboActionPerformed(ActionEvent evt) {
//		System.out.println("commandIntervalCombo.actionPerformed, event="+evt);
		//TODO add your code for commandIntervalCombo.actionPerformed
		this.setting.setProperty("execInterval", (String)(this.execIntervalCombo.getSelectedItem()));
	}
	
	private void returnIntervalComboActionPerformed(ActionEvent evt) {
//		System.out.println("returnIntervalCombo.actionPerformed, event="+evt);
		//TODO add your code for returnIntervalCombo.actionPerformed
		this.setting.setProperty("returnInterval", (String)(this.returnIntervalCombo.getSelectedItem()));
	}
	*/
	private void clearCommandButtonActionPerformed(ActionEvent evt) {
//		System.out.println("clearCommandButton.actionPerformed, event="+evt);
		//TODO add your code for clearCommandButton.actionPerformed
		for(int i=0;i<maxCommands;i++){
			this.commandTable.setValueAt("", i, 0);
			this.commandTable.setValueAt("", i, 1);
		}
	}
	
	private void disConnectButtonActionPerformed(ActionEvent evt) {
//		System.out.println("disConnectButton.actionPerformed, event="+evt);
		//TODO add your code for disConnectButton.actionPerformed
		this.wikiConnectButton.setSelected(false);
	}
	
	private int maxCommands=100;	
	private int maxID=20;
	    
    String TWITTER_CONSUMER_KEY    = "取得したコードを入力";
    String TWITTER_CONSUMER_SECRET = "取得したコードを入力";
     
    String TWITTER_ACCESS_TOKEN        = "取得したコードを入力";
    String TWITTER_ACCESS_TOKEN_SECRET = "取得したコードを入力";
//    Properties configuration = new Properties();       
    Twitter twitter;
    MainController pukiwikiCom;
	private Vector<String> putMessageQueue=new Vector();
	private Vector<String> putResultQueue=new Vector();	
	private Vector<String> putTableQueue=new Vector();
	private JTextField auth1ID;
	private JPasswordField password1Field;
	private JLabel auth2Label;
	private JTextField auth2ID;
	private JLabel auth2PassLabel;
	private JPasswordField password2Field;
	/**
	 * @wbp.nonvisual location=61,1
	 */
	public void putMessage(String x){
		putMessageQueue.add(x);
		/* caution! uncomment after gui build */
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	String so=putMessageQueue.remove(0);
                messageArea.append(so+"\n");
            }
       });
       /* */
	}
	private void initCommandTable(int size){
		String [] oneComLine=new String[]{"",""};
		String [][] comLines =new String[size][];
		for(int i=0;i<size;i++){
			comLines[i]=new String[]{"",""};
		}
		DefaultTableModel tableModel= new DefaultTableModel(
				comLines ,
				new String[] { "No","Command" });
		
		commandTable = new JTable();
		commandTable.setModel(tableModel);
		commandAreaPane.setViewportView(commandTable);		
	}
	private void initIdListTable(int size){
		String [][] urlListLines =new String[size][];
		for(int i=0;i<size;i++){
			urlListLines[i]=new String[]{""+i,"","",""};
		}
		DefaultTableModel tableModel= new DefaultTableModel(
				urlListLines ,
				new String[] { "No","URL","ID","Password" });
		
		oTable = new JTable();
		oTable.setModel(tableModel);
		UrlIDTable  idListAble=new UrlIDTable(oTable);
		for(int j=1;j<4;j++){
			JTextField text = new JTextField();
			text.setFont(new Font("Dialog", Font.PLAIN,10));
			text.setPreferredSize(new Dimension(100,30));
			DefaultCellEditor editor =new DefaultCellEditor(text);					
			TableColumn col=oTable.getColumnModel().getColumn(j);
			col.setCellEditor(editor);
		}
		oTable.setEnabled(true);
		idListAreaPane.setViewportView(oTable);		
	}	

	public void loadProperties(){
	       try {
	           setting = new Properties() ;
	           FileInputStream appS = new FileInputStream( settingFileName);
	           setting.load(appS);

	        } catch( Exception e){
//	           System.err.println(e);
		        setting = new Properties() ;
		        setProperties();
//	        	return;
	        } 
	}
	public void saveProperties(){
	       
	       try {
	           FileOutputStream saveS = new FileOutputStream(settingFileName);
	           if(setting==null){
	        	   setting=new Properties();
	           }
	           reflectProperties();
	           setting.store(saveS,"--- tweet-by-wiki settings ---");

	        } catch( Exception e){
	           System.err.println(e);
	        } 
	}
	
	public void setProperties(){
		if(this.setting==null)return;
		
/*		
        for(int i=0; i<maxID;i++){
    		String urlx=(String)(this.idListTable.getValueAt(i, 1));
    		String idx=(String)(this.idListTable.getValueAt(i, 2));
    		String pwx=(String)(this.idListTable.getValueAt(i, 3));
    		String idPassx=idx+":"+pwdx;
    		String urlWithoutParametersx=getUrlWithoutParameters(idx);
    	        String authUrlx="basicAuthList-"+i+"-"+urlWithoutParametersx;
            setting.put(authUrl2,idPassx);    	
        }
*/
		for(Object keyo:setting.keySet()){
			String key=(String)keyo;
			if(key.startsWith("basicAuthList-")){
				String xlabel=key.substring("basicAuthList-".length());
				String[] rest=new String[1];
				int[] no=new int[1];
				Util.parseInt(xlabel, no, rest);
				int nox=no[0];
				String label2=rest[0];
				String urlWithoutParametersx=label2.substring(1);
				String idPassx=(String)(setting.get(key));
				int p1=idPassx.indexOf(":");
				String idx=idPassx.substring(0,p1);
				String passx=idPassx.substring(idx.length()+1);
				oTable.setValueAt(""+nox, nox, 0);
				oTable.setValueAt(urlWithoutParametersx, nox, 1);
				oTable.setValueAt(idx, nox, 2);
				oTable.setValueAt(passx, nox, 3);
			}
		}
		String w=this.setting.getProperty("deviceID");
		if(w!=null)
		   this.deviceIDField.setText(w);
		w=this.setting.getProperty("managerUrl");
		if(w!=null)
		   this.wikiUrlTextField.setText(w);
		w=this.setting.getProperty("secondaryUrl");
		if(w!=null)
		   this.wikiSecondaryUrlTextField.setText(w);	
		w=this.setting.getProperty("onlineCommandRefresh");
		if(w!=null){
			if(w.equals("true"))
		       this.onlineCommandRefreshButton.setSelected(true);
			else
				this.onlineCommandRefreshButton.setSelected(false);
		}
		w=this.setting.getProperty("readInterval");
		if(w!=null)
			this.readIntervalField.setText(w);
//			this.readIntervalCombo.setSelectedItem(w);
		w=this.setting.getProperty("execInterval");
		if(w!=null)
			this.execIntervalField.setText(w);
//			this.execIntervalCombo.setSelectedItem(w);
		w=this.setting.getProperty("sendInterval");
		if(w!=null)
			this.sendIntervalField.setText(w);
//			this.sendIntervalCombo.setSelectedItem(w);	
		w=this.setting.getProperty("maxCommandsStr");
		if(w==null){
			setting.put("maxCommandsStr",""+this.maxCommands);
		}
		else{
			try{
		    this.maxCommands=(new Integer(w)).intValue();
			}
			catch(Exception e){
				this.maxCommands=200;
			}
		}		
		initCommandTable(this.maxCommands);	
		
		String url=wikiUrlTextField.getText();
		if(url!=null){
			if(url.length()>=1){
		      String urlWithoutParameters=getUrlWithoutParameters(url);
	          String authUrl="basicAuth-"+urlWithoutParameters;
		      w=this.setting.getProperty(authUrl);
		      if(w!=null){
  	            StringTokenizer st1 =new StringTokenizer(w,":");
  	            String id="";
  	            String pas="";
  	            if(st1!=null){
  	            	try{
  	                  id=st1.nextToken();
  	                  pas=st1.nextToken();
                      this.auth1ID.setText(id);
                      this.password1Field.setText(pas);
  	            	}
  	            	catch(Exception e){
  	            		this.putMessage("Auth error."+e+" w="+w);
  	            	}
  	            }
		      }
			}
		}
		String url2=wikiSecondaryUrlTextField.getText();
		if(url2!=null){
			if(url2.length()>=1){
		      String urlWithoutParameters2=getUrlWithoutParameters(url2);
	          String authUrl2="basicAuth-"+urlWithoutParameters2;
		      w=this.setting.getProperty(authUrl2);
		      if(w!=null){
  	            StringTokenizer st1 =new StringTokenizer(w,":");
  	            String id="";
  	            String pas="";
  	            if(st1!=null){
  	            	try{
     	              id=st1.nextToken();
  	                  pas=st1.nextToken();
                      this.auth2ID.setText(id);
                      this.password2Field.setText(pas);
	            	}
	            	catch(Exception e){
	            		this.putMessage("Auth error."+e+" w="+w);
	            	}                  
  	            }
		      }		
			}
		}
//		
        w =this.setting.getProperty("oauth.consumerKey");
        if(w!=null){
        	this.consumerKeyTextField.setText(w);
        }
        w =this.setting.getProperty("oauth.consumerSecret");
        if(w!=null){
        	this.consumerSecretTextField.setText(w);
        }
        w =this.setting.getProperty("oauth.accessToken");
        if(w!=null){
        	this.accessTokenTextField.setText(w);
        }
        w =this.setting.getProperty("oauth.accessTokenSecret");
        if(w!=null){
        	this.accessTokenSecretTextField.setText(w);
        }		
	}
	public void reflectProperties(){
		if(this.setting==null)return;
		setting.put("deviceID", this.deviceIDField.getText());
		setting.put("managerUrl", this.wikiUrlTextField.getText());
		boolean selected=this.onlineCommandRefreshButton.isSelected();
		setting.put("onlineCommandRefresh", ""+selected);
		setting.put("readInterval", this.readIntervalField.getText());
        setting.put("execInterval", this.execIntervalField.getText());
		setting.put("sendInterval", this.sendIntervalField.getText());
		setting.put("oauth.consumerKey", this.consumerKeyTextField.getText());
		setting.put("oauth.consumerSecret", this.consumerSecretTextField.getText());
		setting.put("oauth.accessToken", this.accessTokenTextField.getText());
		setting.put("oauth.accessTokenSecret", this.accessTokenSecretTextField.getText());
		setting.put("maxCommandsStr", this.maxComField.getText());
		String uname=auth1ID.getText();
		char[] pwd=password1Field.getPassword();
		String pwdx=new String(pwd);
		String idPass=uname+":"+pwdx;
		String url=wikiUrlTextField.getText();
		String urlWithoutParameters=getUrlWithoutParameters(url);
	        String authUrl="basicAuth-"+urlWithoutParameters;
        setting.put(authUrl,idPass);
        
		String url2=wikiSecondaryUrlTextField.getText();
		setting.put("secondaryUrl", url2);
		String uname2=auth2ID.getText();
		char[] pwd2=password2Field.getPassword();
		String pwdx2=new String(pwd2);
		String idPass2=uname2+":"+pwdx2;
		String urlWithoutParameters2=getUrlWithoutParameters(url2);
	        String authUrl2="basicAuth-"+urlWithoutParameters2;
        setting.put(authUrl2,idPass2);
        
        for(int i=0; i<maxID;i++){
    		String urlx=(String)(this.oTable.getValueAt(i, 1));
    		String idx=(String)(this.oTable.getValueAt(i, 2));
    		String pwx=(String)(this.oTable.getValueAt(i, 3));
    		String idPassx=idx+":"+pwx;
    		String urlWithoutParametersx=getUrlWithoutParameters(urlx);
    	        String authUrlx="basicAuthList-"+i+"-"+urlWithoutParametersx;
            setting.put(authUrlx,idPassx);
        	
        }
		
	}
	private String getUrlWithoutParameters(String url){
		int i=url.indexOf("?");
		if(i<0) return url;
		String rtn=url.substring(0,i);
		return rtn;
	}
	
	public void setMaxComand(String x){
		
	}
	static boolean isError;	
	public String command(String x, String v) {
		// TODO Auto-generated method stub
//		this.putMessage(x+"-"+v);
		if(x.equals("setDeviceID")){
			this.deviceIDField.setText(v);
			this.reflectProperties();
			this.saveProperties();
			return "ok";
		}
		else
		if(x.equals("writeResult")){
			this.writeResult(v);
		}		
		else
		if(x.equals("writeMessage")){
			this.writeMessage(v);
		}
		else
		if(x.equals("getWikiUrl")){
			String rtn=this.wikiUrlTextField.getText();
			return rtn;
		}
		else
		if(x.startsWith("wikiCommandTable setValueAt ")){
			String p0=x.substring("wikiCommandTable setValueAt ".length());
			putTableQueue.add(p0);
			putTableQueue.add(v);
			isError=false;
	        SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	            	String p0=putTableQueue.remove(0);
	            	String v=putTableQueue.remove(0);
	    			String[] rest=new String[1];
	    			String[] sconst=new String[1];
	    			int[] iv2=new int[1];
	    			int[] iv3=new int[1];
	    			if(!Util.parseInt(p0,iv2,rest)) isError=true;
	    			String p1=Util.skipSpace(rest[0]);
	    			if(!Util.parseInt(p1, iv3, rest)) isError=true;
	    			int i=iv2[0];
	    			int j=iv3[0];	    			
//	    			this.commandTable.setValueAt("", i, 0);
	                commandTable.setValueAt(v, i, j);
	            	
	            }
	       });
	        if(isError) return "ERROR";
            return "ok";
		}
		else
		if(x.equals("getDeviceID")){
			String rtn=this.deviceIDField.getText();
			return rtn;
		}
		else
		if(x.equals("getCurrentUrl")){
			String rtn=this.wikiUrlTextField.getText();
			return rtn;
		}
		else	
		if(x.equals("setSecondaryURLList")){
			this.wikiSecondaryUrlTextField.setText(v);
            this.setting.put("secondaryURLList", v);
		}
		else
		if(x.equals("setPageName")){
			String url=""+this.wikiUrlTextField.getText();
			StringTokenizer st=new StringTokenizer(url,"?");
			if(!st.hasMoreElements()) return "ERROR";
			String baseUrl=st.nextToken();
			if(!st.hasMoreElements()) return "ERROR";
			String oldPageName=st.nextToken();
			String newPageUrl=baseUrl+"?"+v;
			putMessage("parseCommand-setPageName-"+url+" to "+newPageUrl);
			this.wikiUrlTextField.setText(""+newPageUrl);
			this.reflectProperties();
			return "OK";
		}
		else
		if(x.startsWith("set ")){
			x=x.substring("set ".length());
			if(x.equals("readInterval")){
				this.readIntervalField.setText(v);
				this.reflectProperties();
				this.saveProperties();
				return "OK";
			}
			else
			if(x.equals("execInterval")){
				this.execIntervalField.setText(v);
				this.reflectProperties();
				this.saveProperties();
				return "OK";
			}
			else
			if(x.equals("sendInterval")){
				this.sendIntervalField.setText(v);
				this.reflectProperties();
				this.saveProperties();
				return "OK";
			}
		}
		else{
		   return null;
		}
		return null;
	}
	public void writeMessage(String x){
		Date d=new Date();
		String w="";
		try{
			if(messageArea!=null){
		       w=this.messageArea.getText();
		       if(w!=null){
		          if(w.length()>10000)
			          w=w.substring(5000);
		       }
			}
		}
		catch(Exception e){
			if(x!=null)
			System.out.println("writeMessage("+x+") error");
			System.out.println(e.toString());
			return;
		}
		w=w+d+" "+x+"\n";
		putMessageQueue.add(w);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	String w=putMessageQueue.remove(0);
        		messageArea.setText(w);
        		JScrollBar sb=messageAreaScrollPane.getVerticalScrollBar();
        		sb.setValue(sb.getMaximum());           	
            }
       });
	}
	public void writeResult(String x){
//		Date d=new Date();
		String w=this.resultArea.getText();
		if(w.length()>10000)
			w=w.substring(5000);
		w=w+" "+x;
		putResultQueue.add(w);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	String w=putResultQueue.remove(0);
        		resultArea.setText(w);
        		JScrollBar sb=resultPane.getVerticalScrollBar();
        		sb.setValue(sb.getMaximum());           	
            }
       });
	}


	public JTable getJTable(String name) {
		// TODO Auto-generated method stub
		if(name.equals("commandTable")){
			return this.commandTable;
		}
		return null;
	}
}
