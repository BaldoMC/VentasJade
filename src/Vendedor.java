import jade.core.Agent;
import java.util.*;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;


public class Vendedor extends Agent{
	//Catalogo de juegos disponibles en cada vendedor
	private Hashtable catalogo;
	
	protected void setup() {
		catalogo = new Hashtable();
		
		//Se crea el servicio del vendedor 
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("venta-juegos");
		sd.setName("JADE-Venta-Juegos");
		dfd.addServices(sd);
		
		try {
			DFService.register(this, dfd);
		}catch(FIPAException fe) {
			fe.printStackTrace();
		}
		
		System.out.println("Vendedor " + getAID().getLocalName() + " listo para vender!");
	}
	
	/**
	 * Metodo que termina con un agente
	 */
	protected void takeDown() {
		
		try {
			DFService.deregister(this);
		}catch(FIPAException fe) {
			fe.printStackTrace();
		}
		System.out.println(getAID().getLocalName() +" se despide.");
	}
	/**
	 * Metodo que actualiza el catalogo de juegos de un Vendedor
	 * @param nombre Nombre del juego
	 * @param precio Precio del juego
	 */
	public void actualizarCatalogo(final String nombre, final int precio) {
		addBehaviour(new OneShotBehaviour() {
				public void action() {
					catalogo.put(nombre,new Integer(precio));
					System.out.println(nombre +" insertado en el catalogo de juegos de: " + myAgent.getLocalName()+ " a un precio de $" +precio);
				}
		});
	}
	
}
