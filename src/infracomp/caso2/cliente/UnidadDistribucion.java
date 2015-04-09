package infracomp.caso2.cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class UnidadDistribucion {
	
	public final static int PUERTO_SEGURIDAD = 443;
	
	public final static int PUERTO_SIN_SEGURIDAD = 80;
	
	public final static String SIMETRICO="AES";
	
	public final static String ASIMETRICO="RSA";
	
	public final static String HMAC="HMACSHA1";
	

	
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
			socket = new Socket("infracomp.virtual.uniandes.edu.co", PUERTO_SIN_SEGURIDAD);
			System.out.println(socket.isConnected());
			print = new PrintWriter(socket.getOutputStream());
			input = new InputStreamReader(socket.getInputStream());
			System.out.println("INICIALIZACION");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// -----------------------------------------------------------------
    // Metodos
    // -----------------------------------------------------------------
	
	public void cerrarConexion() throws IOException{
		buff.close();
		print.close();
		socket.close();
	};
	
	public void verificarInicio( String mensaje, String[] mensajes) throws IOException{
		
		if( mensajes[0].equals("INICIO")){
			System.out.println("INICIO");
			//El cliente deberia enviar los algoritmos 
			print.println("ALGORITMOS:"+SIMETRICO+":"+ASIMETRICO+":"+HMAC);
			System.out.println("ALGORTIMOS");
			pasos[0]=true;

			String mensaje1 = buff.readLine();
			if(mensaje1 == null){
				System.out.println("No se ha recibido ningún mensaje");
			}
			else{
				
				String[] mensajes1=mensaje1.split(":");
				verificarEstadoAlgoritmos(mensaje1, mensajes1);
			}
		}
		else{
			//Reportar error
			System.out.println("No se recibio INICIO, sino:" + mensaje);
			cerrarConexion();
			System.out.println("Se cerro la conexion");
			
		}
	};
	
	public void verificarEstadoAlgoritmos(String mensaje, String[] mensajes) throws IOException{
		if(mensajes[0].equals("ESTADO")){
			System.out.println("ESTADO");
			if(mensajes[1].equals("OK")){
				System.out.println("OK");
				print.println("CERCLNT");
				System.out.println("CERCLNT");
				//GENERAR CERTIFICADO
				byte[] bytes = new byte[8];
				socket.getOutputStream().write(bytes);
				socket.getOutputStream().flush();
				pasos[1]=true;
				String mensaje1 = buff.readLine();
				if( mensaje1 == null ){
					System.out.println("No se ha recibido ningún mensaje");
				}else{
					autenticarServidor(mensaje1);
				}
			}
			else if(mensajes[1].equals("ERROR")){
				System.out.println("Hubo un error en la linea de algoritmos: "+ mensaje);
				cerrarConexion();
				System.out.println("Se cerro la conexion");
			}
			else{
				System.out.println("No se recibio ni ERROR ni OK en la verificacion del estado de los algoritmos, sino: " + mensaje);
				cerrarConexion();
				System.out.println("Se cerro la conexion");
			}
		}
		else{
			//Reportar error
			System.out.println("No se recibio ESTADO, sino:" + mensaje);
			cerrarConexion();
			System.out.println("Se cerro la conexion");
		}
		
	};
	
	public void autenticarServidor(String mensaje) throws IOException{
		if(mensaje.equals("CERTSRV")){
		}
		else{
			System.out.println("No se recibio CERTSRV, sino:" + mensaje);
			cerrarConexion();
			System.out.println("Se cerro la conexion");
		}
		
	}
	
	// -----------------------------------------------------------------
    // Run
    // -----------------------------------------------------------------
	
	public void run(){
		
		print.println("HOLA");
		print.flush();
		System.out.println("HOLA");
		
		try{
			buff = new BufferedReader(input);
			boolean caido = false;
				String mensaje = buff.readLine();
				if( mensaje == null ){
					//reportar error
					System.out.println("No se ha recibido ningún mensaje");
					cerrarConexion();
					System.out.println("Se cerro la conexion");
					//cerrar conexion con el servidor
				}
				else{
					String[] mensajes = mensaje.split(":");
					verificarInicio(mensaje, mensajes);
					
				}
		
		}
		catch(Exception e){
			try{
				cerrarConexion();
				System.out.println("Se cerro la conexion");
			}catch(Exception e1){
				System.out.println("NO SE PUDO CERRAR LA CONEXION");
				e1.printStackTrace();
			}
		}
		
	}
	

	public static void main(String[] args){
		UnidadDistribucion ud = new UnidadDistribucion();
		ud.run();
		
	};
	

}
