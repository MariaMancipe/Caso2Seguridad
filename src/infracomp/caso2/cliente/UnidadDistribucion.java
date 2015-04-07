package infracomp.caso2.cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class UnidadDistribucion extends Thread {
	
	public final static int PUERTO_SEGURIDAD = 443;
	
	public final static int PUERTO_SIN_SEGURIDAD = 80;
	
	public final static String SIMETRICO="";
	
	public final static String ASIMETRICO="RSA";
	
	public final static String HASH="";
	

	
	// -----------------------------------------------------------------
    // Atributos
    // -----------------------------------------------------------------
	
	private Socket socket;
	
	private BufferedReader buff;
	
	private InputStreamReader input;
	
	private PrintWriter print;
	
	private boolean[] pasos;
	
	private Certificado certificado;
	
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
	
	
	public void verificarInicio( String mensaje, String[] mensajes) throws IOException{
		
		if( mensajes[0].equals("INICIO")){
			//El cliente deberia enviar los algoritmos 
			print.println("ALGORITMOS:"+SIMETRICO+":"+ASIMETRICO+":"+HASH);
			pasos[0]=true;
			
			String mensaje1 = buff.readLine();
			if(mensaje1 == null){
				System.out.println("No se ha recibido ning�n mensaje");
			}
			else{
				
				String[] mensajes1=mensaje1.split(":");
				verificarEstadoAlgoritmos(mensaje1, mensajes1);
			}
		}
		else{
			//Reportar error
			System.out.println("No se recibio INICIO, sino:" + mensaje);
			System.out.println("Se cerro la conexion");
			
		}
	};
	
	public void verificarEstadoAlgoritmos(String mensaje, String[] mensajes) throws IOException{
		if(mensajes[0].equals("ESTADO")){
			if(mensajes[1].equals("OK")){
				
				print.println("CERCLNT");
				//GENERAR CERTIFICADO
				byte[] bytes = new byte[8];
				socket.getOutputStream().write(bytes);
				socket.getOutputStream().flush();
				pasos[1]=true;
				String mensaje1 = buff.readLine();
				if( mensaje1 == null ){
					System.out.println("No se ha recibido ning�n mensaje");
				}else{
					autenticarServidor(mensaje1);
				}
			}
			else if(mensajes[1].equals("ERROR")){
				System.out.println("Hubo un error en la linea de algoritmos: "+ mensaje);
			}
			else{
				System.out.println("No se recibio ni ERROR ni OK en la verificacion del estado de los algoritmos, sino: " + mensaje);
			}
		}
		else{
			//Reportar error
			System.out.println("No se recibio ESTADO, sino:" + mensaje);
			System.out.println("Se cerro la conexion");
		}
		
	};
	
	public void autenticarServidor(String mensaje) throws IOException{
		if(mensaje.equals("CERTSRV")){
		}
		else{
			System.out.println("No se recibio CERTSRV, sino:" + mensaje);
		}
		
	}
	
	// -----------------------------------------------------------------
    // Run
    // -----------------------------------------------------------------
	
	public void run(){
		
		print.println("HOLA");
		print.flush();
		
		try{
			buff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			boolean caido = false;
				String mensaje = buff.readLine();
				if( mensaje == null ){
					//reportar error
					System.out.println("No se ha recibido ning�n mensaje");
					caido = true;
					//cerrar conexion con el servidor
				}
				else{
					String[] mensajes = mensaje.split(":");
					verificarInicio(mensaje, mensajes);
					
				}
		
		}
		catch(Exception e){
			
		}
		
	}
	


}
