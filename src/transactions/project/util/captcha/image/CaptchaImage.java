package transactions.project.util.captcha.image;

import java.io.IOException;
import java.sql.Types;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dinamica.Recordset;
import dinamica.RecordsetException;

public class CaptchaImage extends HttpServlet {
	private static final long serialVersionUID = -9093370307882399879L;

	public CaptchaImage() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setContentType("image/jpeg");

			// 生成随机字串
			String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
			// 存入会话session
			HttpSession session = request.getSession(true);
//			session.setAttribute("rand", verifyCode.toLowerCase());
			Recordset rs = new Recordset();
			rs.append("rand_code", Types.VARCHAR);
			rs.addNew();
			rs.setValue("rand_code", verifyCode.toLowerCase());
			session.setAttribute("rand", rs);
			
			// 生成图片
			int w = 200, h = 80;
			VerifyCodeUtils.outputImage(w, h, response.getOutputStream(), verifyCode);
		} catch (RecordsetException e) {
			e.printStackTrace();
		}
	}

}
