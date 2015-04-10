package infracomp.caso2.cliente;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Main {
	
	public static void main(String[] args)
	{
		try
		{
			Socket socket = new Socket("infracomp.virtual.uniandes.edu.co", 80);
			InputStream input = socket.getInputStream();
			BufferedReader buff =  new BufferedReader(new InputStreamReader(input));
			PrintWriter print = new PrintWriter(socket.getOutputStream());
			
			print.println("HOLA");
			print.flush();
			
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
