package br.ucsal.central.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.ucsal.central.model.Caminhao;
import br.ucsal.central.model.Container;

public class Servidor {

	private static final long SEGUNDO = 10000;
	private static final String LIVRE = "LIVRE";
	private static final String OCUPADO = "OCUPADO";
	private static final String CHEIO = "CHEIO";
	private static final String CHEGUEI = "CHEGUEI_CONTAINER";
	private static final String FINALIZADA = "COLETA_FINALIZADA";

	private int porta;
	private List<Caminhao> caminhoesList = new ArrayList<>();
	private List<Container> containersList = new ArrayList<>();
	private List<Container> listaEspera = new ArrayList<>();

	public Servidor(int porta) {
		this.porta = porta;
	}

	public static void main(String[] args) {
		new Servidor(22).executa();
	}

	private void executa() {

		try {
			ServerSocket servidor = new ServerSocket(this.porta);
			System.out.println("Server up na porta " + this.porta);

			boolean executa = true;

			while (executa) {
				Socket cliente = servidor.accept();

				System.out.println("Novo cliente conectado: " + cliente.getRemoteSocketAddress());

				Scanner scanner = new Scanner(cliente.getInputStream());
				String linha = scanner.nextLine();
				String[] strings = linha.split("\\s+");
				int id = Integer.parseInt(strings[1]);
				String comando = strings[0];

				if (comando.toUpperCase().equalsIgnoreCase(LIVRE)) {
					Caminhao caminhao = new Caminhao(id, cliente);
					caminhoesList.add(caminhao);

				} else if (comando.toUpperCase().equalsIgnoreCase(CHEIO)) {
					Container container = new Container(id, cliente);
					containersList.add(container);
					solicitaColeta(container);

				} else if (comando.toUpperCase().equalsIgnoreCase(CHEGUEI)) {
					for (Caminhao caminhao : caminhoesList) {
						if (caminhao.getId() == id) {
							Container container = caminhao.getContainer();
							new ClienteHandler(container.getOutputStream(), this, linha);
						}
					}

				} else if (comando.toUpperCase().equalsIgnoreCase(FINALIZADA)) {
					for (Caminhao caminhao : caminhoesList) {
						if (caminhao.getId() == id) {
							caminhao.setCondicao(LIVRE);
							caminhao.setContainer(null);
							break;
						}
					}

				}
			}

		} catch (IOException e) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, "Erro ao iniciar Thread", e);
		}
	}

	private void solicitaColeta(Container container) {
		Caminhao caminhao = null;
		for (Caminhao c : caminhoesList) {
			if (c.getCondicao().equalsIgnoreCase(LIVRE)) {
				c.setCondicao(OCUPADO);
				c.setContainer(container);
				caminhao = c;
				break;
			}
		}

		String mensagem = ("COLETAR " + container.getId()).toUpperCase();

		if (caminhao != null) {
			new ClienteHandler(caminhao.getOutStream(), this, mensagem);
		} else {
			// Sem disponibilidade de caminhao, add na listaDeEspera
			if (!listaEspera.contains(container)) {
				listaEspera.add(container);
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(SEGUNDO);
							solicitaColeta(container);
						} catch (InterruptedException e) {
							Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, "Erro ao iniciar Thread", e);

						}

					}
				}).start();

			} else {
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(SEGUNDO);
							solicitaColeta(container);
						} catch (InterruptedException e) {
							Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, "Erro ao iniciar Thread", e);

						}

					}
				}).start();
			}
		}
	}
}