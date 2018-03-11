package es.ubu.lsi.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import es.ubu.lsi.common.GameElement;

/**
 * Clase GameClientImpl.
 * 
 * @author Raúl Negro Carpintero
 * @author Mario Núñez Izquierdo
 * @version 1.0
 *
 */
public class GameClientImpl implements GameClient {

	// Declaración de variables
	private String server;
	final static int port = 1500;
	private String user;
	private Socket socket;
	private ObjectOutputStream salida;
	private ObjectInputStream entrada;

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
		boolean flag;
		
		try {
			socket = new Socket(server, port);
			salida = new ObjectOutputStream(socket.getOutputStream());
			entrada = new ObjectInputStream(socket.getInputStream());
			salida.writeUTF("El usuario " + user + "se ha conectado.");
			GameClientListener listener = new GameClientListener();
			Thread hilo = new Thread(listener);
			flag = true;
			hilo.start();
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		
		return flag;
	}

	/**
	 * Método sendElement.
	 * 
	 * @param element
	 */
	public void sendElement(GameElement element) {
		try {
			salida.writeObject(element);
		} catch (IOException e) {
			e.printStackTrace();
			disconnect();
		}
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
			while(true) {
				try {
					System.out.println("El servidor dice: " + entrada.readObject().toString());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					disconnect();
				} catch (IOException e) {
					e.printStackTrace();
					disconnect();
				}
			}

		}

	}
}
