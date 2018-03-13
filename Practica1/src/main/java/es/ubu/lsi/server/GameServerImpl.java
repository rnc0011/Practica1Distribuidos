package es.ubu.lsi.server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import es.ubu.lsi.common.ElementType;
import es.ubu.lsi.common.GameElement;
import es.ubu.lsi.common.GameResult;

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
	private List<Socket> socketClientes = new ArrayList<Socket>();
	private int currentRoom = 1;
	private boolean clientInRoom = false;
	private HashMap salas = new HashMap<Integer,GameElement>();
	private GameElement jugada = null;

	/**
	 * Constructor de la clase GameServerImpl.
	 * 
	 * @param port
	 */
	public GameServerImpl(int port) {

	}
	
	private int calcularSala() {
		int res = this.currentRoom;
		
		if(!this.clientInRoom) {
			this.clientInRoom = true;
		}else {
			this.clientInRoom = false;
			this.currentRoom++;
		}
		
		return res;
	}

	/**
	 * Método startup.
	 */
	public void startup() {
		ChatServerThreadForClient chat = null;
		ObjectInputStream entrada;
		int nuevaSala;
		try {
			serverSocket = new ServerSocket(port);
			while (true) {
				
					clientSocket = new Socket();
					System.out.println("Servidor en espera...");
					clientSocket = serverSocket.accept();
					
					entrada = new ObjectInputStream(clientSocket.getInputStream());
					nuevaSala = this.calcularSala();
					System.out.println("El siguiente cliente se ha conectado:");
					System.out.println(entrada.readUTF());
					socketClientes.add(clientSocket);
					chat = new ChatServerThreadForClient(clientSocket, nuevaSala);
					
					Thread hilo = new Thread(chat);
					hilos.add(hilo);
					hilo.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
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
		int resultado = 0;
		try {
			if (jugada.getElement().equals(element.getElement())) {
				
					new ObjectOutputStream(this.socketClientes.get(jugada.getClientId()-1).getOutputStream()).writeObject(GameResult.DRAW);
					new ObjectOutputStream(this.socketClientes.get(element.getClientId()-1).getOutputStream()).writeObject(GameResult.DRAW);
				
			}else {
			
				switch(element.getElement()) {
				case PAPEL:
					resultado = jugada.getElement().equals(ElementType.PIEDRA) ? 1 : -1;
				case PIEDRA:
					resultado = jugada.getElement().equals(ElementType.TIJERA) ? 1 : -1;
				case TIJERA:
					resultado = jugada.getElement().equals(ElementType.PAPEL) ? 1 : -1;
				}
				
				if(resultado==1) {
					new ObjectOutputStream(this.socketClientes.get(jugada.getClientId()-1).getOutputStream()).writeObject(GameResult.WIN);
					new ObjectOutputStream(this.socketClientes.get(element.getClientId()-1).getOutputStream()).writeObject(GameResult.LOSE);
				}else if(resultado==-1) {
					new ObjectOutputStream(this.socketClientes.get(jugada.getClientId()-1).getOutputStream()).writeObject(GameResult.LOSE);
					new ObjectOutputStream(this.socketClientes.get(element.getClientId()-1).getOutputStream()).writeObject(GameResult.WIN);
				}else {
					System.err.println("Error al comparar jugadas.");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		};

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
		private int idRoom;
		private Socket clientSocket;
		private ObjectOutputStream salida;
		private ObjectInputStream entrada;
		
		private ChatServerThreadForClient(Socket clientSocket,int idRoom) {
			this.clientSocket = clientSocket;
			try {
				this.entrada = new ObjectInputStream(this.clientSocket.getInputStream());
				this.salida = new ObjectOutputStream(this.clientSocket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			this.idRoom = idRoom;
		}
		/**
		 * Método getIdRoom.
		 * 
		 * @return
		 */
		public int getIdRoom() {
			return this.idRoom;
		}

		/**
		 * Método run.
		 */
		public void run() {
			while (true) {
				try {
					if (jugada == null) {
						jugada = (GameElement) this.entrada.readObject();
					}else {
						broadcastRoom((GameElement) this.entrada.readObject());
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
