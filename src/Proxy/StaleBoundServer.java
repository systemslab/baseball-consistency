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

public class StaleBoundServer extends UnicastRemoteObject implements Client_Proxy_interface {
	int cluster_size = 2;
	Proxy_Cluster_interface pci[] = new Proxy_Cluster_interface[cluster_size];
	static HashMap<Integer, Lock> hashMap = new HashMap<Integer, Lock>();
	long staleBoundLimit = 0, readTime = 0,count = 0;
	HashMap<Integer, Integer> localStore[] = new HashMap[cluster_size];
	protected StaleBoundServer() throws RemoteException {
		try {
			pci[0] = (Proxy_Cluster_interface)Naming.lookup("cluster0");
			pci[1] = (Proxy_Cluster_interface)Naming.lookup("cluster1");
			for(int i = 0; i<cluster_size;i++){
				localStore[i] = new HashMap<Integer, Integer>();
			}
		} catch (MalformedURLException | NotBoundException e) {
			e.printStackTrace();
		}
		System.out.println("Server started");
	}

	public static void main(String[] args) {
		try {
			StaleBoundServer p = new StaleBoundServer();
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
				localStore[0].put(key, localStore[0].get(key)+1);
				try {
					Thread.sleep(23);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				pci[1].put(key, value);
				localStore[1].put(key, localStore[1].get(key)+1);
				try {
					Thread.sleep(23);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				lock.unlock();
				//System.out.println("key "+key+ " released");
				break;
			} 
		}
		return true;
	}

	public String get(int key) throws RemoteException {
		long nanos = ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId());
		String str = null;
		int index = (int)(Math.random()*2);
		int max = findMax(key);
			if((max - localStore[index].get(key)) <= staleBoundLimit){
				str = pci[index].get(key);
				try {
					Thread.sleep(23);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else{
				count++;
				System.out.println("\u001B31;1mnot stale bounded read");
				while(true){
					index = (index+1)%2;
					if((max - localStore[index].get(key)) <= staleBoundLimit){
						break;
					}
				}
				str = pci[index].get(key);
				try {
					Thread.sleep(23);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
		
		long nanos2 = ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId());
		System.out.println("time taken for read with key "+key+" is "+ (nanos2-nanos));
		readTime = readTime + nanos2 - nanos;
		System.out.println(readTime+ " not stale bounded is "+count);
		return str;
	
	}
	
	int findMax(int key){
		int max = 0;
		for(int i = 0; i<cluster_size; i++){
			if(max<localStore[i].get(key)){
				max = localStore[i].get(key);
			}
		}
		return max;
	}
	
	public String getWithLock(int key, Lock lock) throws RemoteException{
		String str = null;
		/*long nanos = ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId());
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
		System.out.println("total time is: "+readTime);*/

		return str;
	}
	
	public synchronized Lock getLock(int key){
		Lock lock = null;
		if((lock = hashMap.get(key)) != null){
			return lock;	
		}
		lock = new ReentrantLock();
		hashMap.put(key, lock);
		for(int i = 0; i < cluster_size; i++)
		{
			localStore[i].put(key, 0);	
		}
		return lock;
	}
}
