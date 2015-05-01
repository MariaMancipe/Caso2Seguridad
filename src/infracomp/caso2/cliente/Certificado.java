package infracomp.caso2.cliente;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.x509.X509V3CertificateGenerator;

/**
 * 
 * @author Maria Paula Mancipe Diaz 
 * @author Santiago Abisambra Castillo
 * Esta clase se encarga de generar los certificados, las llaves asimetricas, y el codigo criptografico de hash
 * Tambien se encarga de cifrar y descrifrar simetrica y asimetricamente
 *
 */
public class Certificado
{
	
	public final static String ASIMETRICO = "RSA";
	
	public final static String SIMETRICO = "AES";
	
	public final static String HMAC = "HMACSHA256";
	
	public final static String PADDING = "AES/ECB/PKCS5Padding";
	
	//---------------------------------------------------------------
	//Atributos
	//---------------------------------------------------------------
	/**
	 * Certificado del cliente
	 */
	private X509Certificate certificado;
	
	/**
	 * fecha de inicio del certificado
	 */
	private Date fechaInicio;
	
	/**
	 * Fecha de fin del certificado
	 */
	private Date fechaFin;
	
	/**
	 * Llaves asimetricas del cliente
	 */
	private KeyPair llaves;
	
	//---------------------------------------------------------------
	//Constructor
	//---------------------------------------------------------------
	
	/**
	 * Se llama al metodo generarLlaves y al de generarCertificado 
	 */
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
	
	
	//---------------------------------------------------------------
	//Metodos
	//---------------------------------------------------------------
	
	/**
	 * Genera las llaves asimetricas del cliente
	 * Las asigna al atributo llaves
	 * RSA
	 * 1024 BITS
	 * @throws NoSuchAlgorithmException
	 */
	private void generarLlaves( ) throws NoSuchAlgorithmException{
		
		KeyPairGenerator generador = KeyPairGenerator.getInstance(ASIMETRICO);
		generador.initialize(1024, new SecureRandom());
		llaves = generador.generateKeyPair();
	}
	
	/**
	 * Genera el certificado X509
	 * El certificado dura 1 mes desde el dia en que se crea
	 * La firma es MariaMancipeYSantiagoAbisambra
	 * El algoritmo es HMACSHA256 con RSA
	 * @throws CertificateEncodingException
	 * @throws InvalidKeyException
	 * @throws IllegalStateException
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 */
	private void generarCertificado( ) throws CertificateEncodingException, InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, SignatureException{
		
		fechaInicio = new Date(System.currentTimeMillis());
		fechaFin = new Date(System.currentTimeMillis() + (30*1000*60*60*24));
		
		X509V3CertificateGenerator certificateGenerator = new X509V3CertificateGenerator();
		X500Principal firma = new X500Principal("CN=MariaMancipeYSantiagoAbisambra");
		
		certificateGenerator.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		certificateGenerator.setSubjectDN(firma);
		certificateGenerator.setIssuerDN(firma);
		certificateGenerator.setNotBefore(fechaInicio);
		certificateGenerator.setNotAfter(fechaFin);
		certificateGenerator.setPublicKey(llaves.getPublic());
		//Aqui puede haber un error
		
		certificateGenerator.setSignatureAlgorithm("SHA256WITHRSA");
		certificado = certificateGenerator.generate(llaves.getPrivate());
	};
	
	/**
	 * Crea el certificado a partir de los bytes que son recibidos por el socket del servidor
	 * Se verifica la validez del certificado
	 * @param recibidosEl flujo de bytes recibidos
	 * @return La llave publica del servidor
	 */
	public PublicKey crearCertificado( byte[] recibidos){
		
		InputStream inCer = new ByteArrayInputStream(recibidos);
		try{
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate certificado = (X509Certificate) cf.generateCertificate(inCer);
			inCer.close();
			certificado.checkValidity();
			return certificado.getPublicKey();
		}
		catch(Exception e){
			System.out.println("Error creando CertificateFactory: " + e.getMessage());
			return null;
		}
		
	}
	
	/**
	 * Descifra el mensaje que viene con la llave simetrica del servidor
	 * AES
	 * @param recibidos La cadena de texto recibida convertida en un array de bytes
	 * @return La llave simetrica del servidor
	 */
	public SecretKey descifrarMensaje( byte[] recibidos ){
		try {
			Cipher cipher = Cipher.getInstance(ASIMETRICO);
			cipher.init(Cipher.DECRYPT_MODE, llaves.getPrivate());
			byte [] clearText = cipher.doFinal(recibidos);
			SecretKey llave = new SecretKeySpec(clearText, 0, clearText.length, SIMETRICO);
			return llave;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Hay un error descifrando la llave simetrica: "+ e.getMessage());
			return null;
		}
	}
	
	
	/**
	 * Cifra las coordenadas con la llave simetrica del servidor
	 * AES
	 * La cadena de texto es convertida en hexadecimales
	 * @param llave la llave simetrica del servidor
	 * @param coordenadas las coordenadas
	 * @return Una cadena de texto con las coordenadas cifradas 
	 */
	public String cifrarCoordenadasSimetrica(SecretKey llave,  String coordenadas ){
		byte [] cipheredText;
		try {

		Cipher cipher = Cipher.getInstance(PADDING);
		
		byte [] clearText = coordenadas.getBytes();
		cipher.init(Cipher.ENCRYPT_MODE, llave);

		cipheredText = cipher.doFinal(clearText);
		String hexa = deByteString(cipheredText);

		String s2 = hexa;
		
		return s2;
		}
		catch (Exception e) {
		System.out.println("Excepcion cifrando coordenadas (simetrica): " + e.getMessage());
		return null;
		}
		
	}
	
	/**
	 * Genera el codigo criptografico de hash de las coordenadas
	 * HMACSHA256
	 * @param llaveSimetrica La llave simetrica del servidor
	 * @param coordenadas las coordenadas de la unidad de distribucion
	 * @return El arreglo de bytes con el codigo criptografico de hash
	 */
	public byte[] encriptarHash(SecretKey llaveSimetrica, String coordenadas ){
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			sha256_HMAC.init(llaveSimetrica);
			byte[] codigo = sha256_HMAC.doFinal(coordenadas.getBytes());
			return codigo;
		} catch (Exception e) {
			System.out.println("Hubo un error generarando el codigo de integridad: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * Cifra con la llave publica del servidor el codigo criptografico de hash de las coordenadas
	 * RSA
	 * El arreglo de bytes cifrado se convierte en hexadecimales
	 * @param llavePS La llave publica del servidor
	 * @param hash El codigo criptografico de hash de las coordenadas
	 * @return
	 */
	public String cifrarAsimetrico(PublicKey llavePS, byte[] hash){
		try{
			Cipher cipher = Cipher.getInstance("RSA");
					
			cipher.init(Cipher.ENCRYPT_MODE, llavePS);
			byte [] cipheredText = cipher.doFinal(hash);
			String cifrado = deByteString(cipheredText);
			return cifrado;
		}catch(Exception e){
			System.out.println("Hubo un error cifrando asimetricamente: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] deStringByte(String cadena)
	{
		byte[] ret = new byte[cadena.length()/2];
		for (int i = 0 ; i < ret.length ; i++) {
			ret[i] = (byte) Integer.parseInt(cadena.substring(i*2,(i+1)*2), 16);
		}
		return ret;
	}
	
	public String deByteString(byte[] bytes){
		String ret = "";
		for (int i = 0 ; i < bytes.length ; i++) {
			String g = Integer.toHexString(((char)bytes[i])&0x00ff);
			ret += (g.length()==1?"0":"") + g;
		}
		return ret;
	}
	
	/**
	 * @return el par de llaves asimetricas del cliente
	 */
	public KeyPair darLlaves(){
		return llaves;
	}
	
	/**
	 * @return el certificado x509 del cliente
	 */
	public X509Certificate darCertificado(){
		return certificado;
	}
	
}
