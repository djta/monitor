package com.ccms.dinamica.domain.panel;

import java.io.File;

import dinamica.FolderFileList;
import dinamica.Recordset;

public class ImageList extends FolderFileList {

	String path = "";
	
	@Override
	public Recordset getRecordset(Recordset inputParams) throws Throwable {
		
		path = getContext().getRealPath("/") + File.separator + "images" + File.separator + "panel" + File.separator;
		File f = new File(path);
		if (!f.isDirectory()) {
			throw new Throwable("未找到面板路径!");
		}
		return super.getRecordset(inputParams);
	}

	@Override
	public String getFolderName() throws Throwable {
		return path;
	}

	@Override
	public boolean accept(File dir, String name) {
		
		if	(	name.toLowerCase().endsWith(".gif") || 
				name.toLowerCase().endsWith(".jpg") || 
				name.toLowerCase().endsWith(".jpeg") ||
				name.toLowerCase().endsWith(".jpe") ||
				name.toLowerCase().endsWith(".jfif") ||
				name.toLowerCase().endsWith(".png") ||
				name.toLowerCase().endsWith(".bmp") ||
				name.toLowerCase().endsWith(".tiff") ||
				name.toLowerCase().endsWith(".tif")
			)
			return true;
		else
			return false;
	}

}
