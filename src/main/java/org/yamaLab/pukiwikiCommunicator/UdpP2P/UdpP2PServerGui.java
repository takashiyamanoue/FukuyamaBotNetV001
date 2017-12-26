package org.yamaLab.pukiwikiCommunicator.UdpP2P;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class UdpP2PServerGui {
	private JLabel udpHoleLabel;
	private JTable uTable;
	private JScrollPane udpHoleAreaPane;
	private int maxID=30;
	private JPanel mainPanel;
	private UdpP2P controller;
	
	private JScrollPane serverMessageAreaScrollPane;
	private JTextArea serverMessageArea;

	UdpP2PServerGui(UdpP2P mc){
		initUdpP2PServerGui();
		controller=mc;
	}
	public JPanel getPanel(){
		return mainPanel;
	}
	public void initUdpP2PServerGui(){
		mainPanel= new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		mainPanel.setLayout(null);
        int x1=1;
        int x2=95;

		{
			int h=30;
			udpHoleLabel = new JLabel();
			mainPanel.add(udpHoleLabel);
			udpHoleLabel.setText("udp-ip-port list:");
			udpHoleLabel.setBounds(x1, h, 109, 33);
			udpHoleAreaPane = new JScrollPane();
			mainPanel.add(udpHoleAreaPane);
			udpHoleAreaPane.setBounds(x2, h, 550, 200);
			{
				/*
				commandArea = new JTextArea();
				commandAreaPane.setViewportView(commandArea);
				*/
				initUdpHoleTable(maxID);
			}
		}

		JButton savePropertiesButton = new JButton("SaveProperties");
		savePropertiesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Save Properties");
//				connectTwitter();
//				reflectProperties();
//				saveProperties();
			}
		});
		savePropertiesButton.setBounds(530, 0, 165, 29);
		mainPanel.add(savePropertiesButton);

		
		{		
//			int h=540;
			int h=230;
		    serverMessageAreaScrollPane = new JScrollPane();
		    serverMessageAreaScrollPane.setBounds(x2, h, 550, 120);
		    mainPanel.add(serverMessageAreaScrollPane);
		
		    serverMessageArea = new JTextArea();
		    serverMessageAreaScrollPane.setViewportView(serverMessageArea);		
		
		    JLabel lblMessage = new JLabel("ServerMessage:");
		    lblMessage.setBounds(x1, h, 101, 16);
		    mainPanel.add(lblMessage);
		}		
		
	}	
	private void initUdpHoleTable(int size){
		String [][] udpHoleListLines =new String[size][];
		for(int i=0;i<size;i++){
			udpHoleListLines[i]=new String[]{""+i,"","",""};
		}
		DefaultTableModel tableModel= new DefaultTableModel(
				udpHoleListLines ,
				new String[] { "No","IP","PORT","Memo" });
		
		uTable = new JTable();
		uTable.setModel(tableModel);
//		UrlIDTable  udpHoleLisTable=new UrlIDTable(uTable);
		for(int j=1;j<4;j++){
			JTextField text = new JTextField();
			text.setFont(new Font("Dialog", Font.PLAIN,10));
			text.setPreferredSize(new Dimension(100,30));
			DefaultCellEditor editor =new DefaultCellEditor(text);					
			TableColumn col=uTable.getColumnModel().getColumn(j);
			col.setCellEditor(editor);
		}
		uTable.setEnabled(true);
		udpHoleAreaPane.setViewportView(uTable);		
	}			
	public void setIpPort(int i, String ip, String port, String memo){
		TableModel tm=uTable.getModel();
		tm.setValueAt(ip, i, 1);
		tm.setValueAt(port, i, 2);
		tm.setValueAt(memo, i, 3);
	}

	String serverMessage;
	public void writeServerMessage(String x){
		System.out.println(x);
		if(serverMessage==null){
			serverMessage=x;
		}
		else{
			if(serverMessage.length()>5000){
				serverMessage=serverMessage.substring(serverMessage.length()-4900);
			}
			serverMessage=serverMessage+"\n"+x;
		}
		serverMessageArea.setText(serverMessage);
	}	
	EchoServer echoServer;
	public void setServer(EchoServer es){
		echoServer=es;
	}
	
}
