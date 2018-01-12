import javax.swing.JOptionPane;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.core.behaviours.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Comprador extends Agent {
	private String titulo;
	private AID[] vendedores;
	private Sistema sistema;
	
	
	
	protected void setup() {
		
		System.out.println("Comprador " + getAID().getLocalName() + " listo para comprar.");
		//sistema = new Sistema(null,this,"comprador");
		Object[] args = getArguments();
		titulo = (String) args[0];
		System.out.println("El titulo es: "+ titulo);
		if(titulo != null) {
			addBehaviour(new OneShotBehaviour() {
				@Override
				public void action() {
					JOptionPane.showMessageDialog(null,"Quiero comprar " + titulo);
						
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType("venta-juegos");
					template.addServices(sd);
					try {
						DFAgentDescription[] resultado = DFService.search(myAgent, template);
						System.out.println("Se encontraron " + resultado.length + "vendedores.");
						vendedores = new AID[resultado.length];
						for(int i = 0; i < resultado.length ; i++) {
							vendedores[i] = resultado[i].getName();
							System.out.println("Nombre de vendedor: " + vendedores[i].getLocalName());
						}
					}catch (FIPAException fe) {
						fe.printStackTrace();
					}
				}
			});
		}else {
			System.out.println("No hay titulo de juego, comprador sin ganas de comprar :( ");
			doDelete();
		}
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
	
	private class solicitarCompra extends Behaviour{
		private AID mejorVendedor;
		private int mejorPrecio;
		private int contResp;
		private MessageTemplate mt;
		private int paso =0;
		public void action() {
			switch(paso) {
			case 0:
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for(int i = 0; i < vendedores.length ; i++) {
					cfp.addReceiver(vendedores[i]);
				}
				cfp.setContent(titulo);
				cfp.setConversationId("venta-juegos");
				cfp.setReplyWith("cfp"+System.currentTimeMillis());
				myAgent.send(cfp);
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("venta-juegos"),MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				paso = 1;
				break;
			case 1:
				ACLMessage respuesta = myAgent.receive(mt);
				if(respuesta != null) {
					if(respuesta.getPerformative() == ACLMessage.PROPOSE) {
						int precio = Integer.parseInt(respuesta.getContent());
						if(mejorVendedor == null || precio < mejorPrecio) {
							mejorPrecio = precio;
							mejorVendedor = respuesta.getSender();
						}
					}
					contResp++;
					if(contResp >= vendedores.length) {
						paso = 2;
					}
				}
				else {
					block();
				}
				break;
				
			case 2:
				ACLMessage orden = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				orden.addReceiver(mejorVendedor);
				orden.setContent(titulo);
				orden.setConversationId("venta-juegos");
				orden.setReplyWith("orden"+System.currentTimeMillis());
				myAgent.send(orden);
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("venta-juegos"),MessageTemplate.MatchInReplyTo(orden.getReplyWith()));
				paso = 3;
				break;
				
			case 3:
				respuesta = myAgent.receive(mt);
				if(respuesta != null) {
					if(respuesta.getPerformative() == ACLMessage.INFORM) {
						System.out.println(titulo + " fue comprado al vendedor "+ respuesta.getSender().getLocalName() + " al precio de $" + mejorPrecio);
						myAgent.doDelete();
					}
					else {
						System.out.println("Compra fallida, juego no encontrado");
					}
					paso = 4;
					
				}else {
					block();
				}
				break;
			}
			
		}
		
		public boolean done() {
			if(paso == 2 && mejorVendedor == null) {
				System.out.println("Ningun vendedor tiene el juego: " + titulo + " a la venta");
			}
			return((paso ==2 && mejorVendedor == null) || paso == 4);
		}
	}
	
	public void setTitulo(String nombre) {
		titulo = nombre;
	}
	
	
}
