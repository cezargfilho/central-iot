package br.ucsal.central.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import br.ucsal.central.model.Caminhao;
import br.ucsal.central.model.Container;

public class ClienteHandler implements Runnable {

	private Socket cliente;
	private Servidor servidor;
	private int id;

	public ClienteHandler(Socket cliente, Servidor servidor) {
		this.cliente = cliente;
		this.servidor = servidor;
	}

	@Override
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(cliente.getInputStream()));

			while (true) {
				String linha = br.readLine();

				String[] strings = linha.split("\\s+");
				String comando = strings[0];
				id = Integer.parseInt(strings[1]);

				if (comando.toUpperCase().equalsIgnoreCase(servidor.LIVRE)) {
					System.out.println("caminhao= " + linha);
					Caminhao caminhao = new Caminhao(id, cliente);
					servidor.getCaminhoesList().add(caminhao);

				} else if (comando.toUpperCase().equalsIgnoreCase(servidor.CHEIO)) {
					System.out.println("container= " + linha);
					Container container = new Container(id, cliente);
					servidor.getContainersList().add(container);
					servidor.solicitaColeta(container);

				} else if (comando.toUpperCase().equalsIgnoreCase(servidor.CHEGUEI)) {
					for (Caminhao caminhao : servidor.getCaminhoesList()) {
						if (caminhao.getId() == id) {
							Container container = caminhao.getContainer();
							new PrintStream(container.getOutputStream()).println(linha);
							System.out.println("caminhao " + caminhao.getId() + " = " + linha);
						}
					}

				} else if (comando.toUpperCase().equalsIgnoreCase(servidor.FINALIZADA)) {
					System.out.println("caminhao= " + linha);
					for (Caminhao caminhao : servidor.getCaminhoesList()) {
						if (caminhao.getId() == id) {
							caminhao.setCondicao(servidor.LIVRE);
							caminhao.setContainer(null);
							System.out.println("caminhao livre = " + caminhao.getId());
							break;
						}
					}
				}

			}
		} catch (Exception e) {
			Caminhao caminhao = null;
			for (Caminhao c : servidor.getCaminhoesList()) {
				if (c.getId() == id) {
					caminhao = c;
				}
			}
			servidor.getCaminhoesList().remove(caminhao);
			e.printStackTrace();
		}

	}
}