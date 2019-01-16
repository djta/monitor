package transactions.project.fitness.util;

public class Base64 {
	private final static int DEFAULT_BASE64_ENCRYPT_NUM = 4;
	private static String encode(String s) {
		if (s == null){
			return null;
		}
		return (new sun.misc.BASE64Encoder()).encode(s.getBytes());
	}

	// 将 BASE64 编码的字符串 s 进行解码 解密
	private static String decode(String s) {
		if (s == null){
			return null;
		}
		sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(s);
			return new String(b);
		} catch (Exception e) {
			return null;
		}
	}

	public static String encodeMore(String str, Integer encryptnum) {
		if( null == encryptnum || 0 == encryptnum ){
			encryptnum = DEFAULT_BASE64_ENCRYPT_NUM;
		}
		String outstr = str;
		for(int i = 0; i < encryptnum; i++){
			outstr = encode(outstr);
		}
		return outstr;
	}

	public static String decodeMore(String str, Integer encryptnum) {
		if( null == encryptnum || 0 == encryptnum ){
			encryptnum = DEFAULT_BASE64_ENCRYPT_NUM;
		}
		String outstr = str;
		for(int i = 0; i < encryptnum; i++){
			outstr = decode(outstr);
		}
		return outstr;
	}

	public static void main(String[] args) {
		String a = encodeMore("ABC_FH1810290001", 2);
		System.out.println(a);// 加密
		System.out.println(decodeMore(a, 2));// 解密
	}
}
