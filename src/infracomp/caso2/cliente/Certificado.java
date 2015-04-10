package infracomp.caso2.cliente;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

import org.bouncycastle.x509.X509V3CertificateGenerator;

public class Certificado
{
	private X509Certificate certificado;
	
	private Date fechaInicio;
	
	private Date fechaFin;
	
	private KeyPair llaves;
	
	public Certificado() {
		try {
			generarLlaves();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("HUBO UN ERROR AL GENERAR LAS LLAVES: " + e.getMessage());
		}
		try{
			generarCertificado();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("HUBO UN ERROR AL GENERAR EL CERTIFICADO: "+e.getMessage());
		}
		
	}
	
	private void generarLlaves( ) throws NoSuchAlgorithmException{
		
		KeyPairGenerator generador = KeyPairGenerator.getInstance(UnidadDistribucion.ASIMETRICO);
		generador.initialize(1024, new SecureRandom());
		llaves = generador.generateKeyPair();
	}
	
	private void generarCertificado( ) throws CertificateEncodingException, InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, SignatureException{
		
		fechaInicio = new Date(System.currentTimeMillis());
		fechaFin = new Date(System.currentTimeMillis() + (30*1000*60*60*24));
		
		X509V3CertificateGenerator certificateGenerator = new X509V3CertificateGenerator();
		X500Principal firma = new X500Principal("CN=MariaMancipeYSantiagoAbisambra");
		
		certificateGenerator.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		certificateGenerator.setSubjectDN(firma);
		certificateGenerator.setIssuerDN(firma);
		certificateGenerator.setNotBefore(fechaInicio);
		certificateGenerator.setNotBefore(fechaFin);
		certificateGenerator.setPublicKey(llaves.getPublic());
		//Aqui puede haber un error
		
		certificateGenerator.setSignatureAlgorithm("SHA256withRSA");
		certificado = certificateGenerator.generate(llaves.getPrivate());
	};
	
	public X509Certificate crearCertificado( byte[] recibidos){
		
		InputStream inCer = new ByteArrayInputStream(recibidos);
		try{
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate certificado = (X509Certificate) cf.generateCertificate(inCer);
			inCer.close();
			return certificado;
		}
		catch(Exception e){
			System.out.println("Error creando CertificateFactory: " + e.getMessage());
			return null;
		}
		
	}
	
	public SecretKey descifrarMensaje( byte[] recibidos ){
		try {
			Cipher cipher = Cipher.getInstance(UnidadDistribucion.ASIMETRICO);
			cipher.init(Cipher.DECRYPT_MODE, llaves.getPrivate());
			byte [] clearText = cipher.doFinal(recibidos);
			SecretKey llave = new SecretKeySpec(clearText, 0, clearText.length, UnidadDistribucion.SIMETRICO);
			return llave;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Hay un error descifrando la llave simetrica: "+ e.getMessage());
			return null;
		}
	}
	
	public String cifrarCoordenadasSimetrica( SecretKey llave, String coordenadas ){
		byte [] cipheredText;
		try {

		Cipher cipher = Cipher.getInstance(UnidadDistribucion.PADDING);
		
		byte [] clearText = coordenadas.getBytes();
		cipher.init(Cipher.ENCRYPT_MODE, llave);

		cipheredText = cipher.doFinal(clearText);

		String s2 = new String (cipheredText);
		
		return s2;
		}
		catch (Exception e) {
		System.out.println("Excepcion cifrando coordenadas (simetrica): " + e.getMessage());
		return null;
		}
		
	}
	
	public KeyPair darLlaves(){
		return llaves;
	};
	
	public X509Certificate darCertificado(){
		return certificado;
	};
}
