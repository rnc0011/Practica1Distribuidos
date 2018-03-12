package es.ubu.lsi.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.ubu.lsi.common.ElementType;
import es.ubu.lsi.common.GameElement;

/**
 * Clase GameServerImpl.
 * 
 * @author Raúl Negro Carpintero
 * @author Mario Núñez Izquierdo
 * @version 1.0
 *
 */
public class GameServerImpl implements GameServer {

	final static int port = 1500;
	private List<Integer> clientes = new ArrayList<Integer>();
	private List<Thread> hilos = new ArrayList<Thread>();
	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	private ObjectOutputStream salida;
	private ObjectInputStream entrada;
	private List<Socket> socketClientes = new ArrayList<Socket>();

	/**
	 * Constructor de la clase GameServerImpl.
	 * 
	 * @param port
	 */
	public GameServerImpl(int port) {

	}

	/**
	 * Método startup.
	 */
	public void startup() {
		while (true) {
			try {
				serverSocket = new ServerSocket(port);
				try {
					clientSocket = new Socket();
					clientSocket = serverSocket.accept();
					System.out.println(clientSocket.getInputStream().toString());
					socketClientes.add(clientSocket);
				} catch (Exception e) {
					e.printStackTrace();
				}
				salida = new ObjectOutputStream(clientSocket.getOutputStream());
				entrada = new ObjectInputStream(clientSocket.getInputStream());
				ChatServerThreadForClient chat = new ChatServerThreadForClient();
				Thread hilo = new Thread(chat);
				hilos.add(hilo);
				hilo.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Método shutdow.
	 */
	public void shutdown() {
		Iterator<Socket> it = socketClientes.iterator();
		while (it.hasNext()) {
			try {
				it.next().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Método broadcastRoom.
	 * 
	 * @param element
	 */
	public void broadcastRoom(GameElement element) {

	}

	/**
	 * Método remove.
	 * 
	 * @param id
	 */
	public void remove(int id) {
		clientes.remove(id);
	}

	/**
	 * Método main.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GameServerImpl servidor = new GameServerImpl(port);
		servidor.startup();
	}

	/**
	 * Clase ChatServerThreadForClient.
	 * 
	 * @author Raúl Negro Carpintero
	 * @author Mario Núñez Izquierdo
	 * @version 1.0
	 *
	 */
	public class ChatServerThreadForClient extends Thread {

		/**
		 * Método getIdRoom.
		 * 
		 * @return
		 */
		public int getIdRoom() {

		}

		/**
		 * Método run.
		 */
		public void run() {
			while (true) {
				try {
					broadcastRoom((GameElement) entrada.readObject());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
