import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

public class Cryptography{

	public static void main(String[] args) throws Exception{
	
	// Step:2	
		
	 //TASK: Do the below when the client starts	
		
		// A : Sender
		KeyPair generateKeyPairSender = CryptographyExample.generateKeyPair();
        byte[] publicKeySender = generateKeyPairSender.getPublic().getEncoded();
        byte[] privateKeySender = generateKeyPairSender.getPrivate().getEncoded();	
	
		// B : Recipient
		KeyPair generateKeyPairRecipient = CryptographyExample.generateKeyPair();
        byte[] publicKeyRecipient = generateKeyPairRecipient.getPublic().getEncoded();
        byte[] privateKeyRecipient = generateKeyPairRecipient.getPrivate().getEncoded();	
		
	
		
    // TASK: Extend the register method for publishing the public key openly along with the client information.
	// TASK: Also store these public keys in the server registry, make a vector or something 
		
	// TASK: A new message type called "FETCHKEY" is also given. Please implement that as well
		
		// Encryption -> Done at A
		
	    String message = "helloee"; // M
		byte[] sentEncryptedMessage = CryptographyExample.encrypt(publicKeyRecipient,message.getBytes()); // M'
	
		// The base64 format encoding function -> Done at A
		String sentMessage = java.util.Base64.getEncoder().encodeToString(sentEncryptedMessage);
		
		// The base64 decoder -> Done at B
		byte[] receivedEncryptedMessage = java.util.Base64.getDecoder().decode(sentMessage);
		
		// Decryption -> Done at B
		byte[] receivedDecryptedMessage = CryptographyExample.decrypt(privateKeyRecipient, receivedEncryptedMessage);
		
		// Converting to a string format -> Done at B
		String receivedMessage = new String(receivedDecryptedMessage);
		
	// TASK : I have no idea about this communication thing. That's why have added these things: Done at A and B. Please add them to the corresponding client codes	
		
		
		// Step : 3
		
		// Hashing the message etc.-> Done at A
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] hashedMessage = md.digest(sentEncryptedMessage); // H = Hash(M')
		byte[] sentHashedMessage = CryptographyExample.encryptUsingPrivate(privateKeySender, hashedMessage); // H'
		String sentHash = java.util.Base64.getEncoder().encodeToString(sentHashedMessage);
		
		// Decypting -> Done at B
		byte[] receivedEncryptedHash = java.util.Base64.getDecoder().decode(sentHash); // H'
	    byte[] receivedHashedMessage = CryptographyExample.decryptUsingPublic(publicKeySender, receivedEncryptedHash); // K_pubA(H')
		
		
		//Checking -> Done at B
	    String s1 = new String(receivedHashedMessage); // K_pubA(H')
		String s2 = new String(hashedMessage); // H = Hash(M')
		
	// TASK : I am using System.out here. Use the corresponding message format for the server-client in your code 
		
		if(s1.equals(s2)){
            System.out.println("Signature matched");
        }
        else{
            System.out.println("Signature not matched");
        }  
		
	// TASK: Add these features in the same way as added for the Step:2
		
		
	} 


}