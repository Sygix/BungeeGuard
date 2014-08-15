package fr.greenns.BungeeGuard.Authenticator;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;

public class Authenticator {

	public static boolean valid_code(String code, String secretKey) {
		int generatedCode = -1;
		int enterCode = -1;
		try {
			generatedCode = verify_code(secretKey, System.currentTimeMillis() / 30000L);
			enterCode = Integer.parseInt(code);
		} catch (NumberFormatException | InvalidKeyException | NoSuchAlgorithmException e) {}
		
		return (generatedCode > 0 && generatedCode == enterCode);
	}
	
	public static int verify_code(
			  String secretKey,
			  long t)
			  throws NoSuchAlgorithmException,
			    InvalidKeyException {
			  byte[] data = new byte[8];
			  long value = t;
			  for (int i = 8; i-- > 0; value >>>= 8) {
			    data[i] = (byte) value;
			  }

			  Base32 codec = new Base32();
			  byte[] key = codec.decode(secretKey);


			  SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
			  Mac mac = Mac.getInstance("HmacSHA1");
			  mac.init(signKey);
			  byte[] hash = mac.doFinal(data);


			  int offset = hash[20 - 1] & 0xF;
			  
			  // We're using a long because Java hasn't got unsigned int.
			  long truncatedHash = 0;
			  for (int i = 0; i < 4; ++i) {
			    truncatedHash <<= 8;
			    // We are dealing with signed bytes:
			    // we just keep the first byte.
			    truncatedHash |= (hash[offset + i] & 0xFF);
			  }


			  truncatedHash &= 0x7FFFFFFF;
			  truncatedHash %= 1000000;


			  return (int) truncatedHash;
		}	
}
