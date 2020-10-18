package br.ucsal.central.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.ucsal.central.model.Caminhao;
import br.ucsal.central.model.Container;

public class Servidor {

	private static final long SEGUNDO = 10000;
	protected final String LIVRE = "LIVRE";
	protected final String OCUPADO = "OCUPADO";
	protected final String CHEIO = "CHEIO";
	protected final String CHEGUEI = "CHEGUEI_CONTAINER";
	protected final String FINALIZADA = "COLETA_FINALIZADA";

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

			while (true) {
				Socket cliente = servidor.accept();
				System.out.println("Novo cliente conectado a central: " + cliente.getInetAddress().getHostAddress());
				Thread t1 = new Thread(new ClienteHandler(cliente, this));
				t1.start();
			}

		} catch (IOException e) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, "Erro ao iniciar Thread", e);
		}
	}

	public void solicitaColeta(Container container) {
		Caminhao caminhao = null;
		// buscar caminhao livre
		for (Caminhao c : getCaminhoesList()) {
			if (c.getCondicao().equalsIgnoreCase(LIVRE)) {
				c.setCondicao(OCUPADO);
				c.setContainer(container);
				caminhao = c;
				break;
			}
		}
		// enviar mensagem para o caminhao sobre coleta
		String mensagem = ("COLETAR " + container.getId()).toUpperCase();

		if (caminhao != null) {
			// new ClienteHandler(caminhao.getOutStream(), mensagem);
			new PrintStream(caminhao.getOutStream()).println(mensagem);
			System.out.println("caminhao " + caminhao.getId() + " = " + mensagem);
		} else {
			// Sem disponibilidade de caminhao, add na listaDeEspera
			if (!getListaEspera().contains(container)) {
				// se o container nao estiver na lista de espera, add ele
				getListaEspera().add(container);
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							System.out.println("container " + container.getId() + " espera 10s");
							Thread.sleep(SEGUNDO);
							solicitaColeta(container);
						} catch (InterruptedException e) {
							Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, "Erro ao iniciar Thread", e);

						}
					}
				}).start();

			} else {
				// se o container ja estiver na lista de espera, realiza a solicitacao
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							System.out.println("container " + container.getId() + " espera 10s");
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

	public List<Container> getContainersList() {
		return containersList;
	}

	public List<Caminhao> getCaminhoesList() {
		return caminhoesList;
	}

	public List<Container> getListaEspera() {
		return listaEspera;
	}

}