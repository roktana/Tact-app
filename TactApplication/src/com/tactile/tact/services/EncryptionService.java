package com.tactile.tact.services;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import android.app.Activity;
import android.provider.Settings;
import android.util.Base64;

public class EncryptionService {

    protected static final String UTF8 = "utf-8";
    private static final String ENC_KEY = "someEncryptionKey";	
	
    public static String encrypt( Activity context, String value ) {
        try {
            final byte[] bytes = value!=null ? value.getBytes(UTF8) : new byte[0];
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(ENC_KEY.toCharArray()));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID).getBytes(UTF8), 20));
            return new String(Base64.encode(pbeCipher.doFinal(bytes), Base64.NO_WRAP),UTF8);
        } catch( Exception e ) {
            throw new RuntimeException(e);
        }

    }

    public static String decrypt( Activity context, String value){
        try {
            final byte[] bytes = value!=null ? Base64.decode(value,Base64.DEFAULT) : new byte[0];
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(ENC_KEY.toCharArray()));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID).getBytes(UTF8), 20));
            return new String(pbeCipher.doFinal(bytes),UTF8);
        } catch( IllegalArgumentException illEx) {
            return "";
        } catch( IllegalBlockSizeException bse) {
        	return "";
        } catch( Exception e) {
            throw new RuntimeException(e);
        }
    }	
	
}
