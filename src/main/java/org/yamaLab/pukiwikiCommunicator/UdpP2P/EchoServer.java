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
		if(socket!=null){
		    socket.close();
		}
	}
	public void init(){
		if(addressMap==null){
		   addressMap=new HashMap();
		}
		addressMap.clear();
		this.stop();
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
		DatagramPacket sendPacket;
		while(me!=null) {
			try {
				recvPacket = new DatagramPacket(new byte[1024], 1024);
				socket.receive(recvPacket);
				String line="address:" + recvPacket.getAddress() + " port:" + recvPacket.getPort();
				gui.writeServerMessage(line);
				line="data:" + new String(recvPacket.getData(), 0, recvPacket.getLength()).trim();
				gui.writeServerMessage(line);
				String recvkey = recvPacket.getAddress() + ":" + recvPacket.getPort();
				InetSocketAddress address = new InetSocketAddress(recvPacket.getAddress(), recvPacket.getPort());
				if(!addressMap.containsKey(recvkey)) {
					gui.writeServerMessage("new server key:" + recvkey);
					// 前クライアントに新しい人が追加されたことを通知しておく。
					int ix=0;
					for(String key : addressMap.keySet()) {
						gui.writeServerMessage("clearAddressMap:" + key);
						sendPacket = new DatagramPacket("clearAddressMap".getBytes(), 0, "clearAddressMap".getBytes().length, addressMap.get(key));
						for(int i=0;i<4;i++) socket.send(sendPacket);
						gui.setIpPort(ix, "", "", "server");
						ix++;
					}
					
					ix=0;
					for(String key : addressMap.keySet()) {
						gui.writeServerMessage("known key:" + key);
						sendPacket = new DatagramPacket(recvkey.getBytes(), 0, recvkey.getBytes().length, addressMap.get(key));
						for(int i=0;i<4;i++) socket.send(sendPacket);
						sendPacket = new DatagramPacket(key.getBytes(), 0, key.getBytes().length, address);
						for(int i=0;i<4;i++) socket.send(sendPacket);
						StringTokenizer st=new StringTokenizer(key,":");
						String ips=st.nextToken();
						String ps=st.nextToken();
						gui.setIpPort(ix, ips, ps, "server");
						ix++;
					}
					// あたらしいクライアントが追加された。
					addressMap.put(recvkey, address);
				}
				sendPacket = new DatagramPacket(recvPacket.getData(), 0, recvPacket.getLength(), address);
				socket.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
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