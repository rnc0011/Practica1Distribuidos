package es.ubu.lsi.client;

import es.ubu.lsi.common.GameElement;

/**
 * Clase GameClientImpl.
 * 
 * @author Raúl Negro Carpintero
 * @author Mario Núñez Izquierdo
 * @version 1.0
 *
 */
public class GameClientImpl {

	// Declaración de variables
	private String server;
	private static int port = 1500;
	private String user;

	/**
	 * Constructor de la clase GameClientImpl.
	 * 
	 * @param server
	 * @param port
	 * @param username
	 */
	public GameClientImpl(String server, int port, String username) {
		this.server = server;
		this.user = username;
	}

	/**
	 * Método start.
	 * 
	 * @return
	 */
	public boolean start() {

	}

	/**
	 * Método sendElement.
	 * 
	 * @param element
	 */
	public void sendElement(GameElement element) {

	}

	/**
	 * Método disconnect.
	 */
	public void disconnect() {

	}

	/**
	 * Método main.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length == 1) {
			String usuario = args[0];
			GameClientImpl cliente = new GameClientImpl("localhost", port, usuario);
			cliente.start();
		} else if (args.length == 2) {
			String hostName = args[0];
			String usuario = args[1];
			GameClientImpl cliente = new GameClientImpl(hostName, port, usuario);
			cliente.start();
		}

	}

	/**
	 * Clase GameClientListener.
	 * 
	 * @author Raúl Negro Carpintero
	 * @author Mario Núñez Izquierdo
	 * @version 1.0
	 *
	 */
	public class GameClientListener implements Runnable {

		/**
		 * Método run.
		 */
		public void run() {

		}

	}
}
