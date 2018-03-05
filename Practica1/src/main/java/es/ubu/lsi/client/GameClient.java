package es.ubu.lsi.client;

import es.ubu.lsi.common.GameElement;

/**
 * Interfaz GameClientl.
 * 
 * @author Raúl Negro Carpintero
 * @author Mario Núñez Izquierdo
 * @version 1.0
 *
 */
public interface GameClient {

	/**
	 * Método start.
	 * 
	 * @return
	 */
	public abstract boolean start();

	/**
	 * Método sendElement.
	 * 
	 * @param element
	 */
	public abstract void sendElement(GameElement element);

	/**
	 * Método disconnect.
	 */
	public abstract void disconnect();

}
