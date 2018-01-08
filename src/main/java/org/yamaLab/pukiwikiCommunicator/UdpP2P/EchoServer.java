package org.yamaLab.pukiwikiCommunicator.UdpP2P;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class EchoServer implements Runnable {
	private UdpP2PServerGui gui;
	private DatagramSocket socket;
	private Map<String, InetSocketAddress> addressMap;
	private Thread me;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new EchoServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public EchoServer()  {
	}
	public void setGui(UdpP2PServerGui g){
		this.gui=g;
		g.setServer(this);
		init();
	}	
	public void stop(){
		me=null;
	}
	public void init(){
		if(addressMap==null){
		   addressMap=new HashMap();
		}
		int ix=0;
		for(String key : addressMap.keySet()) {
			gui.setIpPort(ix, "", "", "");
			ix++;
		}	
		addressMap.clear();
		this.stop();
		if(socket!=null){
		    socket.close();
		    socket=null;
		}		
		try{
		socket = new DatagramSocket(12345);
		socket.setReuseAddress(true);
		gui.writeServerMessage("start server.");
		Enumeration e = NetworkInterface.getNetworkInterfaces();
		while(e.hasMoreElements())
		{
		    NetworkInterface n = (NetworkInterface) e.nextElement();
		    Enumeration ee = n.getInetAddresses();
		    while (ee.hasMoreElements())
		    {
		        InetAddress i = (InetAddress) ee.nextElement();
		        gui.writeServerMessage("myip="+i.getHostAddress());
		    }
		}
		
		String myaddress=(socket.getLocalSocketAddress()).toString();
		gui.writeServerMessage("myaddress="+myaddress);
		}
		catch(Exception e){
			gui.writeServerMessage("EchoServer error:"+e);
		}
		this.start();		
	}
	public void run() {
		DatagramPacket recvPacket;
		while(me!=null) {
			try {
				recvPacket = new DatagramPacket(new byte[1024], 1024);
				socket.receive(recvPacket);
				String ap=" address:" + recvPacket.getAddress() + " port:" + recvPacket.getPort();
				gui.writeServerMessage(ap);
				String receivedData=new String(recvPacket.getData(), 0, recvPacket.getLength()).trim();
				String line="received data:" + receivedData+" from "+ap;
				gui.writeServerMessage(line);
				String recvkey = recvPacket.getAddress() + ":" + recvPacket.getPort();
				InetSocketAddress address = new InetSocketAddress(recvPacket.getAddress(), recvPacket.getPort());
				if(receivedData.startsWith("init.")){
					this.updateHoleList(recvPacket, receivedData, recvkey, address);
				}
				if(receivedData.startsWith("ack.")){
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void updateHoleList(DatagramPacket d,String x, String recvkey, InetSocketAddress address){
		DatagramPacket sendPacket;		
//		if(!addressMap.containsKey(recvkey)) {
			gui.writeServerMessage("new server key:" + recvkey);
			int ix=0;
			try{
			
			// 全ノードの udp hole list をクリア
			for(String key : addressMap.keySet()) {
				 gui.writeServerMessage("clearAddressMap:" + key);
				 sendPacket = new DatagramPacket("clearAddressMap.".getBytes(), 0, "clearAddressMap.".getBytes().length, addressMap.get(key));
//				 for(int i=0;i<4;i++)
				 socket.send(sendPacket);
				 gui.setIpPort(ix, "", "", "server");
				 ix++;
			}
			// addressMap に新しい client の udp hole を追加。
			addressMap.put(recvkey, address);	
			// 全クライアントに新しい人が追加されたことを通知しておく。			
			for(String key : addressMap.keySet()) {
				gui.writeServerMessage("known key:" + key);
				for(String key2:addressMap.keySet()){
				   sendPacket = new DatagramPacket(key2.getBytes(), 0, key2.getBytes().length, addressMap.get(key));
				   //for(int i=0;i<3;i++) 
					   socket.send(sendPacket);
				}
			  }
			// GUIのUDP hole list を更新。

			  ix=0;					
			  for(String key : addressMap.keySet()) {
				 StringTokenizer st=new StringTokenizer(key,":");
				 String ips=st.nextToken();
				 String ps=st.nextToken();						
				 String memo="server";
				 if(key.equals(recvkey)){
					memo="new server";
				 }
				 gui.setIpPort(ix, ips, ps, memo);
				 sendPacket = new DatagramPacket("updateGui.".getBytes(), 0, "updateGui.".getBytes().length, addressMap.get(key));
				   //for(int i=0;i<3;i++) 
				   socket.send(sendPacket);				 
				 ix++;
			  }
			}
			catch(Exception e){
				gui.writeServerMessage("updateHoleList error:"+e);
			}
//		  }
			// 新 client に、サーバーから見た、新クライアントの hole を通知。
		  String registAck="yourHole "+recvkey;
		  try{
		  sendPacket = new DatagramPacket(registAck.getBytes(), 0, registAck.length(), address);
		  //for(int i=0;i<4;i++) 
			  socket.send(sendPacket);
		  }
		  catch(Exception e){
				gui.writeServerMessage("updateHoleList write hole error:"+e);			  
		  }
	}
	private void start(){
		if(me==null){
			me=new Thread(this,"echo-server-thread");
			me.start();
		}
	}
	public String getLog(){
		return "";
	}	
}