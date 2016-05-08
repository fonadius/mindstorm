package cz.muni.ia158.PongRobot.tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPConnection {

	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private InputStreamReader isReader;
	private OutputStreamWriter osWriter;

	private void createReaderWriter() throws IOException {
		isReader = new InputStreamReader(socket.getInputStream());
		reader = new BufferedReader(isReader);
		osWriter = new OutputStreamWriter(socket.getOutputStream());
		writer = new BufferedWriter(osWriter);
	}

	public TCPConnection(Socket socket) throws IOException {
		this.socket = socket;
		createReaderWriter();
	}

	public TCPConnection(String server, int port) throws UnknownHostException, IOException {
		socket = new Socket(server, port);
		createReaderWriter();
	}

	public void write(String str) throws IOException {
		if(!str.endsWith("\n")){
			str += "\n";
		}
		writer.append(str);
		writer.flush();
	}


	public String readLine() throws IOException {
		return reader.readLine();
	}
	/*
	 * blocking call, probably not most efficient solution..
	 */
	public String readLineBlocking() throws IOException {
		while (!readerReady()){
			
		}
		return reader.readLine();
	}
	public boolean readerReady() throws IOException {
		return socket.getInputStream().available() > 0 || reader.ready();
	}

	public void close() throws IOException {
		reader.close();
		writer.close();
		socket.close();
	}

	public boolean closed() {
		return socket.isClosed();
	}

}
