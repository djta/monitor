package transactions.project.fitness.util;

import java.io.File;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * word转pdf工具类（只适应于Windows）
 * @author C
 * 2016-12-07
 * 依赖于：
 * 		SaveAsPDFandXPS.exe	下载地址：http://www.microsoft.com/zh-cn/download/confirmation.aspx?id=7
 * 		jacob-1.18.zip		下载地址：https://sourceforge.net/projects/jacob-project/
 * 引入jacob里的jar包，并将jacob的dll文件放到到jdk/jre/bin下
 */
public class Word2Pdf {
	
	private static final int wdFormatPDF = 17;// PDF 格式
	
	/***
	 * word文档装pdf
	 * @param docPath word文档完整路径
	 * @param basePath pdf保存路径
	 * @param fileName 保存文件名
	 * @throws Exception 
	 */
	public static String convert(String docPath, String basePath, String fileName) throws Exception {
		String _pdfPath = null;
		ActiveXComponent app = null;
		Dispatch doc = null;
		try {
			app = new ActiveXComponent("Word.Application");
			app.setProperty("Visible", new Variant(false));
			Dispatch docs = app.getProperty("Documents").toDispatch();

			String pdfPath = basePath + fileName + ".pdf";

			doc = Dispatch.call(docs, "Open", docPath).toDispatch();
			File tofile = new File(pdfPath);
			if (tofile.exists()) {
				tofile.delete();
			}
			Dispatch.call(doc, "SaveAs", pdfPath, wdFormatPDF);
			_pdfPath = pdfPath;
		} catch (Exception e) {
			throw new Exception("pdf转换失败：" + e.getMessage());
		} finally {
			Dispatch.call(doc, "Close", false);
			if (app != null){
				app.invoke("Quit", new Variant[] {});
			}
			// 如果没有这句话,winword.exe进程将不会关闭
			ComThread.Release();
		}
		return _pdfPath;
	}
}
