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
	private final static int port = 1500;
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
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método main.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GameClientImpl cliente = null;

		if (args.length == 1) {
			String usuario = args[0];
			cliente = new GameClientImpl("localhost", port, usuario);
		} else if (args.length == 2) {
			String hostName = args[0];
			String usuario = args[1];
			cliente = new GameClientImpl(hostName, port, usuario);
		} else {
			throw new IllegalArgumentException("Se deben introducir 1 o 2 argumentos.");
		}
		cliente.start();
		

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
		@Override
		public void run() {
			boolean running = true;
			while(running) {
				try {
					System.out.println("El servidor dice: " + entrada.readObject().toString());
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
					disconnect();
				} catch (IOException e2) {
					e2.printStackTrace();
					disconnect();
				}
			}

		}

	}
}
