package org.yamaLab.pukiwikiCommunicator.UdpP2P;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class UdpP2PClientGui 
{
	private JLabel udpHoleLabel;
	private JTable uTable;
	private JScrollPane udpHoleAreaPane;
	private int maxID=30;
	private JPanel mainPanel;
	private UdpP2P controller;

	private JScrollPane commandAreaScrollPane;
	private JTextArea commandArea;
	
	private JScrollPane clientMessageAreaScrollPane;
	private JTextArea clientMessageArea;
	private JTextField clientSendingMessageField;
	private JButton serverAddressSetButton;
	private JTextField serverAddressField;

	UdpP2PClientGui(UdpP2P mc){
		initUdpP2PClientGui();
		controller=mc;
	}
	public JPanel getPanel(){
		return mainPanel;
	}
	public void initUdpP2PClientGui(){
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
			int h=230;
			JLabel serverAddressLabel = new JLabel();
			mainPanel.add(serverAddressLabel);
			serverAddressLabel.setText("server ip:");
			serverAddressLabel.setBounds(x1, h, 105, 24);
			serverAddressField = new JTextField();
			mainPanel.add(serverAddressField);
			serverAddressField.setBounds(x2, h, 446, 30);

			serverAddressSetButton = new JButton();
			mainPanel.add(serverAddressSetButton);
			serverAddressSetButton.setText("set");
			serverAddressSetButton.setBounds(651, h, 120, 30);
			serverAddressSetButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					serverAddressSetButtonActionPerformed(evt);
				}
			});
		}		
				
		
		{
			int h=260;
			JLabel sendClientMessageLabel = new JLabel();
			mainPanel.add(sendClientMessageLabel);
			sendClientMessageLabel.setText("clientMessage:");
			sendClientMessageLabel.setBounds(x1, h, 105, 24);
			clientSendingMessageField = new JTextField();
			mainPanel.add(clientSendingMessageField);
			clientSendingMessageField.setBounds(x2, h, 446, 30);

			JButton sendClientMessageButton = new JButton();
			mainPanel.add(sendClientMessageButton);
			sendClientMessageButton.setText("send");
			sendClientMessageButton.setBounds(651, h, 120, 30);
			sendClientMessageButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					clientSendButtonActionPerformed(evt);
				}
			});
		}		
		
		{		
//			int h=540;
			int h=290;
		    commandAreaScrollPane = new JScrollPane();
		    commandAreaScrollPane.setBounds(x2, h, 550, 120);
		    mainPanel.add(commandAreaScrollPane);
		
		    commandArea = new JTextArea();
		    commandAreaScrollPane.setViewportView(commandArea);		
		
		    JLabel lblMessage = new JLabel("command:");
		    lblMessage.setBounds(x1, h, 101, 16);
		    mainPanel.add(lblMessage);
		}		
		{		
//			int h=540;
			int h=420;
		    clientMessageAreaScrollPane = new JScrollPane();
		    clientMessageAreaScrollPane.setBounds(x2, h, 550, 120);
		    mainPanel.add(clientMessageAreaScrollPane);
		
		    clientMessageArea = new JTextArea();
		    clientMessageAreaScrollPane.setViewportView(clientMessageArea);		
		
		    JLabel lblMessage = new JLabel("ClientMessage:");
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
	
	String clientMessage;
	public void writeClientMessage(String x){
		System.out.println(x);
		if(clientMessage==null){
			clientMessage=x;
		}
		else{
			if(clientMessage.length()>5000){
				clientMessage=clientMessage.substring(clientMessage.length()-4900);
			}
			clientMessage=clientMessage+"\n"+x;
		}
		clientMessageArea.setText(clientMessage);
	}
	String commandList;
	public void writeCommand(String x){
		System.out.println(x);
		if(commandList==null){
			commandList=x;
		}
		else{
			if(commandList.length()>5000){
				commandList=commandList.substring(commandList.length()-4900);
			}
			commandList=commandList+"\n"+x;
		}
		commandArea.setText(commandList);
	}
	EchoClient echoClient;
	public void setClient(EchoClient ec){
		echoClient=ec;
	}
	public void clientSendButtonActionPerformed(ActionEvent e){
		if(echoClient!=null){
			String x=this.clientSendingMessageField.getText();
			echoClient.writeClientMessage(x);
		}
	}
	public void serverAddressSetButtonActionPerformed(ActionEvent e){
		if(echoClient!=null){
			String x=this.serverAddressField.getText();
			echoClient.setServerAddress(x);
		}
	}	
	public void setServerAddress(String x){
		this.serverAddressField.setText(x);
	}
	public void setSendMessage(String x){
		this.clientSendingMessageField.setText(x);
	}	
	public String getServerAddress(){
		return this.serverAddressField.getText();
	}
}
