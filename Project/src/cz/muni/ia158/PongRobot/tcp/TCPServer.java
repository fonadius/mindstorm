package cz.muni.ia158.PongRobot.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;

public class TCPServer {

    private ServerSocket serverSocket;

	public TCPServer(int port) throws IOException {

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            throw new IOException("TCP-Port nelze otevrit: " + port + "; ", ex);
        }
	}


    public TCPConnection waitForConnection() throws IOException {
        Socket socket = null;
        socket = serverSocket.accept();
        return new TCPConnection(socket);
    }


}
