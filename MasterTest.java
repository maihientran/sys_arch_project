import java.rmi.Naming;

public class MasterTest {
    public static void main(String[] args) {
        try {
            Master master = (Master) Naming.lookup("rmi://localhost:1099/Master");
            int dataSize = master.getDataSize(1);
            System.out.println("Master server is running and responded with data size: " + dataSize);
        } catch (Exception e) {
            System.err.println("Error connecting to Master server: " + e.getMessage());
        }
    }
}
