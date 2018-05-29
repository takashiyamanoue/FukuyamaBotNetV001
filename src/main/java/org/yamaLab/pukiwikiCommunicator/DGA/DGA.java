package org.yamaLab.pukiwikiCommunicator.DGA;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.Calendar;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.apache.commons.httpclient.HttpMethod;
import org.yamaLab.pukiwikiCommunicator.language.InterpreterInterface;
import org.yamaLab.pukiwikiCommunicator.language.MyString;

public class DGA implements Runnable 
{
	private JPanel mainPanel;

	private JScrollPane ccServerAreaScrollPane;
	private JTextArea ccServerMessageArea;
	private JTextField ccServerUrlField;
	private JButton ccServerUrlSetButton;
	
	private JTabbedPane tabbedPane;

    InterpreterInterface service;	
    
    private Thread me;
	
	public DGA(InterpreterInterface bf){
		this.service=bf;
		initDGAGui();
		
			Calendar cal = Calendar.getInstance();
//			mkDomainName(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DATE));
			start();
					

	}
	
	public void setGui(JTabbedPane t){
		tabbedPane=t;
		t.add("DGA",this.getPanel());
	}

	
	
	public JPanel getPanel(){
		return mainPanel;
	}
	public void initDGAGui(){
		mainPanel= new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		mainPanel.setLayout(null);
        int x1=1;
        int x2=95;

				
		{
			int h=25;
			JLabel ccAddressLabel = new JLabel();
			mainPanel.add(ccAddressLabel);
			ccAddressLabel.setText("cc server:");
			ccAddressLabel.setBounds(x1, h, 105, 24);
			ccServerUrlField = new JTextField();
			mainPanel.add(ccServerUrlField);
			ccServerUrlField.setBounds(x2, h, 446, 30);

			ccServerUrlSetButton = new JButton();
			mainPanel.add(ccServerUrlSetButton);
			ccServerUrlSetButton.setText("get");
			ccServerUrlSetButton.setBounds(651, h, 120, 30);
			ccServerUrlSetButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
                    accessDGADomain();
				}
			});
		}		
				
		{		
//			int h=540;
			int h=100;
		    ccServerAreaScrollPane = new JScrollPane();
		    ccServerAreaScrollPane.setBounds(x2, h, 550, 500);
		    mainPanel.add(ccServerAreaScrollPane);
		
		    ccServerMessageArea = new JTextArea();
		    ccServerAreaScrollPane.setViewportView(ccServerMessageArea);		
		
		    JLabel lblMessage = new JLabel("ClientMessage:");
		    lblMessage.setBounds(x1, h, 101, 16);
		    mainPanel.add(lblMessage);
		}

		
	}	
String[] domains = new String[100];
	
public void accessDGADomain(){
	Calendar cal = Calendar.getInstance();
	mkDomainName(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DATE));	
	for(int i=0;i<100;i++) {
		System.out.println(domains[i]);
	
	 ccServerUrlField.setText("http://"+domains[i]);
	
//    String appliname=print.print(car(argl));
//    String command=print.print(second(argl));
  InterpreterInterface appli=service.lookUp("connector");
  String rtn=appli.parseCommand("getRawPage "+ccServerUrlField.getText());
ccServerMessageArea.setText(rtn);
	}
}

	public void mkDomainName(int year, int month, int day) {
		int i;
		byte[] S = new byte[4];
		
	
	 for(i = 0; i < 100; i++) { 
		 /* S is a byte array and year, date, month are numeric */ 
		 S[0] = (byte)((year + 48) % 256); S[1] = (byte)month; 
		 S[2] = (byte)(7 * (day / 7)); S[3] =  (byte)i; 
		 MessageDigest md5=null;
		 try {
		 md5= MessageDigest.getInstance("MD5");
		 }
		 catch(Exception e) {
			 
		 }
		 byte[] hash = md5.digest(S); 
		// hash = md5(S);
	 
		 /* convert hash to URL domain name */ 
		 String name = ""; 
		 int j;
		 for(j = 0; j < hash.length; j++) { 
			 char c1 = (char)((hash[j] & 0x1F) + 'a'); 
			 char c2 = (char)(((hash[j] / 8)& 0x1F) + 'a'); 
			 if(c1 != c2) { 
				 if(c1 <= 'z') name += c1; 
				 if(c2 <= 'z') name += c2; 
				 } 
			 }
	 /* select TLD for domain */ 
	 name += "."; 
	 if(i % 6 == 0) { 
		 name += "ru"; 
		 } else if(i % 5 != 0) { 
			 if(i == 0 && 0x03 == 0) { 
				 name += "info"; 
				 } else if(i % 3 != 0) { 
					 if ((i % 256) != 0 && 0x01 != 0) name += "com"; 
					 else name += "net"; 
					 } else { 
						 name += "org"; 
						 } 
			} else { 
				 name += "biz"; 
				 }
	 domains[i] = name; 
	 }
	}
	
private void start(){
	 if(me==null){
		   me=new Thread(this,"DGA Thread");
		   me.start();
	 }
}

private void stop(){
	 me=null;
}

	public void run() {
		// TODO Auto-generated method stub
		while(me!=null){
			 accessDGADomain();
			 try{
				    me.sleep(60000);
			 }
			 catch(InterruptedException e){
				     System.out.println("error at sleep:"+e);
			 }
		}
		
	}
	
}

