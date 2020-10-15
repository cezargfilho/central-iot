package br.ucsal.central.server;

import java.io.InputStream;
import java.util.Scanner;

public class ReceptorCliente implements Runnable {

	private InputStream serverInput;

	public ReceptorCliente(InputStream serverInput) {
		this.serverInput = serverInput;
	}

	@Override
	public void run() {
		try (Scanner input = new Scanner(this.serverInput)) {
			while (input.hasNextLine()) {
				System.out.println(input.nextLine());
			}
		}
	}
}
