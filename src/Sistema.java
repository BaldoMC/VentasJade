import java.util.Scanner;
import java.util.*;

public class Sistema {
    private Comprador agenteC;
	private Vendedor agenteV;

	Sistema (Vendedor vendedor,Comprador comprador, String tipo){
		
		if(tipo == "vendedor") {
			agenteV = vendedor;
			String titulo = "";
			int precio = 0;
			System.out.println("Para no agregar mas titulos, escriba fin");
			Scanner entrada = new Scanner (System.in);
			System.out.println("Nombre del juego:");
			titulo = entrada.nextLine();
			while(!titulo.equals("fin")) {
				System.out.println("Precio:");
				precio = Integer.parseInt(entrada.nextLine());
				agenteV.actualizarCatalogo(titulo, precio);
				System.out.println("Nombre del juego:");
				titulo = entrada.nextLine();
			}	
		}else {
			
			agenteC = comprador;

			Scanner entrada = new Scanner (System.in);
			System.out.println("Nombre del juego a comprar:");
			String nombre = entrada.nextLine();
			agenteC.setTitulo(nombre);

		}
		
	}
	
}
