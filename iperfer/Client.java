package P1.iperfer;

import java.util.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
public class Client {

    private String hostname;
    private long time = 0;
    private int portNumber;

    public Client(String hostname, int portNumber, long time) throws IOException {
        this.hostname = hostname;
        this.portNumber = portNumber;
        this.time = time;
    }

    public void requestServer() {
        byte blocks[] = new byte[1000];
        try {
            Socket clientSocket = new Socket(hostname, portNumber);
            OutputStream outStream = clientSocket.getOutputStream();
            long startTime = System.currentTimeMillis();
            long currentTime = 0;
            long endTime = time * 1000;
            int num_bytes = 0;

            while(currentTime - startTime < endTime) {
                outStream.write(blocks, 0, blocks.length);
                num_bytes++;
                currentTime = System.currentTimeMillis();
            }
            clientSocket.close();
            double data = (num_bytes * 8) / (currentTime - startTime);
            System.out.println("sent=" + num_bytes + " KB rate=" + String.format("%.3f", data) + " Mbps");

        } catch(Exception e) {
            e.printStackTrace();
        }
    }



}
