package infracomp.caso2.cliente;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PublicKey;

import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

public class Main {
	
	public static void main(String[] args)
	{
		try
		{
			Socket socket = new Socket("infracomp.virtual.uniandes.edu.co", 443);
			System.out.println(socket.isConnected());
			InputStream input = socket.getInputStream();
			BufferedReader buff =  new BufferedReader(new InputStreamReader(input));
			PrintWriter print = new PrintWriter(socket.getOutputStream());
			
			print.println("HOLA");
			print.flush();
			System.out.println("HOLA");
			
			System.out.println(socket.isConnected());
			String mensaje = buff.readLine();
			System.out.println(mensaje);
			if( mensaje.equals("INICIO")){
				
				print.println("ALGORITMOS:AES:RSA:HMACSHA256");
				System.out.println("Si llego al algoritmos");
				print.flush();
				
				
				String mensaje1 = buff.readLine();
				if(mensaje1.equals("ESTADO:OK")){
					System.out.println("Si llego al estado");
					
					print.println("CERCLNT");
					print.flush();
					System.out.println("Si llego al cerclnt");
					
					Certificado cer = new Certificado();
					byte[] bytesCerC = (cer.darCertificado()).getEncoded();
					socket.getOutputStream().write(bytesCerC);
					socket.getOutputStream().flush();
					
					System.out.println("si llego a certificado cliente");
					
					String mensaje2 = buff.readLine();
					
					if(mensaje2.equals("CERTSRV")){
						System.out.println("si llego a cerSer");
						
						//int size = socket.getReceiveBufferSize();
						int size = 520;
						byte[] recibidos = new byte[size];
						input.read(recibidos, 0, size);
						PublicKey llavePublicaServidor = cer.crearCertificado(recibidos);
						System.out.println("si llego a certificado servidor");
						
						String mensaje3 = buff.readLine();
						System.out.println(mensaje3);
						String[] mensajes3 = mensaje3.split(":");
						if(mensajes3[0].equals("INIT")){
							System.out.println("si llego a INIT");
							
							//Cifrar coordenadas con llave simetrica del servidor
							String llaveCifrada = mensajes3[1];
							//De hexadecimal a binario
							byte[] bytesLlaveC = DatatypeConverter.parseHexBinary(llaveCifrada);
							//descifra el mensaje y guarda la llave simetrica en un String(el metodo se puede consultar en la clase Certificado)
							SecretKey llaveSimetrica = cer.descifrarMensaje(bytesLlaveC);
							System.out.println("Si llego a descifrar la llave");
							
							//coordenadas
							String coordenadas = "41 24.2028,2 10.4418";
							//Coordenadas cifradas con la llave simetrica del servidor(el metodo se puede consultar en la clase Certificado)
							String coordenadasS = cer.cifrarCoordenadasSimetrica(llaveSimetrica, coordenadas);
							//Se envian las coordenadas al servidor
							print.println("ACT1:" + coordenadasS);
							print.flush();
							System.out.println(coordenadasS);
							System.out.println("Si llego a coordenadas simetricas");
							
							//codigo criptografico de hash de las coordenadas cifrado con la llave publica del servidor
							cer.cifrarHashAsimetrico(llavePublicaServidor, coordenadas);
							
							String mensajef = buff.readLine();
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
			
			input.close();
			buff.close();
			print.close();
			socket.close();
			System.out.println("Se cerro la conexion");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
