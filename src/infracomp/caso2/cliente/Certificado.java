package infracomp.caso2.cliente;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
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
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("HUBO UN ERROR AL GENERAR LAS LLAVES: " + e.getMessage());
		}
		generarCertificado();
	}
	
	private void generarLlaves( ) throws NoSuchAlgorithmException{
		
		KeyPairGenerator generador = KeyPairGenerator.getInstance(UnidadDistribucion.ASIMETRICO);
		generador.initialize(1024, new SecureRandom());
		llaves = generador.generateKeyPair();
	}
	
	private void generarCertificado( ){
		
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
		
		certificateGenerator.setSignatureAlgorithm("SHA256WITHRSA");
	};
	
	public X509Certificate darCertificado(){
		generarCertificado();
		return certificado;
	};
}
