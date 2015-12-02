package Proxy;

import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Clusters.Proxy_Cluster_interface;

public class ProxyServer extends UnicastRemoteObject implements Client_Proxy_interface {
	
	Proxy_Cluster_interface pci[] = new Proxy_Cluster_interface[2];
	static HashMap<Integer, Lock> hashMap = new HashMap<>();
	long readTime = 0;
	HashMap<Integer, String> localStore = new HashMap<>();
	protected ProxyServer() throws RemoteException {
		try {
			pci[0] = (Proxy_Cluster_interface)Naming.lookup("cluster0");
			pci[1] = (Proxy_Cluster_interface)Naming.lookup("cluster1");
		} catch (MalformedURLException | NotBoundException e) {
			e.printStackTrace();
		}
		System.out.println("Server started");
	}

	public static void main(String[] args) {
		try {
			ProxyServer p = new ProxyServer();
			Naming.rebind("client_proxy", p);
		} catch (RemoteException | MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public int put(int key, String value) throws RemoteException {
		Lock lock = null;
		if((lock = hashMap.get(key)) == null){
			lock = getLock(key);
		}
		putWithLock(key, value, lock);	
		return 0;
	}
	
	public boolean putWithLock(int key, String value, Lock lock) throws RemoteException{
		while (true) {
			if (lock.tryLock()) {
				pci[0].put(key, value);
				localStore.put(key, value);
				try {
					Thread.sleep(23);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				pci[1].put(key, value);
				try {
					Thread.sleep(23);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				lock.unlock();
				/*synchronized (lock) {
					lock.notifyAll();	
				}*/
				//System.out.println("key "+key+ " released");
				break;
			}/*else{
				try {
					synchronized (lock) {
						lock.wait();	
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
		}
		return true;
	}

	public String get(int key) throws RemoteException {
		//String consistent read
		Lock lock = null;
		if((lock = hashMap.get(key)) != null){
			lock = getLock(key);
			return getWithLock(key, lock);
		}
		System.out.println("not yet there");
		return "not yet there";

		// Eventually consistent read
		/*long nanos = ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId());
		String str = pci[(int)(Math.random()*2)].get(key);
		try {
			Thread.sleep(23);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long nanos2 = ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId());
		System.out.println("time taken for read with key "+key+" is "+ (nanos2-nanos));
		if(!str.equals(localStore.get(key))){
			System.out.println("Not Latest data  " + str + "  "+localStore.get(key));
		}
		readTime = readTime + nanos2 - nanos;
		System.out.println(readTime);
		return str;*/
	}
	
	public String getWithLock(int key, Lock lock) throws RemoteException{
		String str = null;
		long nanos = ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId());
		while (true) {
			if (lock.tryLock()) {
				str = pci[(int)(Math.random()*2)].get(key);
				if(!str.equals(localStore.get(key))){
					System.out.println("Not Latest data  " + str + "  "+localStore.get(key));
				}
				try {
					Thread.sleep(23);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				lock.unlock();
				break;
			}
		//	System.out.println("locked ");
		}
		long nanos2 = ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId());
		System.out.println("time taken for strong read with key "+key+" is "+ (nanos2-nanos));
		readTime = readTime + nanos2 - nanos;
		System.out.println("total time is: "+readTime);

		return str;
	}
	
	public synchronized Lock getLock(int key){
		Lock lock = null;
		if((lock = hashMap.get(key)) != null){
			return lock;	
		}
		lock = new ReentrantLock();
		hashMap.put(key, lock);
		return lock;
	}
}
