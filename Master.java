
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;


public interface Master extends Remote {
	public void registerDatanode(Node n) throws RemoteException;
	public Collection<Node> getAllDatanode() throws RemoteException;
	public void registerData(int iddata, int datasize) throws RemoteException;
	public int getDataSize(int iddata) throws RemoteException;
}