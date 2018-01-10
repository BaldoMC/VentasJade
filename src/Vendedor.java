import jade.core.Agent;
import java.util.*;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.ACLMessage;


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
	/**
	 * Inner clase de ofrecer Juego
	 * @author Joaquin Solano
	 * Comportamiendo usado por el vendedor, para ofrecer juegos a las peticiones entrantes
	 * de los comparadores.
	 */
	public class ofrecerJuego extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage mensaje = myAgent.receive(mt);
			if (mensaje != null) {
				// Si se recibe un mensaje, se busca el juego en el catalogo.
				String nombre = mensaje.getContent();
				ACLMessage respuesta = mensaje.createReply();
				Integer precio = (Integer) catalogo.get(nombre);
				
				if (precio != null) {
					respuesta.setPerformative(ACLMessage.PROPOSE);
					respuesta.setContent(String.valueOf(precio.intValue()));
				}else{
					respuesta.setPerformative(ACLMessage.REFUSE);
					respuesta.setContent("no disponible");
				}
				myAgent.send(respuesta);
			}else block();
		}
	}
	/**
	 * Inner clase que realiza venta
	 * @author Joaquin Solano
	 * Comportamiendo usado por el vendedor, para realizar la venta de juego a los compradores.
	 */
	public class RealizarVenta extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage mensaje = myAgent.receive(mt);
			if(mensaje != null) {
				// Si se recibe un mensaje, se busca el juego en el catalogo.
				String nombre = mensaje.getContent();
				ACLMessage respuesta = mensaje.createReply();
				Integer precio = (Integer) catalogo.get(nombre);
				
				if (precio != null) {
					respuesta.setPerformative(ACLMessage.INFORM);
					System.out.println("El juego "+ nombre +" fue vendido a "+mensaje.getSender().getLocalName());
				}else{
					respuesta.setPerformative(ACLMessage.FAILURE);
					respuesta.setContent("no disponible");
				}
				myAgent.send(respuesta);
			}else block();
		}
	}
}
