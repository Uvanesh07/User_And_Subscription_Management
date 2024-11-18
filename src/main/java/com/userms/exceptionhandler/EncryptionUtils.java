package com.userms.exceptionhandler;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptionUtils {

    private static final String AES_ALGORITHM = "AES";
    String secretKey = "ago-gosTech@(1234)*";
    public String encrypt(String input) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        // Adjust key length to 16, 24, or 32 bytes
        System.out.println("Entered Encrypted");
        byte[] adjustedKey = new byte[32];
        System.arraycopy(secretKey.getBytes(), 0, adjustedKey, 0, Math.min(secretKey.getBytes().length, 32));
        SecretKeySpec secretKeySpec = new SecretKeySpec(adjustedKey, AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedBytes = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedInput) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        // Adjust key length to 16, 24, or 32 bytes
        System.out.println("Entered Decrypted");
        byte[] adjustedKey = new byte[32];
        System.arraycopy(secretKey.getBytes(), 0, adjustedKey, 0, Math.min(secretKey.getBytes().length, 32));
        SecretKeySpec secretKeySpec = new SecretKeySpec(adjustedKey, AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedInput));
        return new String(decryptedBytes);
    }

    public void example() {
        try {
            String originalData = "12345678";
            // Encrypt data
            String encryptedData = encrypt(originalData);
            System.out.println("Encrypted Data: " + encryptedData);
            // Decrypt data
            String decryptedData = decrypt(encryptedData);
            System.out.println("Decrypted Data: " + decryptedData);
//            if(originalData.equals(decryptedData)){
//                System.out.println("same " + decryptedData+ " " + originalData);
//            }else {
//                System.out.println("False Not Equal");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
