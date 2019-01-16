package com.ccms.dinamica.verify;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;

import javax.crypto.SecretKey;

public class VerifyDSA {

	public static byte[] encryptByPriKey(String priname, byte[] data) throws IOException, ClassNotFoundException, NoSuchAlgorithmException,
			InvalidKeyException, SignatureException {
		java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.FileInputStream(priname));
		PrivateKey myprikey = (PrivateKey) in.readObject();
		in.close();
		// 初始一个Signature对象,并用私钥对信息签名
		java.security.Signature signet = java.security.Signature.getInstance("DSA");
		signet.initSign(myprikey);
		signet.update(data);
		byte[] signed = signet.sign();
		// 把信息和签名保存在一个文件中(myinfo.dat)
		// java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(
		// new java.io.FileOutputStream(outfilename));
		ByteArrayOutputStream br = new ByteArrayOutputStream();
		java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(br);
		out.writeObject(data);
		out.writeObject(signed);
		out.close();
		return br.toByteArray();
	}

	public static byte[] encryptByPriKey(PrivateKey myprikey, byte[] data) throws IOException, ClassNotFoundException, NoSuchAlgorithmException,
			InvalidKeyException, SignatureException {
		// 初始一个Signature对象,并用私钥对信息签名
		java.security.Signature signet = java.security.Signature.getInstance("DSA");
		signet.initSign(myprikey);
		signet.update(data);
		byte[] signed = signet.sign();
		// 把信息和签名保存在一个文件中(myinfo.dat)
		// java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(
		// new java.io.FileOutputStream(outfilename));
		ByteArrayOutputStream br = new ByteArrayOutputStream();
		java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(br);
		out.writeObject(data);
		out.writeObject(signed);
		out.close();
		return br.toByteArray();
	}

	public static byte[] verifyByPubKey(PublicKey pubkey, byte[] data) throws IOException, ClassNotFoundException, NoSuchAlgorithmException,
			InvalidKeyException, SignatureException {

		// 读入签名和信息
		java.io.ObjectInputStream in = new java.io.ObjectInputStream(new ByteArrayInputStream(data));
		byte[] info = (byte[]) in.readObject();
		// System.out.println(new String(info));
		byte[] signed = (byte[]) in.readObject();
		in.close();

		// 初始一个Signature对象,并用公钥和签名进行验证
		java.security.Signature signetcheck = java.security.Signature.getInstance("DSA");
		signetcheck.initVerify(pubkey);
		signetcheck.update(info);
		if (signetcheck.verify(signed)) {
			return info;
		}
		return null;
		// signetcheck.

	}

	public static byte[] verifyByPubKey(String pubname, byte[] data) throws IOException, ClassNotFoundException, NoSuchAlgorithmException,
			InvalidKeyException, SignatureException {
		// 读入公钥
		java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.FileInputStream(pubname));
		PublicKey pubkey = (PublicKey) in.readObject();
		in.close();

		// 读入签名和信息
		in = new java.io.ObjectInputStream(new ByteArrayInputStream(data));
		byte[] info = (byte[]) in.readObject();
		// System.out.println(new String(info));
		byte[] signed = (byte[]) in.readObject();
		in.close();

		// 初始一个Signature对象,并用公钥和签名进行验证
		java.security.Signature signetcheck = java.security.Signature.getInstance("DSA");
		signetcheck.initVerify(pubkey);
		signetcheck.update(info);
		if (signetcheck.verify(signed)) {
			return info;
		}
		return null;
		// signetcheck.

	}

	public static void genPubAndPriKey(String pubname, String priname, String key) throws NoSuchAlgorithmException, IOException {
		java.security.KeyPairGenerator keygen = java.security.KeyPairGenerator.getInstance("DSA");
		// 如果设定随机产生器就用如相代码初始化
		SecureRandom secrand = new SecureRandom();
		secrand.setSeed(key.getBytes()); // 初始化随机产生器
		keygen.initialize(512, secrand); // 初始化密钥生成器
		// 否则
		// keygen.initialize(512);
		// 生成密钥公钥pubkey和私钥prikey
		KeyPair keys = keygen.generateKeyPair(); // 生成密钥组
		PublicKey pubkey = keys.getPublic();
		PrivateKey prikey = keys.getPrivate();
		// 分别保存在myprikey.dat和mypubkey.dat中,以便下次不在生成
		// (生成密钥对的时间比较长
		java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(new java.io.FileOutputStream(priname));
		out.writeObject(prikey);
		out.close();
		out = new java.io.ObjectOutputStream(new java.io.FileOutputStream(pubname));
		out.writeObject(pubkey);
		out.close();
	}

	public static void genPubAndPriKeyStore(String pubname, String priname, String key, String deskey) throws NoSuchAlgorithmException, IOException {
		java.security.KeyPairGenerator keygen = java.security.KeyPairGenerator.getInstance("DSA");
		// 如果设定随机产生器就用如相代码初始化
		SecureRandom secrand = new SecureRandom();
		secrand.setSeed(key.getBytes()); // 初始化随机产生器
		keygen.initialize(512, secrand); // 初始化密钥生成器
		// 否则
		// keygen.initialize(512);
		// 生成密钥公钥pubkey和私钥prikey
		KeyPair keys = keygen.generateKeyPair(); // 生成密钥组
		PublicKey pubkey = keys.getPublic();
		PrivateKey prikey = keys.getPrivate();

		// 首先生成密钥,并保存
		SecretKey desSecretKey = new javax.crypto.spec.SecretKeySpec(deskey.getBytes(), "DES");

		// 分别保存在myprikey.dat和mypubkey.dat中,以便下次不在生成
		// (生成密钥对的时间比较长
		java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(new java.io.FileOutputStream(priname));
		out.writeObject(prikey);
		out.writeObject(desSecretKey);
		out.close();
		out = new java.io.ObjectOutputStream(new java.io.FileOutputStream(pubname));
		out.writeObject(pubkey);
		out.writeObject(desSecretKey);
		out.close();
	}
}
