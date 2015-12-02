package Clients;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;

import Proxy.Client_Proxy_interface;

public class ConsistentPrefixClient implements Runnable{
	static ConsistentPrefixClient cl;
	public static void main(String args[])
	{
		cl = new ConsistentPrefixClient();
		try	{
			Client_Proxy_interface cpinterface = (Client_Proxy_interface)(Naming.lookup("client_proxy"));
			int i = 10;
		//	long currentTimeinMillis = System.currentTimeMillis();
			while(i>0)
			{
				i--;
				cpinterface.put(i, Thread.currentThread().getName() + " index "+i);	
			}
		//	System.out.println(Thread.currentThread().getName() + " time taken is " + (System.currentTimeMillis() - currentTimeinMillis));
		} catch (IOException | NotBoundException e) {
			e.printStackTrace();
		}
		cl.startWriteClients();
		cl.startReadClients();
	}
	
	void startWriteClients(){
		Thread clientThread1 = new Thread(new ConsistentPrefixClient());
		clientThread1.setName("Thread1");
		clientThread1.start();
		/*Thread clientThread2 = new Thread(new ConsistentPrefixClient());
		clientThread2.setName("Thread2");
		clientThread2.start();*/
	}
	
	void startReadClients(){
		Thread strongReadThread1 = new Thread(new Runnable() {
			public void run() {
				try	{
					Client_Proxy_interface cpinterface = (Client_Proxy_interface)(Naming.lookup("client_proxy"));
					int i = 100;
					long currentTimeinMillis = System.currentTimeMillis();
					while(i>0)
					{
						i--;
						System.out.println("read" + cpinterface.get((int)(Math.random()*10)));
					}
					System.out.println(Thread.currentThread().getName() + " time taken is " + (System.currentTimeMillis() - currentTimeinMillis));
				} catch (IOException | NotBoundException e) {
					e.printStackTrace();
				}
			}
		});
		strongReadThread1.setName("ConsistentPrefixThread1");
		strongReadThread1.start();
	}
	
	@Override
	public void run() {
		makeConnection();	
	}
	
	void makeConnection(){
	try	{
			Client_Proxy_interface cpinterface = (Client_Proxy_interface)(Naming.lookup("client_proxy"));
			int i = 100;
		//	long currentTimeinMillis = System.currentTimeMillis();
			while(i>0)
			{
				i--;
				cpinterface.put((int)(Math.random()*10), Thread.currentThread().getName() + " index "+i);	
			}
		//	System.out.println(Thread.currentThread().getName() + " time taken is " + (System.currentTimeMillis() - currentTimeinMillis));
		} catch (IOException | NotBoundException e) {
			e.printStackTrace();
		}
		
	}

}
