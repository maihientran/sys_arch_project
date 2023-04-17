

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;


public class MasterImpl extends UnicastRemoteObject implements Master {
    private Collection<Node> datanodes;
    private Collection<DataInfo> data;

    public MasterImpl() throws RemoteException {
        datanodes = new ArrayList<>();
        data = new ArrayList<>();
    }

    //@Override
    public void registerDatanode(Node n) throws RemoteException {
        datanodes.add(n);
    }

    //@Override
    public Collection<Node> getAllDatanode() throws RemoteException {
        return datanodes;
    }

    //@Override
    public void registerData(int iddata, int datasize) throws RemoteException {
        data.add(new DataInfo(iddata, datasize));
    }

    //@Override
    public int getDataSize(int iddata) throws RemoteException {
        for (DataInfo d : data) {
            if (d.iddata == iddata) {
                return d.datasize;
            }
        }
        return -1;
    }

    private static class DataInfo {
        int iddata;
        int datasize;

        DataInfo(int iddata, int datasize) {
            this.iddata = iddata;
            this.datasize = datasize;
        }
    }
    
    // implementation of Master interface methods
    public static void main(String[] args) {
        try {
            // create a new instance of MasterImpl and bind it to the RMI registry
            MasterImpl master = new MasterImpl();
            Naming.rebind("Master", master);
            System.out.println("Master server ready");
        } catch (Exception e) {
            System.err.println("Master server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
