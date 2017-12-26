package org.yamaLab.pukiwikiCommunicator.UdpP2P;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JTabbedPane;
import javax.swing.JPanel;

import org.yamaLab.pukiwikiCommunicator.SBUtil;
import org.yamaLab.pukiwikiCommunicator.language.InterpreterInterface;
import org.yamaLab.pukiwikiCommunicator.language.LispObject;
import org.yamaLab.pukiwikiCommunicator.language.ReadLine;
import org.yamaLab.pukiwikiCommunicator.language.Util;

public class UdpP2P implements InterpreterInterface {
	InterpreterInterface main;
	Properties setting;
	JTabbedPane tabbedPane;
	UdpP2PClientGui clientGui;
	UdpP2PServerGui serverGui;
	HashMap<String, InterpreterInterface> applications;
	EchoClient echoClient;
	EchoServer echoServer;
	int messageID;
	Vector<Integer> idList;
	public UdpP2P(InterpreterInterface m) {
		main=m;
		applications=new HashMap();
		clientGui=new UdpP2PClientGui(this);		
		echoClient=new EchoClient();
		echoClient.setGui(clientGui,this);
		serverGui=new UdpP2PServerGui(this);		
		echoServer=new EchoServer();
		echoServer.setGui(serverGui);
		idList=null;
	}
	public void setSetting(Properties s){
		setting=s;
	}
	public void setGui(JTabbedPane t){
		tabbedPane=t;
		t.add("udpP2PClientPanel",clientGui.getPanel());
		t.add("udpP2PServerPanel",serverGui.getPanel());		
	}

	public String getOutputText() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isTracing() {
		// TODO Auto-generated method stub
		return false;
	}

	public String parseCommand(String x) {
		// TODO Auto-generated method stub
		String[] rest=new String[1];
		if(Util.parseKeyWord(x,"join ",rest)){
			String [] sx=new String[1];
			String w=Util.skipSpace(rest[0]);
			if(!Util.parseStrConst(w, sx, rest)) return "ERROR";
			this.echoClient.join(sx[0]);
			return "OK";
		}
		else
		if(Util.parseKeyWord(x,"leave.",rest)){
			return "OK";
		}
		else			
		if(Util.parseKeyWord(x,"send ",rest)){
			String [] sx=new String[1];
			String w=Util.skipSpace(rest[0]);
			if(!Util.parseStrConst(w, sx, rest)) return "ERROR";
			this.echoClient.send(sx[0]);			
			return "OK";
		}
		else			
		if(Util.parseKeyWord(x,"broadcast ",rest)){
			/*
			 * make a broadcast command message from the command from the wiki command.
			 * and forward it to the p2p group network.
			 * 
			 * The command from the wiki command:
			 * broadcast cmd=<strConst> arg=<strConst>
			 *   ->
			 * The broadcast command in the p2p group network.
			 *  broadcast id=<int> ttl=<int> cmd=<strConst> arg=<strConst>
			 */
			int id=getNewID();
			int ttl=4;
			String line=Util.skipSpace(rest[0]);
			line="id="+id+" ttl="+ttl+rest[0];
			if(!parseBroadcastCommand(line,null)) return "ERROR";
			return "OK";
		}		
		else
		if(Util.parseKeyWord(x,"get ",rest)){
			String w=Util.skipSpace(rest[0]);
			if(Util.parseKeyWord(w, "serverlog", rest)){
				String log=echoServer.getLog();
			}
			else	
			if(Util.parseKeyWord(w, "clientlog", rest)){
				String log=echoClient.getLog();				
			}
			else							
			if(Util.parseKeyWord(w, "thisAddress", rest)){
				String rtn="";
				Enumeration e=null;
				try{
				   e = NetworkInterface.getNetworkInterfaces();
				}
				catch(Exception ev){
					return "ERROR";
				}
				while(e.hasMoreElements())
				{
				    NetworkInterface n = (NetworkInterface) e.nextElement();
				    Enumeration ee = n.getInetAddresses();
				    while (ee.hasMoreElements())
				    {
				        InetAddress i = (InetAddress) ee.nextElement();
				        rtn=rtn+"¥n"+"myip="+i.getHostAddress();
				    }
				}				
			}
			else
			if(Util.parseKeyWord(w, "hole-list", rest)){
				
			}
			return "OK";
		}
		return null;
	}

	public InterpreterInterface lookUp(String x) {
		// TODO Auto-generated method stub
		return main.lookUp(x);
	}
	public void putApplicationTable(String key, InterpreterInterface obj) {
		// TODO Auto-generated method stub
		applications.put(key, obj);
	}
	public boolean parseBroadcastCommand(String line,InetSocketAddress a){
		/*
		 *  broadcast id=<int> ttl=<int> cmd=<strConst> arg=<strConst>
		 *  ... the line does not include "broadcast"
		 */
		String[] rest=new String[1];
	    String[] strc=new String[1];
		line=Util.skipSpace(line);
		if(!Util.parseKeyWord(line, "id=", rest)) return false;
		line=rest[0];
		int[] num=new int[1];
		if(!Util.parseInt(line, num, rest)) return false;
		int id=num[0];
		line=rest[0];
		line=Util.skipSpace(line);
		if(!Util.parseKeyWord(line, "ttl=", rest)) return false;
		line=rest[0];
		if(!Util.parseInt(line, num, rest)) return false;
		int ttl=num[0];
		line=rest[0];
		line=Util.skipSpace(line);		
		if(!Util.parseKeyWord(line, "cmd=", rest)) return false;
		line=rest[0];
		if(!Util.parseStrConst(line, strc, rest)) return false;
		String cmd=strc[0];
		line=rest[0];
		line=Util.skipSpace(line);
		if(!Util.parseKeyWord(line, "arg=", rest)) return false;
		line=rest[0];
		if(!Util.parseStrConst(line, strc, rest)) return false;
		String arg=strc[0];
		forwardBroadcastCommand(id,ttl,cmd,arg,a);
		execCommand(cmd,arg);
		return true;		
	}
	private void forwardBroadcastCommand(int id,int ttl, String cmd, String arg, InetSocketAddress a){
		if(idList==null) {
			idList=new Vector();
			idList.add(Integer.valueOf(id));
		}
		else {
		    if(idList.contains(Integer.valueOf(id))) return;
		    if(idList.size()>10){
			   idList.remove(0);
		     }
		     idList.add(Integer.valueOf(id));
		}
		if(ttl==0) return;
		String forwardCommand="broadcast id="+id+" ttl="+(ttl-1)
				+" cmd=\""+cmd+"\" arg=\""+arg+"\".";
		if(forwardCommand.length()>998){
			echoClient.writeClientMessage("too long broadcast message:"+forwardCommand);
		}
//		echoClient.send(forwardCommand);
		if(a==null){
			echoClient.writeClientMessage(forwardCommand);			
		}
		else{
		    echoClient.writeClientMessageExcept(forwardCommand, a);
		}
	}
	private void execCommand(String id, String arg){
		
	}
	private int getNewID(){
		try{
	    Enumeration<NetworkInterface> nics = 
	            NetworkInterface.getNetworkInterfaces();
	        while(nics.hasMoreElements()){
	            NetworkInterface nic = nics.nextElement();
	            String s = "";
	            byte[] hardwareAddress = nic.getHardwareAddress();
	            if(hardwareAddress != null){
	            	int x=(0xff & hardwareAddress[4]);
	            	x=x<<8|(0xff & hardwareAddress[5]);
	            	x=x<<8;
	            	x=x|(0xff & messageID);
	            	messageID++;
	            	return x;
	            }
	        }		
		}
		catch(Exception e){
			echoClient.writeClientMessage("UdpP2P.getNewID error:"+e);
		}
		return 0;
	}

}
