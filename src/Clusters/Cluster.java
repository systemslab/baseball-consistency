package Clusters;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Cluster extends UnicastRemoteObject implements Proxy_Cluster_interface{
	int clusterNumber = 0;
	private final Lock lock = new ReentrantLock();
	HashMap<Integer, String> hashMap = new HashMap<>();
	protected Cluster() throws RemoteException {
		System.out.println("cluster initiated");
	}

	public static void main(String[] args) {
		Cluster cluster1, cluster2;
		try {
			cluster1 = new Cluster();
			cluster1.clusterNumber = 0;
			cluster2 = new Cluster();
			cluster2.clusterNumber = 1;
			Naming.rebind("cluster0", cluster1);
			Naming.rebind("cluster1", cluster2);
		} catch (RemoteException | MalformedURLException e) {
			e.printStackTrace();
		}

	}

	public boolean takeKey() throws RemoteException {
		return lock.tryLock();
	}
	

	@Override
	public void releaseKey() throws RemoteException {
		System.out.println(" key released for " + clusterNumber);
		lock.unlock();
	}
	
	@Override
	public int put(int key, String value) throws RemoteException {
		System.out.println("cluster "+clusterNumber + "  " +value);
		hashMap.put(key, value);
		return 0;
	}

	@Override
	public String get(int key) throws RemoteException {
		return hashMap.get(key);
	}


}
