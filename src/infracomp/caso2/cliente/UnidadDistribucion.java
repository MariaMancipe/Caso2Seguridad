package infracomp.caso2.cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class UnidadDistribucion extends Thread {
	
	public final static int PUERTO_SEGURIDAD = 443;
	
	public final static int PUERTO_SIN_SEGURIDAD = 80;

	
	// -----------------------------------------------------------------
    // Atributos
    // -----------------------------------------------------------------
	
	private Socket socket;
	
	private BufferedReader buff;
	
	private InputStreamReader input;
	
	private PrintWriter print;
	
	private boolean[] pasos;
	
	// -----------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------
	
	public UnidadDistribucion(){
		pasos = new boolean[5];
		for( int i = 0; i< 5; i++){
			pasos[i] = false;
		}
		
		try
		{
			
			print = new PrintWriter(socket.getOutputStream());
			input = new InputStreamReader(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// -----------------------------------------------------------------
    // Metodos
    // -----------------------------------------------------------------
	
	public void enviarAlgoritmos(){
		
	};
	
	public void enviarCertificado(){
		
	}
	
	public void verificarMensaje(String[] mensajes){
		
		String inicio = mensajes[0];
		if( inicio.equals("INICIO")){
			//El cliente deberia enviar los algoritmos 
			pasos[0]=true;
		}
		else if( inicio.equals("ESTADO")){
			if( mensajes[1].equals("OK")){
				//enviar certificado
				pasos[1]=true;
			}
			else if(mensajes[1].equals("ERROR")){
				//reportar error
			}
			else{
				//reportar palabra que no esta en el protocolo
			}
		}
		else if(inicio.equals("CERTSRV")){
			//recibir flujo de bytes del certificado
			pasos[2]=true;
		}
		else if(inicio.equals("INIT")){
			//enviar reporte y manejo 
			pasos[3]=true;
		}
		else if(inicio.equals("RTA")){
			if( mensajes[1].equals("OK")){
				//terminar comunicacion
				pasos[4]=true;
			}
			else if(mensajes[1].equals("ERROR")){
				//reportar error
			}
			else{
				//reportar palabra que no esta en el protocolo
			}
		}
		else{
			//reportar palabra que no esta en el protocolo
		}
	};
	
	// -----------------------------------------------------------------
    // Run
    // -----------------------------------------------------------------
	
	public void run(){
		
		print.print("HOLA");
		
		try{
			buff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			boolean caido = false;
			while(!caido){
				String mensaje = buff.readLine();
				if( mensaje == null ){
					//reportar error
				}
				else{
					String[] mensajes = mensaje.split(":");
					verificarMensaje(mensajes);
				}
						
			}
		}
		catch(Exception e){
			
		}
		
	}
	


}
