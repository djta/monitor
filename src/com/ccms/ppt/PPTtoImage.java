package com.ccms.ppt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.usermodel.RichTextRun;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlide;

public class PPTtoImage {

	public static void main(String[] args) {
		doPPTtoImage2007(new File("D:/test/ppt/test.pptx"), "D:/test/ppt", "test", "jpg", 960, 720);
	}

	/**
	 * PPT转图片 （jpeg）(2007)
	 * 
	 * @param file
	 * @param path
	 *            存放路径
	 * @param picName
	 *            图片前缀名称 如 a 生成后为a_1,a_2 ...
	 * @param picType
	 *            转成图片的类型，无点 如 jpg bmp png ...
	 * @return true/false
	 */
	public static String [] doPPTtoImage2007(File file, String path, String picName, String picType, int width, int height) {
		try {
			FileInputStream is = new FileInputStream(file);
			XMLSlideShow xmlSlideShow = new XMLSlideShow(is);
			XSLFSlide[] xslfSlides = xmlSlideShow.getSlides();

			is.close();
			String [] filePathArray = new String [xslfSlides.length];
			for (int i = 0; i < xslfSlides.length; i++) {
				//设置字体为宋体，解决中文乱码问题
	            CTSlide rawSlide=xslfSlides[i].getXmlObject();
	            CTGroupShape gs = rawSlide.getCSld().getSpTree();
	            CTShape[] shapes = gs.getSpArray();
	            for (CTShape shape : shapes) {
					CTTextBody tb = shape.getTxBody();
					if (null == tb)
						continue;
					CTTextParagraph[] paras = tb.getPArray();
					CTTextFont font = CTTextFont.Factory
							.parse("<xml-fragment xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\">"
									+ "<a:rPr lang=\"zh-CN\" altLang=\"en-US\" dirty=\"0\" smtClean=\"0\"> "
									+ "<a:latin typeface=\"+mj-ea\"/> "
									+ "</a:rPr>" + "</xml-fragment>");
					for (CTTextParagraph textParagraph : paras) {
						CTRegularTextRun[] textRuns = textParagraph.getRArray();
						for (CTRegularTextRun textRun : textRuns) {
							CTTextCharacterProperties properties = textRun.getRPr();
							properties.setLatin(font);
						}
					}
	            }

				BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

				Graphics2D graphics = img.createGraphics();
				graphics.setPaint(Color.white);
				graphics.fill(new Rectangle2D.Float(0, 0, width, height));
				xslfSlides[i].draw(graphics);

				FileOutputStream out = new FileOutputStream(path + "/" + picName + "_" + (i + 1) + "." + picType);
				javax.imageio.ImageIO.write(img, "jpeg", out);
				out.close();
				filePathArray[i] = path + "/" + picName + "_" + (i + 1) + "." + picType;
			}
			return filePathArray;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String [0];
	}

	/**
	 * PPT转图片 （jpeg）(2003)
	 * 
	 * @param file
	 * @param path
	 *            存放路径
	 * @param picName
	 *            图片前缀名称 如 a 生成后为a_1,a_2 ...
	 * @param picType
	 *            转成图片的类型，无点 如 jpg bmp png ...
	 * @return true/false
	 */
	public static String [] doPPTtoImage(File file, String path, String picName, String picType, int width, int height) {
		try {
			FileInputStream is = new FileInputStream(file);
			SlideShow ppt = new SlideShow(is);
			is.close();
			// Dimension pgsize = ppt.getPageSize();
			org.apache.poi.hslf.model.Slide[] slide = ppt.getSlides();
			String [] filePathArray = new String [slide.length];
			for (int i = 0; i < slide.length; i++) {

				System.out.print("第" + i + "页。");
				
				TextRun[] textRuns=slide[i].getTextRuns();
	            for(TextRun tr:textRuns){
	               RichTextRun rt=tr.getRichTextRuns()[0];
	               rt.setFontName("宋体");
	            }
				
				BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

				Graphics2D graphics = img.createGraphics();
				graphics.setPaint(Color.white);
				graphics.fill(new Rectangle2D.Float(0, 0, width, height));
				slide[i].draw(graphics);

				FileOutputStream out = new FileOutputStream(path + "/" + picName + "_" + (i + 1) + "." + picType);
				javax.imageio.ImageIO.write(img, "jpeg", out);
				out.close();
				filePathArray[i] = path + "/" + picName + "_" + (i + 1) + "." + picType;
			}
			return filePathArray;
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
		}
		return new String [0];
	}

}
