package br.ucsal.central.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Container {

	private int id;
	private InputStream inputStream;
	private OutputStream outputStream;

	public Container(int id, Socket socket) throws IOException {
		this.id = id;
		this.inputStream = socket.getInputStream();
		this.outputStream = socket.getOutputStream();
	}

	public int getId() {
		return id;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

}
