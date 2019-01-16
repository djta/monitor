package com.ccms.dinamica.verify;

public class TestVerify {

	public static final String  PATH = "D:/";
	public static final String PUBKEY = "DESOFT_KPI";
	public static void main(String[] a) throws Exception {
		genKeys();
		genKeys_withMd5();
		//私钥加密 公钥解密
		String s = encrypttest();
		decrypttest(s);
	}
	
	private static void genKeys() throws Exception {
		String pubkeyname = PATH + "pubkey.dat";
		String prikeyname = PATH + "privatekey.dat";
		VerifyDSA.genPubAndPriKey(pubkeyname, prikeyname, PUBKEY);

	}

	private static void genKeys_withMd5() throws Exception {
		String pubkeyname = PATH + "pubkey.dat";
		String prikeyname = PATH + "privatekey.dat";
		VerifyDSA.genPubAndPriKeyStore(pubkeyname, prikeyname, "", "");

	}

	private static String encrypttest() throws Exception {
		String prikeyname = PATH + "privatekey.dat";

		String s = VerifyUserCert.encrypt("yxd2,463457118932502," + System.currentTimeMillis(), prikeyname);

		System.out.println("加密结果： " + s);
		System.out.println("加密长度： " + s.length());
		return s;
	}

	private static void decrypttest(String str) throws Exception {
		if (str.endsWith("0")) {
			str = str.substring(0, str.length() - 1);
		} else if (str.endsWith("1")) {
			str = str.substring(0, str.length() - 1) + "=";
		} else if (str.endsWith("2")) {
			str = str.substring(0, str.length() - 1) + "==";
		}
		String pubkeyname = PATH + "pubkey.dat";
		VerifyUser user = VerifyUserCert.verify(str, pubkeyname);
		if (user != null) {

			System.out.println("uid： " + user.getUserID());
			System.out.println("ucode： " + user.getUserCode());
			System.out.println("signtime： " + user.getSignTime());
		} else {
			System.out.println("验证失败");
		}
	}
}
