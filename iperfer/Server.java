package P1.iperfer;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port;

    public Server(int port) {
        this.port = port;
    }

    private void printSummary(long time, long data) {
        time = System.currentTimeMillis() - time;
        time = time / 1000;
        data = data / 1000;
        double trafficRate = (data * 8) / (time * 1000);
        System.out.println("received=" + data + " KB rate=" + String.format("%.3f", trafficRate) + " Mbps");
    }

    public void establishConnection() throws IOException {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Socket clientSocket = serverSocket.accept();
            byte streamData[] = new byte[1000];
            long data = 0;
            long bytesRead;
            long time = System.currentTimeMillis();

            InputStream inStream = clientSocket.getInputStream();
            while ((bytesRead = inStream.read(streamData)) != -1) {
                data = data + bytesRead;
            }

            printSummary(time, data);
            serverSocket.close();
            clientSocket.close();

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new IOException();
        }
    }
}
