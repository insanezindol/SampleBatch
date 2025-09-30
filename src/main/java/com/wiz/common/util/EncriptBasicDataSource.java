package com.wiz.common.util;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncriptBasicDataSource extends org.apache.commons.dbcp.BasicDataSource{
	
	private final static String[] keyHexStrings = {"c0cefc2b812c0f2066fa2176b98b6b80"};
	private final static String[] ivSHextrings  = {"00cefc2b812c0f2066fa2176b98b6b80"};
	
	
	/*public static void main(String[] args){
		
		String param = "xringstat";
		if(args != null && args.length > 0){
			param = args[0];
		}
		
		System.out.println("plainString : " + param);
		System.out.println("encriptString : " + encriptString(param));
		//System.out.println("decriptString : " + decriptString(param));
	}*/
	
	public static String encriptString(String plainString){
		
		String encriptString = null;

		try{						
			int keyIdx = 0;
			
			String keyHexString = keyHexStrings[keyIdx];
	        String ivSHextring = ivSHextrings[keyIdx];
	        
	        byte [] row = hexToBytes(keyHexString);
	        byte [] iv  = hexToBytes(ivSHextring);
	               	        
	        IvParameterSpec ivSpec = new IvParameterSpec(iv);
	        SecretKeySpec key = new SecretKeySpec(row, "AES");
	        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
	               
	        // encryption
	        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
	        byte[] cipherText = cipher.doFinal(plainString.getBytes());
	        encriptString = bytesToHexString(cipherText);	        
	        //encriptString = "" + keyIdx + encriptString;
	        
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return encriptString;
	}
	
	
	public static String decriptString(String encriptString){
		
		String plainString = null;

		try{	
			if(encriptString != null && !encriptString.equals("")){
				int keyIdx = 0;	
			
				String keyHexString = keyHexStrings[keyIdx];
		        String ivSHextring = ivSHextrings[keyIdx];
		        
		        byte [] row = hexToBytes(keyHexString);
		        byte [] iv  = hexToBytes(ivSHextring);	               
		        
		        IvParameterSpec ivSpec = new IvParameterSpec(iv);
		        SecretKeySpec key = new SecretKeySpec(row, "AES");
		        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
	
		        // decryption
		        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		        byte[] plainText = cipher.doFinal(hexToBytes(encriptString));
		        
		        plainString = new String(plainText, "UTF-8");
			}
		
		}catch(Exception e){
			e.printStackTrace();
		}		
		
		return plainString;
	}

	
	private static String bytesToHexString(byte[] bytes){
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<bytes.length;i++){
			String tmpStr = Integer.toHexString(bytes[i]& 0xff);
			if(tmpStr.length()==1) tmpStr="0"+tmpStr;
			sb.append(tmpStr);
			sb.append("");
        }
		return new String(sb);
	}
	

	public static byte[] hexToBytes(String str) {
		if (str==null) {
			return null;
		} else if (str.length() < 2) {
			return null;
		} else {
			int len = str.length() / 2;
			byte[] buffer = new byte[len];
			for (int i=0; i<len; i++) {
				buffer[i] = (byte) Integer.parseInt(str.substring(i*2,i*2+2),16);
			}
			
			return buffer;
		}
	}	
	
	@Override
	public void setUrl(String url){
		super.setUrl(decriptString(url));
	}
	
	@Override
	public void setPassword(String password){
		super.setPassword(decriptString(password));
	}
	
	@Override
	public void setUsername(String username){
		super.setUsername(decriptString(username));
	}
}
