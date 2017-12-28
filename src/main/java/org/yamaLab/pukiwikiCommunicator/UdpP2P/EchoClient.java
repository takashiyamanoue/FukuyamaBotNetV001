package org.yamaLab.pukiwikiCommunicator.UdpP2P;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class EchoClient implements Runnable {
	private UdpP2PClientGui gui;
	private DatagramSocket socket;
	private String server; // change ipaddress.
	private int port = 12345;
	private DatagramPacket sendPacket;
	private Map<String, InetSocketAddress> socketMap = new HashMap<String, InetSocketAddress>();
	private Thread me;
	private UdpP2P main;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new EchoClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setGui(UdpP2PClientGui g, UdpP2P m){
		this.gui=g;
		this.main=m;
		g.setClient(this);
	}
	public EchoClient()  {
	}
	public void setServerAddress(){
		String serverAddress=gui.getServerAddress();
		if(serverAddress==null) return;
		if(serverAddress.equals("")) return;
		setServerAddress(serverAddress);
	}
	public void setServerAddress(String x){
		this.stop();
		if(socket!=null){
			   socket.close();
			   socket=null;
		}
		clearAddressMap();
		if(socketMap!=null){
		   socketMap.clear();
		}
		else{
			socketMap=new HashMap();
		}
		server=x;
		try{
		    socket = new DatagramSocket();
		    socket.setReuseAddress(true);
		}
		catch(Exception e){
			gui.writeClientMessage("EchoClient new DatagramSocket error:"+e);			
		}
		gui.writeClientMessage("start client.");
		if(server!=null){
			gui.writeClientMessage("new sendPacket.");
		    try{
		      sendPacket = new DatagramPacket("test".getBytes(), 0, "test".getBytes().length, new InetSocketAddress(server, port));
		    }
		    catch(Exception e){
			   System.out.println("EchoClient new datagram Packet error:"+e);
		    }
		    gui.writeClientMessage("send.");
		    try{
		       socket.send(sendPacket);
		    }
		    catch(Exception e){
			    gui.writeClientMessage("EchoClient constructor error:"+e);
		    }
		}
		else{
			gui.writeClientMessage("server==null");			
		}
		this.start();		
	}
	public void join(String x){
		gui.setServerAddress(x);
		gui.serverAddressSetButtonActionPerformed(null);
	}
	public void send(String x){
		gui.setSendMessage(x);
		gui.clientSendButtonActionPerformed(null);
	}	

	public void writeClientMessage(String line){
		// 送信動作
		System.out.println("line data:" + line);
		if(gui!=null){
			gui.writeClientMessage(line);
		}
		for(String key : socketMap.keySet()) {
			try{
			sendPacket = new DatagramPacket(line.getBytes(), 0, line.getBytes().length, socketMap.get(key));
			for(int i=0;i<4;i++) socket.send(sendPacket);
			}
			catch(Exception e){
				gui.writeClientMessage("error:"+e);
			}
		}
		
	}
	public void writeClientMessageExcept(String line, InetSocketAddress a){
		// 送信動作
		System.out.println("line data:" + line);
		if(gui!=null){
			gui.writeClientMessage(line);
		}
		for(String key : socketMap.keySet()) {
			try{
				InetSocketAddress x=socketMap.get(key);
				if(!x.equals(a)){ // do not return the same packet to the direct sender.
			      sendPacket = new DatagramPacket(line.getBytes(), 0, line.getBytes().length, socketMap.get(key));
			      for(int i=0;i<4;i++) socket.send(sendPacket);
				}
			}
			catch(Exception e){
				gui.writeClientMessage("error:"+e);
			}
		}
		
	}	
	public void writeClientMessageTo(String line, InetSocketAddress a){
		// 送信動作
		System.out.println("line data:" + line);
		if(gui!=null){
			gui.writeClientMessage(line);
		}
		try{
	      sendPacket = new DatagramPacket(line.getBytes(), 0, line.getBytes().length, a);
	      for(int i=0;i<4;i++) socket.send(sendPacket);
		}
		catch(Exception e){
			gui.writeClientMessage("error:"+e);
		}
	}		

	public void run() {
		DatagramPacket recvPacket;
		DatagramPacket sendPacket;
		gui.writeClientMessage("start echoclient, loop for waiting message from server");		
		while(me!=null) {
			try {
				recvPacket = new DatagramPacket(new byte[1024], 1024);
				socket.receive(recvPacket);
				String data = new String(recvPacket.getData(), 0, recvPacket.getLength()).trim();
				String line="address:" + recvPacket.getAddress() + " port:" + recvPacket.getPort();
				gui.writeClientMessage(line);
				gui.writeClientMessage("data:"+data);
				if(data.startsWith("broadcast ")){
					InetSocketAddress sockaddress = new InetSocketAddress(recvPacket.getAddress(), recvPacket.getPort());
					main.parseBroadcastCommand(data.substring("broadcast ".length()),sockaddress);
					this.writeClientMessageTo("ack", sockaddress);
				}
				if(data.startsWith("keepAlive")){
					InetSocketAddress sockaddress = new InetSocketAddress(recvPacket.getAddress(), recvPacket.getPort());
					this.writeClientMessageTo("ack", sockaddress);
				}
				if(data.startsWith("init")){
					InetSocketAddress sockaddress = new InetSocketAddress(recvPacket.getAddress(), recvPacket.getPort());
					this.writeClientMessageTo("ack", sockaddress);
				}								
				if(data.startsWith("clearAddressMap")) {
					gui.writeClientMessage("clearAddressMap");
					clearAddressMap();
				}		
				if(data.startsWith("/") && data.contains(":")) {
					gui.writeClientMessage("this data for socket Connection");
					String key = data;
					String[] serverData = key.substring(1).split(":");
					String server = serverData[0];
					int port = Integer.parseInt(serverData[1]);
					InetSocketAddress address = new InetSocketAddress(server, port);
					sendPacket = new DatagramPacket("init".getBytes(), 0, "init".getBytes().length, address);
					socket.send(sendPacket);
					socketMap.put(key, address);
					updateNextGui();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void clearAddressMap(){
		int ix=0;
		for(String key : socketMap.keySet()) {
			gui.setIpPort(ix, "", "", "");
			ix++;
		}	
		socketMap.clear();
	}
	private void updateNextGui(){
		int ix=0;
		for(String key : socketMap.keySet()) {
			StringTokenizer st=new StringTokenizer(key,":");
			String ipx=st.nextToken();
			String ps=st.nextToken();
			gui.setIpPort(ix, ipx, ps, "client");
			ix++;
		}		
	}
	private void start(){
		if(me==null){
			me=new Thread(this,"udp-echo-client");
			me.start();
		}
	}
	private void stop(){
		me=null;

	}
	public String getLog(){
		return "";
	}
}