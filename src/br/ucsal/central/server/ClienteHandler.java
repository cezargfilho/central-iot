package br.ucsal.central.server;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.TimerTask;

public class ClienteHandler extends TimerTask implements Runnable {

	// Classe para auxiliar no suporte a multiplos clientes

	private Servidor servidor;
	private OutputStream clienteInput;
	private String mensagem;

	public ClienteHandler(OutputStream outputStream, Servidor servidor, String mensagem) {
		this.servidor = servidor;
		this.clienteInput = outputStream;
		this.mensagem = mensagem;
	}

	@Override
	public void run() {
		new PrintStream(this.clienteInput).println(mensagem);
	}
}