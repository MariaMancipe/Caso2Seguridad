package infracomp.caso2.cliente;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class UnidadDistribucion {
	
	public final static int PUERTO_SEGURIDAD = 443;
	
	public final static int PUERTO_SIN_SEGURIDAD = 80;
	
	public final static String SIMETRICO="AES";
	
	public final static String ASIMETRICO="RSA";
	
	public final static String HMAC="HMACSHA256";
	
	public final static String PADDING = "AES/ECB/PKCS5Padding";
	
	// -----------------------------------------------------------------
    // Atributos
    // -----------------------------------------------------------------
	
	private Socket socket;
	
	private BufferedReader buff;
	
	private InputStream input;
	
	private PrintWriter print;
	
	private boolean[] pasos;
	
	private Certificado certificado;
	
	// -----------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------
	
	public UnidadDistribucion(){
		pasos = new boolean[5];
		certificado = new Certificado();
		for( int i = 0; i< 5; i++){
			pasos[i] = false;
		}
		
		try
		{
			socket = new Socket("infracomp.virtual.uniandes.edu.co", PUERTO_SEGURIDAD);
			System.out.println(socket.isConnected());
			print = new PrintWriter(socket.getOutputStream());
			input = socket.getInputStream();
			buff = new BufferedReader(new InputStreamReader(input));
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
			System.out.println(mensaje1);
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
	}
	
	public void verificarEstadoAlgoritmos(String mensaje, String[] mensajes) throws IOException{
		
		if(mensajes[0].equals("ESTADO")){
			
			System.out.println("ESTADO");
			
			if(mensajes[1].equals("OK")){
				
				System.out.println("OK");
				
				print.println("CERCLNT");
				System.out.println("CERCLNT");
				
				//GENERAR CERTIFICADO
				try{
					byte[] bytes = certificado.darCertificado().getEncoded();
					socket.getOutputStream().write(bytes);
					socket.getOutputStream().flush();
				}catch(Exception e){
					e.printStackTrace();
					System.out.println("No se pudo convertir en un arreglo de bits: "+e.getMessage());
				}
				
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
		
	}
	
	public void autenticarServidor(String mensaje) throws IOException{
		if(mensaje.equals("CERTSRV")){
			
			int size = socket.getReceiveBufferSize();
			byte[] recibidos = new byte[size];
			
			input.read(recibidos, 0, size);
			certificado.crearCertificado(recibidos);
			try{
				
				
				String mensaje2 = buff.readLine();
				String[] mensajes2 = mensaje2.split(mensaje2);
				enviarCoordenadas(mensaje2, mensajes2);
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("Hubo un error validando el certificado del servidor: " + e.getMessage());
			}	
		}
		else{
			System.out.println("No se recibio CERTSRV, sino:" + mensaje);
			cerrarConexion();
			System.out.println("Se cerro la conexion");
		}	
	}
	
	public void enviarCoordenadas(String mensaje, String[] mensajes ) throws IOException{
		if( mensajes[0].equals("INIT")){
			byte[] bytesM =  mensajes[1].getBytes();
			certificado.descifrarMensaje(bytesM);
			//cifrarSimetrico
			print.println("ACT1:");
			
			//cifrarHash
			print.println("ACT2:");
			
			String mensajeFinal = buff.readLine();
			if( mensajeFinal.equals("RTA:OK")){
				System.out.println("Las coordenadas se han enviado correctamente");
				cerrarConexion();
				System.out.println("Se cerro la conexion");
			}
			else{
				System.out.println("Las coordenadas no se enviaron correctamente");
				cerrarConexion();
				System.out.println("Se cerro la conexion");
			}
		}
		else{
			System.out.println("No se recibio INIT, sino:" + mensaje);
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
		
	}
	

}
