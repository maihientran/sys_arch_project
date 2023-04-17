import java.rmi.Naming;
import java.util.Collection;

public class Test {
    public static void main(String[] args) {
        // Print the ports of the connected DataNodes
        try {
            Master master = (Master) Naming.lookup("rmi://localhost:1099/Master");
            Collection<Node> nodes = master.getAllDatanode();
            System.out.println("Connected DataNodes: ");
            for (Node node : nodes) {
                System.out.println(node.getPort());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //Storage.putData(14, "file");
        // the local file "filesample" is replicated in each DataNode,
        // for instance with name "file14.txt"
        // the file size is also registered in the Master
        long t1 = System.currentTimeMillis();
        Storage.getData(14, "data");
        long t2 = System.currentTimeMillis();
        System.out.println("time in ms =" + (t2 - t1));
        // the file stored with iddata 14 is downloaded in parallel, one block per DataNode
        // when the download is completed, we have a set of file foo1, foo2, foo3 ...

    }
}

