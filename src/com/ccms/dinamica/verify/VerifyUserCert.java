package com.ccms.dinamica.verify;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class VerifyUserCert {

	private static byte[] decrypt(byte[] data, File pubkeystore) throws Exception {
		java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.FileInputStream(pubkeystore));
		PublicKey pubkey = (PublicKey) in.readObject();
		in.close();

		byte[] dsadata = VerifyDSA.verifyByPubKey(pubkey, data);
		return dsadata;
	}

	private static byte[] encrypt(byte[] data, File prikeystore) throws Exception {
		java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.FileInputStream(prikeystore));
		PrivateKey myprikey = (PrivateKey) in.readObject();
		in.close();

		byte[] dsadata = VerifyDSA.encryptByPriKey(myprikey, data);
		return dsadata;
	}

	public static String encrypt(String data, String prikeyname) throws Exception {
		return encrypt(data, new File(prikeyname));
	}

	public static String encrypt(String data, File prikey) throws Exception {
		byte[] bs = encrypt(data.getBytes(), prikey);
		String s = VerifyBase64.encode(bs);
		// System.out.println("加密结果： " + s);
		if (s.endsWith("==")) {
			s = s.substring(0, s.length() - 2) + "2";
		} else if (s.endsWith("=")) {
			s = s.substring(0, s.length() - 1) + "1";
		} else {
			s += "0";
		}
		return s;
	}

	private static String[] split(String str) {
		if ((null == str) || str.equals("")) {
			return new String[0];
		}
		String seperator = ",";
		ArrayList<String> temp = new ArrayList<String>();
		int oldPos = 0;
		int newPos = str.indexOf(seperator);
		int parentLength = str.length();
		int subStrLength = seperator.length();
		if (newPos != -1) {
			newPos += subStrLength;
		}
		while ((newPos <= parentLength) && (newPos != -1)) {
			temp.add(str.substring(oldPos, newPos - subStrLength));
			oldPos = newPos;
			newPos = str.indexOf(seperator, oldPos);
			if (newPos != -1) {
				newPos += seperator.length();
			}
		}
		if (oldPos <= parentLength) {
			temp.add(str.substring(oldPos));
		}
		return (String[]) temp.toArray(new String[temp.size()]);
	}

	public static VerifyUser verify(String cert, File pubkey) throws Exception {
		if (cert == null || cert.length() < 1) {
			return null;
		}

		if (cert.endsWith("0")) {
			cert = cert.substring(0, cert.length() - 1);
		} else if (cert.endsWith("1")) {
			cert = cert.substring(0, cert.length() - 1) + "=";
		} else if (cert.endsWith("2")) {
			cert = cert.substring(0, cert.length() - 1) + "==";
		}
		byte[] bs = VerifyBase64.decode(cert);
		byte[] out = decrypt(bs, pubkey);

		if (out == null) {
			return null;
		}
		String str = new String(out);

		String[] strs = split(str);

		VerifyUser user = new VerifyUser(strs);
		if (System.currentTimeMillis() - user.getSignTime() > 60000L) {
			return null;
		}

		return user;
	}

	public static VerifyUser verify(String cert, String pubkeyname) throws Exception {
		return verify(cert, new File(pubkeyname));
	}
}
