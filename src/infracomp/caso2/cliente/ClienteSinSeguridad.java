package infracomp.caso2.cliente;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PublicKey;

import javax.crypto.SecretKey;

public class ClienteSinSeguridad {
	
	public static void main(String[] args)
	{
		try
		{
			//Se crea un nuevo socket y se conecta al puerto 44
			Socket socket = new Socket("localhost", 80);
			System.out.println(socket.isConnected());
			InputStream input = socket.getInputStream();
			BufferedReader buff =  new BufferedReader(new InputStreamReader(input));
			PrintWriter print = new PrintWriter(socket.getOutputStream());
			
			//Se envia mensaje HOLA para comenzar el protocolo
			print.println("HOLA");
			print.flush();
			System.out.println("HOLA");
			
			String mensaje = buff.readLine();
			System.out.println(mensaje);
			//Comprueba si el servidor envia la linea de INICIO
			if( mensaje.equals("INICIO")){
				
				//Se envian los algoritmos que se van a utilizar: Simetrico=AES, Asimetrico=RSA, Hash=HMACSHA256
				print.println("ALGORITMOS:AES:RSA:HMACSHA256");
				System.out.println("Si llego al algoritmos");
				print.flush();
				
				
				String mensaje1 = buff.readLine();
				//Comprueba si el estado que envio el servidor es OK
				if(mensaje1.equals("ESTADO:OK")){
					System.out.println("Si llego al estado");
					
					//Se envia el certificado del cliente con la llave publica del cliente
					print.println("CERCLNT");
					print.flush();
					System.out.println("Si llego al cerclnt");
					
					//La clase certificado se encarga de generar los certificados, cifrar y descifrar los mensajes
					Certificado cer = new Certificado();
					//Se envia por el output stream en forma de bytes
					byte[] bytesCerC = (cer.darCertificado()).getEncoded();
					socket.getOutputStream().write(bytesCerC);
					socket.getOutputStream().flush();
					
					System.out.println("si llego a certificado cliente");
					
					String mensaje2 = buff.readLine();
					
					//Comprueba si el sevidor esta enviando el certificado
					if(mensaje2.equals("CERTSRV")){
						System.out.println("si llego a cerSer");
						
						//int size = socket.getReceiveBufferSize();
						//El tamano del buffer que recibe los bytes
						int size = 520;
						byte[] recibidos = new byte[size];
						//Recibe los bytes del certificado del servidor
						input.read(recibidos, 0, size);
						//Se crea el certificado del servidor a partir del arreglo de bytes recibidos y se obtiene la llave publica del servidor
						PublicKey llavePublicaServidor = cer.crearCertificado(recibidos);
						System.out.println("si llego a certificado servidor");
						
						String mensaje3 = buff.readLine();
						System.out.println(mensaje3);
						String[] mensajes3 = mensaje3.split(":");
						//Se comprueba si el servidor va a enviar el mensaje con la llave simetrica
						if(mensajes3[0].equals("INIT")){
							System.out.println("si llego a INIT");
							
					
							print.println("ACT1" );
							print.flush();
							System.out.println("Si llego a ACT1");
							
							//codigo criptografico de hash de las coordenadas cifrado con la llave publica del servidor
							
							print.println("ACT2");
							print.flush();
							System.out.println("Si llego a ACT2");
							
							String mensajef = buff.readLine();
							System.out.println(mensajef);
							//Se comprueba si las coordenadas se enviaron correctamente y el servidor las recibio
							if( mensajef.equals("RTA:OK")){
								System.out.println("La coordenadas se enviaron correctamente");
							}else{
								System.out.println("Hubo un error en el envio de coordenadas: " + mensajef);
							}
						}else{
							System.out.println("No comienza con INIT: sino: " + mensaje3);
						}
					}else{
						System.out.println("No se recibio certser sino: " + mensaje2);
					}
					
				}else{
					System.out.println("No se recibio estado ok sino: " + mensaje1);
				}
				
			}else{
				System.out.println("No fue inicio, sino: " + mensaje);
			}
			//Se cierra el input stream
			input.close();
			//Se cierra el buffered reader
			buff.close();
			//Se cierra el print writer
			print.close();
			//Se cierra el socket
			socket.close();
			System.out.println("Se cerro la conexion");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
