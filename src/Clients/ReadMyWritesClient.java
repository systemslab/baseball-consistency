package Clients;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;

import Proxy.Client_Proxy_interface;

public class ReadMyWritesClient implements Runnable{
	static ReadMyWritesClient cl;
	public static void main(String args[])
	{
		cl = new ReadMyWritesClient();
		try	{
			Client_Proxy_interface cpinterface = (Client_Proxy_interface)(Naming.lookup("client_proxy"));
			int i = 0;
		//	long currentTimeinMillis = System.currentTimeMillis();
			while(i<10)
			{
				cpinterface.put(i, i+"");
				i++;
			}
		//	System.out.println(Thread.currentThread().getName() + " time taken is " + (System.currentTimeMillis() - currentTimeinMillis));
		} catch (IOException | NotBoundException e) {
			e.printStackTrace();
		}
		cl.startWriteClients();
		cl.startReadClients();
	}
	
	void startWriteClients(){
		Thread clientThread1 = new Thread(new ReadMyWritesClient());
		clientThread1.setName("Thread1");
		clientThread1.start();
		/*Thread clientThread2 = new Thread(new ReadMyWritesClient());
		clientThread2.setName("Thread2");
		clientThread2.start();*/
	}
	
	void startReadClients(){
		Thread strongReadThread1 = new Thread(new Runnable() {
			public void run() {
				try	{
					Client_Proxy_interface cpinterface = (Client_Proxy_interface)(Naming.lookup("client_proxy"));
					int i = 10;
					long currentTimeinMillis = System.currentTimeMillis();
					while(i<110)
					{
						System.out.println("read" + cpinterface.get((int)(Math.random()*10)));
						i++;
					}
					System.out.println(Thread.currentThread().getName() + " time taken is " + (System.currentTimeMillis() - currentTimeinMillis));
				} catch (IOException | NotBoundException e) {
					e.printStackTrace();
				}
			}
		});
		strongReadThread1.setName("ReadMyWritesThread1");
		strongReadThread1.start();
	}
	
	@Override
	public void run() {
		makeConnection();	
	}
	
	void makeConnection(){
	try	{
			Client_Proxy_interface cpinterface = (Client_Proxy_interface)(Naming.lookup("client_proxy"));
			int i = 10;
		//	long currentTimeinMillis = System.currentTimeMillis();
			while(i<110)
			{
				cpinterface.put((int)(Math.random()*10), i+"");
				i++;
			}
		//	System.out.println(Thread.currentThread().getName() + " time taken is " + (System.currentTimeMillis() - currentTimeinMillis));
		} catch (IOException | NotBoundException e) {
			e.printStackTrace();
		}
		
	}

}
