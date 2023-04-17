import java.net.*;
import java.rmi.Naming;
import java.util.*;

public class DataNode {
    private String hostname;
    private int port;
    private List<DataNode> peers;

    public DataNode(int port) throws UnknownHostException {
        this.hostname = InetAddress.getLocalHost().getHostAddress();
        this.port = port;
        this.peers = new ArrayList<>();
    }

    public void registerWithMaster() {
        try {
            Master master = (Master) Naming.lookup("rmi://localhost:1099/Master");
            Node node = new Node(hostname,port);
            master.registerDatanode(node);
            Collection<Node> allNodes = master.getAllDatanode();
            List<DataNode> dataNodes = new ArrayList<>();
            for (Node n : allNodes) {
                if (!n.equals(node)) { // only add peer nodes to the list
                    dataNodes.add(new DataNode(n.getPort()));
                }
            }
            peers.addAll(dataNodes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        if (args.length < 1) {
            System.out.println("Usage: java DataNode <port>");
            return;
        }
        int port = Integer.parseInt(args[0]);
        DataNode dataNode = new DataNode(port);
        dataNode.registerWithMaster();
//        try {
//            ServerSocket ss = new ServerSocket(port);
//            while (true) {
//                Socket s = ss.accept();
//                InputStream is = s.getInputStream();
//                ObjectInputStream ois = new ObjectInputStream(is);
//                int iddata = ois.readInt();
//                int size = ois.readInt();
//                System.out.println("ID: " + iddata + ", Size: " + size);
//
//                s.close();
//                String filename = "file-"+iddata+ "-";
//                Storage.putData(iddata, filename); 
//                Storage.getData(iddata, filename);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
