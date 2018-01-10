import javax.swing.JOptionPane;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.core.behaviours.*;
import jade.domain.FIPAAgentManagement.*;

public class Comprador extends Agent {
	private String titulo;
	private AID[] vendedores;
	protected void setup() {
		System.out.println("Comprador " + getAID().getLocalName() + " listo para comprar.");

		if(titulo == null) {
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
	
	
	
}
