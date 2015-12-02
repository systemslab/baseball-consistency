package Clusters;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Proxy_Cluster_interface extends Remote {
	public int put(int key, String value) throws RemoteException;
	public String get(int key) throws RemoteException;
	public boolean takeKey() throws RemoteException;
	public void releaseKey() throws RemoteException;
	
}
