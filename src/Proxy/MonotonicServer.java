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

public class MonotonicServer extends UnicastRemoteObject implements Client_Proxy_interface {
	
	Proxy_Cluster_interface pci[] = new Proxy_Cluster_interface[2];
	static HashMap<Integer, Lock> hashMap = new HashMap<>();
	long readTime = 0;
	static int count = 0;
	HashMap<Integer, String> localStore = new HashMap<>();
	protected MonotonicServer() throws RemoteException {
		try {
			pci[0] = (Proxy_Cluster_interface)Naming.lookup("cluster0");
			pci[1] = (Proxy_Cluster_interface)Naming.lookup("cluster1");
		} catch (MalformedURLException | NotBoundException e) {
			e.printStackTrace();
		}
		System.out.println("Monotonic Server started");
	}

	public static void main(String[] args) {
		try {
			MonotonicServer p = new MonotonicServer();
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
				try {
					Thread.sleep(46);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
/*				try {
					Thread.sleep(23);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}*/
				pci[1].put(key, value);
				
				lock.unlock();
				//System.out.println("key "+key+ " released");
				break;
			} 
		}
		return true;
	}

	public String get(int key) throws RemoteException {

		// Eventually consistent read
		long nanos = ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId());
		String str = null;
		int index = (int)(Math.random()*2);
		while(true){
			str = pci[index].get(key);
			try {
				Thread.sleep(23);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(Integer.parseInt(localStore.get(key))<=Integer.parseInt(str)){
				localStore.put(key, str);
				break;
			} else{
				System.out.println("not monotonic read");
				index = (index+1)%2;
				count++;
			}
			
		}
		
		long nanos2 = ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId());
		System.out.println("time taken for read with key "+key+" is "+ (nanos2-nanos));
		/*if(!str.equals(localStore.get(key))){
			System.out.println("Not Latest data  " + str + "  "+localStore.get(key));
		}*/
		readTime = readTime + nanos2 - nanos;
		System.out.println("total time taken for monotonic reads "+(readTime + count*23000000) + " with read agains "+count);
		return str;
	}
	
	public synchronized Lock getLock(int key){
		Lock lock = null;
		if((lock = hashMap.get(key)) != null){
			return lock;	
		}
		lock = new ReentrantLock();
		hashMap.put(key, lock);
		localStore.put(key, "0");
		return lock;
	}
}
