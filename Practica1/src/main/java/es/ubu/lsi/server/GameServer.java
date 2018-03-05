package es.ubu.lsi.server;

import es.ubu.lsi.common.GameElement;

/**
 * Interfaz GameServer.
 * 
 * @author Raúl Negro Carpintero
 * @author Mario Núñez Izquierdo
 * @version 1.0
 *
 */
public interface GameServer {

	/**
	 * Método startup.
	 */
	public abstract void startup();

	/**
	 * Método shutodwn.
	 */
	public abstract void shutdown();

	/**
	 * Método broadcastRoom.
	 * 
	 * @param element
	 */
	public abstract void broadcastRoom(GameElement element);

	/**
	 * Método remove.
	 * 
	 * @param id
	 */
	public abstract void remove(int id);

}
