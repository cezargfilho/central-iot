package br.ucsal.central.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/*
 * Comunicacao com a central> LIVRE ID_CAMINHAO (int) 
 */
public class Caminhao {

	private int id;
	private OutputStream outStream;
	private InputStream inputStream;
	private String condicao;
	private Container container;

	public Caminhao(int id, Socket socket) throws IOException {
		this.id = id;
		this.outStream = socket.getOutputStream();
		this.inputStream = socket.getInputStream();
		this.condicao = "LIVRE".toUpperCase();
	}

	public int getId() {
		return id;
	}

	public OutputStream getOutStream() {
		return outStream;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public String getCondicao() {
		return condicao;
	}

	public void setCondicao(String condicao) {
		this.condicao = condicao;
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

}
