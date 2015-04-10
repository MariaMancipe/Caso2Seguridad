package infracomp.caso2.cliente;

import java.math.BigInteger;
import java.security.KeyPair;
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
	
	private void generarLlaves( ){
		
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
	};
	
	public X509Certificate darCertificado(){
		generarCertificado();
		return certificado;
	};
}
