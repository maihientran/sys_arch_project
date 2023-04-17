import java.io.*;
import java.rmi.Naming;
import java.util.*;

public class Storage {

	public static void putData(int iddata, String filename) {
	    try {
	        // Read file content
	        File file = new File(filename);
	        int fileSize = (int) file.length();
	        FileInputStream fis = new FileInputStream(file);
	        byte[] buffer = new byte[fileSize];

	        // Register data size with Master
	        Master master = (Master) Naming.lookup("rmi://localhost:1099/Master");
	        master.registerData(iddata, fileSize);
	        System.out.println("Data registered with Master");

	        // Replicate file onto all DataNodes
	        Collection<Node> datanodes = master.getAllDatanode();
	        for (Node datanode : datanodes) {
	            try {
	                FileOutputStream fos = new FileOutputStream(filename + "-" + datanode.getPort(), true); // Open file in append mode
	                while (fis.available() > 0) {
	                    int bytesRead = fis.read(buffer);
	                    fos.write(buffer, 0, bytesRead);
	                }
	                fos.close();
	                System.out.println("Data replicated to " + datanode.getHost() + ":" + datanode.getPort());
	                fis.getChannel().position(0); // Reset the input stream position to 0
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        fis.close();
	        System.out.println("putData: File read. Size: " + fileSize + " bytes");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public static void getData(int iddata, String filename) {
	    try {
	        // Read file content
	        File file = new File(filename);
	        int fileSize = (int) file.length();
	        FileInputStream fis = new FileInputStream(file);

	        // Get the data size from the Master
	        Master master = (Master) Naming.lookup("rmi://localhost:1099/Master");
	        master.registerData(iddata, fileSize);
	        System.out.println("Data registered with Master");

	        // Divide the data size into N blocks
	        Collection<Node> datanodes = master.getAllDatanode();
	        int numBlocks = datanodes.size();
	        int bytesPerBlock = fileSize / numBlocks;
	        int bytesLeft = fileSize % numBlocks;
	        System.out.println("getData: fileSize= " + fileSize + " bytes");
	        System.out.println("getData: numBlocks=" + numBlocks + ", blockSize=" + bytesPerBlock + " bytes");

	        // Download each block from a different datanode
	        List<Thread> downloadThreads = new ArrayList<>();
	        for (int i = 0; i < numBlocks; i++) {
	            final int blockId = i + 1;
	            Thread downloadThread = new Thread(() -> {
	                try {
	                    // Read block content from file
	                    String blockFilename = "foo" + blockId;
	                    FileOutputStream fos = new FileOutputStream(blockFilename);
	                    int blockSize = bytesPerBlock + ((blockId-1) < bytesLeft ? 1 : 0);
	                    int bytesLeftInBlock = blockSize;
	                    while (bytesLeftInBlock > 0) {
	                        int bufferSize = Math.min(bytesLeftInBlock, fis.available());
	                        byte[] buffer = new byte[bufferSize];
	                        int bytesRead = fis.read(buffer);
	                        fos.write(buffer, 0, bytesRead);
	                        bytesLeftInBlock -= bytesRead;
	                    }
	                    fos.close();
	                    System.out.println("Block " + blockId + " written to " + blockFilename);
	                    fis.getChannel().position(0); // Reset the input stream position to 0
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            });
	            downloadThreads.add(downloadThread);
	            downloadThread.start();
	        }

	        // Wait for all download threads to finish
	        for (Thread downloadThread : downloadThreads) {
	            downloadThread.join();
	        }
	        fis.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}
