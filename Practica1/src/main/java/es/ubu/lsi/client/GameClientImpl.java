package es.ubu.lsi.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import es.ubu.lsi.common.ElementType;
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
	private static int nClients = 0;
	private int id = 0;
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
		GameClientImpl.nClients++;
		this.id = GameClientImpl.nClients;
	}
	
	private int getId() {
		return this.id;
	}

	/**
	 * Método start. Inicializa la conexión con el servidor y arranca el hilo del
	 * listener.
	 * 
	 * @return true si no ha habido problemas, false en caso contrario
	 */
	public boolean start() {
		boolean flag;
		try {
			// Se crean el socket y los objetos que permiten la comunicación hacia/desde el
			// servidor
			socket = new Socket(server, port);
			salida = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("Enviado nick.");
			salida.writeUTF("El usuario"+ user + "se ha conectado.");

			// Se crea el listener y se arranca el hilo
			GameClientListener listener = new GameClientListener();
			Thread hilo = new Thread(listener);
			flag = true;
			hilo.start();
		} catch (IOException e) {
			flag = false;
			System.err.println("Error al conectarse al servidor en el puerto:  " + port);
            System.exit(1);
        } 
		return flag;
	}

	/**
	 * Método sendElement. Manda la jugada al servidor.
	 * 
	 * @param element
	 *            jugada a mandar
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
	 * Método disconnect. Se cierra la conexión mediante el socket.
	 */
	public void disconnect() {
		try {
			socket.close();
		} catch (IOException e) {
			System.err.println("Error al desconectarse");
            System.exit(1);
		}
	}

	/**
	 * Método main. Programa principal del cliente.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GameClientImpl cliente = null;
		GameElement elemento = null;

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

		Scanner scanner = new Scanner(System.in);
		String jugada = scanner.nextLine();

		if (jugada.equalsIgnoreCase(ElementType.PAPEL.toString())) {
			elemento = new GameElement(cliente.getId(), ElementType.PAPEL);
			cliente.sendElement(elemento);
		} else if (jugada.equalsIgnoreCase(ElementType.PIEDRA.toString())) {
			elemento = new GameElement(cliente.getId(), ElementType.PIEDRA);
			cliente.sendElement(elemento);
		} else if (jugada.equalsIgnoreCase(ElementType.TIJERA.toString())) {
			elemento = new GameElement(cliente.getId(), ElementType.TIJERA);
			cliente.sendElement(elemento);
		} else if (jugada.equalsIgnoreCase(ElementType.LOGOUT.toString())) {
			elemento = new GameElement(cliente.getId(), ElementType.LOGOUT);
			cliente.sendElement(elemento);
		} else if (jugada.equalsIgnoreCase(ElementType.SHUTDOWN.toString())) {
			elemento = new GameElement(cliente.getId(), ElementType.SHUTDOWN);
			cliente.sendElement(elemento);
		} else {
			System.out.println("La jugada no es válida.");
		}

		scanner.close();
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
		 * Método run. El listener va a escuchar constantemente al servidor.
		 */
		@Override
		public void run() {
			String aux;
			while (true) {
				try {
					aux =entrada.readObject().toString();
					if(aux != null) {
						System.out.println("El servidor dice: " + entrada.readObject().toString());
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					disconnect();
					System.exit(1);
				}
			}
		}

	}
}
