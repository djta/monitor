/**
 * 
 */
package com.ccms.util.lunece.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.regex.Pattern;
 
import org.apache.commons.lang.ArrayUtils;

/**
 * @author 
 * 文件操作类
 */
public class FileUtil {
	
	public static String normalizePath(String path)
	{
		path = path.replace('\\', '/');
		
		path = StringUtil.replaceEx(path, "../", "/");
		path = StringUtil.replaceEx(path, "./", "/");
		if (path.endsWith(".."))
			path = path.substring(0, path.length() - 2);
		path = path.replaceAll("/+", "/");
		return path;
	}

	public static File normalizeFile(File f)
	{
		String path = f.getAbsolutePath();
		path = normalizePath(path);
		return new File(path);
	}

	public static boolean writeText(String fileName, String content)
	{
		fileName = normalizePath(fileName);
		return writeText(fileName, content, Constant.CHARSET);
	}

	public static boolean writeText(String fileName, String content, String encoding)
	{
		fileName = normalizePath(fileName);
		return writeText(fileName, content, encoding, false);
	}

	public static boolean writeText(String fileName, String content, String encoding, boolean bomFlag)
	{
		fileName = normalizePath(fileName);
		try
		{
			byte bs[] = content.getBytes(encoding);
			if (encoding.equalsIgnoreCase("UTF-8") && bomFlag)
				bs = ArrayUtils.addAll(StringUtil.BOM, bs);
			writeByte(fileName, bs);
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}

	public static byte[] readByte(String fileName)
	{
		fileName = normalizePath(fileName);
		byte r[];
		try{
			FileInputStream fis = new FileInputStream(fileName);
			r = new byte[fis.available()];
			fis.read(r);
			fis.close();
			return r;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		
	}

	public static byte[] readByte(File f)
	{
		f = normalizeFile(f);
		byte r[];
		try{
			FileInputStream fis = new FileInputStream(f);
			r = readByte(((InputStream) (fis)));
			fis.close();
			return r;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	public static byte[] readByte(InputStream is)
	{
		byte r[];
		try{
			r = new byte[is.available()];
			is.read(r);
			return r;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	public static boolean writeByte(String fileName, byte b[])
	{
		fileName = normalizePath(fileName);
		try
		{
			BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(fileName));
			fos.write(b);
			fos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean writeByte(File f, byte b[])
	{
		f = normalizeFile(f);
		try
		{
			BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(f));
			fos.write(b);
			fos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static String readText(File f)
	{
		f = normalizeFile(f);
		return readText(f, Constant.CHARSET);
	}

	public static String readText(File f, String encoding)
	{
		f = normalizeFile(f);
		try{
			String str;
			InputStream is = new FileInputStream(f);
			str = readText(is, encoding);
			is.close();
			return str;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static String readText(InputStream is, String encoding)
	{
		byte bs[] = readByte(is);
		if (encoding.equalsIgnoreCase("utf-8") && StringUtil.hexEncode(ArrayUtils.subarray(bs, 0, 3)).equals("abcdef"))
			bs = ArrayUtils.subarray(bs, 3, bs.length);
		
		try {
			return new String(bs, encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	public static String readText(String fileName)
	{
		fileName = normalizePath(fileName);
		return readText(fileName, Constant.CHARSET);
	}

	public static String readText(String fileName, String encoding)
	{
		fileName = normalizePath(fileName);
		try{
			String str;
			InputStream is = new FileInputStream(fileName);
			str = readText(is, encoding);
			is.close();
			return str;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static String readURLText(String urlPath)
	{
		return readURLText(urlPath, Constant.CHARSET);
	}

	public static String readURLText(String urlPath, String encoding)
	{
		try{
			StringBuffer sb;
			URL url = new URL(urlPath);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), encoding));
			sb = new StringBuffer();
			String line;
			while ((line = in.readLine()) != null) 
				sb.append(line + "\n");
			in.close();
			return sb.toString();
			
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	public static boolean delete(String path)
	{
		path = normalizePath(path);
		File file = new File(path);
		return delete(file);
	}

	public static boolean delete(File f)
	{
		f = normalizeFile(f);
		if (!f.exists())
		{
			return false;
		}
		if (f.isFile())
			return f.delete();
		else
			return deleteDir(f);
	}

	private static boolean deleteDir(File dir)
	{
		dir = normalizeFile(dir);
		if (deleteFromDir(dir) && dir.delete())
			return true;
		return false;
	}

	public static boolean mkdir(String path)
	{
		path = normalizePath(path);
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdirs();
		return true;
	}

	public static boolean deleteEx(String fileName)
	{
		fileName = normalizePath(fileName);
		int index1 = fileName.lastIndexOf("\\");
		int index2 = fileName.lastIndexOf("/");
		index1 = index1 <= index2 ? index2 : index1;
		String path = fileName.substring(0, index1);
		String name = fileName.substring(index1 + 1);
		File f = new File(path);
		if (f.exists() && f.isDirectory())
		{
			File files[] = f.listFiles();
			for (int i = 0; i < files.length; i++)
				if (Pattern.matches(name, files[i].getName()))
					files[i].delete();

			return true;
		} else
		{
			return false;
		}
	}

	public static boolean deleteFromDir(String dirPath)
	{
		dirPath = normalizePath(dirPath);
		File file = new File(dirPath);
		return deleteFromDir(file);
	}

	public static boolean deleteFromDir(File dir)
	{
		dir = normalizeFile(dir);
		if (!dir.exists())
		{
			return false;
		}
		if (!dir.isDirectory())
		{
			return false;
		}
		File tempList[] = dir.listFiles();
		for (int i = 0; i < tempList.length; i++)
			if (!delete(tempList[i]))
				return false;

		return true;
	}

	public static boolean copy(String oldPath, String newPath, FileFilter filter)
	{
		oldPath = normalizePath(oldPath);
		newPath = normalizePath(newPath);
		File oldFile = new File(oldPath);
		File oldFiles[] = oldFile.listFiles(filter);
		boolean flag = true;
		if (oldFiles != null)
		{
			for (int i = 0; i < oldFiles.length; i++)
				if (!copy(oldFiles[i], newPath + "/" + oldFiles[i].getName()))
					flag = false;

		}
		return flag;
	}

	public static boolean copy(String oldPath, String newPath)
	{
		oldPath = normalizePath(oldPath);
		newPath = normalizePath(newPath);
		File oldFile = new File(oldPath);
		return copy(oldFile, newPath);
	}

	public static boolean copy(File oldFile, String newPath)
	{
		oldFile = normalizeFile(oldFile);
		newPath = normalizePath(newPath);
		if (!oldFile.exists())
		{
			return false;
		}
		if (oldFile.isFile())
			return copyFile(oldFile, newPath);
		else
			return copyDir(oldFile, newPath);
	}

	private static boolean copyFile(File oldFile, String newPath)
	{
		oldFile = normalizeFile(oldFile);
		newPath = normalizePath(newPath);
		if (!oldFile.exists())
		{
			return false;
		}
		if (!oldFile.isFile())
		{
			return false;
		}
		try
		{
			int byteread = 0;
			InputStream inStream = new FileInputStream(oldFile);
			FileOutputStream fs = new FileOutputStream(newPath);
			byte buffer[] = new byte[1024];
			while ((byteread = inStream.read(buffer)) != -1) 
				fs.write(buffer, 0, byteread);
			fs.close();
			inStream.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static boolean copyDir(File oldDir, String newPath)
	{
		return false;
	}

	public static boolean move(String oldPath, String newPath)
	{
		oldPath = normalizePath(oldPath);
		newPath = normalizePath(newPath);
		return copy(oldPath, newPath) && delete(oldPath);
	}

	public static boolean move(File oldFile, String newPath)
	{
		oldFile = normalizeFile(oldFile);
		newPath = normalizePath(newPath);
		return copy(oldFile, newPath) && delete(oldFile);
	}

	public static void serialize(Serializable obj, String fileName)
	{
		fileName = normalizePath(fileName);
		try
		{
			FileOutputStream f = new FileOutputStream(fileName);
			ObjectOutputStream s = new ObjectOutputStream(f);
			s.writeObject(obj);
			s.flush();
			s.close();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static byte[] serialize(Serializable obj)
	{
		ByteArrayOutputStream b = null;
		b = new ByteArrayOutputStream();
		try{
			ObjectOutputStream s = new ObjectOutputStream(b);
			s.writeObject(obj);
			s.flush();
			s.close();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return b.toByteArray();
		
	}

	public static Object unserialize(String fileName)
	{
		fileName = normalizePath(fileName);
		Object o = null;
		
		try{
			FileInputStream in = new FileInputStream(fileName);
			ObjectInputStream s = new ObjectInputStream(in);
			o = s.readObject();
			s.close();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return o;
		
	}

	public static Object unserialize(byte bs[])
	{
		Object o = null;
		try {
			
			ByteArrayInputStream in = new ByteArrayInputStream(bs);
			ObjectInputStream s = new ObjectInputStream(in);
			o = s.readObject();
			s.close();
			
		}catch (Exception e) {
			e.printStackTrace();
			return null;
			
		}
		return o;
	}
	
}