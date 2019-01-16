package transactions.project.weixin;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.zip.DataFormatException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.ehcache.Cache;
import org.json.JSONException;
import org.quartz.impl.jdbcjobstore.LockException;

import com.ccms.caches.CacheConst;
import com.ccms.caches.CacheFactory;
import com.ccms.caches.web.AlreadyCommittedException;
import com.ccms.caches.web.AlreadyGzippedException;
import com.ccms.caches.web.GenericResponseWrapper;
import com.ccms.caches.web.Header;
import com.ccms.caches.web.PageInfo;
import com.ccms.caches.web.ResponseHeadersNotModifiableException;
import com.ccms.caches.web.ResponseUtil;
import com.ccms.caches.web.SerializableCookie;
import com.ccms.caches.web.filter.FilterNonReentrantException;
import com.ccms.context.InitializerServlet;

import transactions.project.weixin.common.WeixinUtil;
import dinamica.Db;
import dinamica.Jndi;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.TemplateEngine;
import dinamica.security.DinamicaUser;
import dinamica.security.RequestWrapper;

/**
 * Servlet Filter to enforce Dinamica server-side security. This module
 * checks/enforce authentication and autorization over protected resources,
 * requires special configuration via filter parameters and the existence of a
 * database schema containing the tables defined by Dinamica framework for the
 * security system. It also assumes the existence of a set of generic Actions
 * used for security tasks (login, loginerror, etc). This filter mantains API
 * level compatibity with Servlet J2EE security methods, like getUserName,
 * isUserInRole, etc.
 * 
 * <br>
 * Creation date: 10/march/2004<br>
 * Last Update: 10/march/2004<br>
 * (c) 2003 Martin Cordova<br>
 * This code is released under the LGPL license<br>
 * 
 * @author Martin Cordova - dinamica@martincordova.com
 * 
 */

public class SecurityFilterForWeixin implements Filter{

	// filter configuration object
	private FilterConfig _config = null;
	private static Logger logger = Logger
			.getLogger(SecurityFilterForWeixin.class.getName());
	// filter parameters
	String _dataSource = null;
	String _ssl = null;
	String _loginForm = null;
	DataSource _ds = null;
	String _appAlias = null;
	String _passwordPolicy = null;
	boolean _debug = false;
	CacheFactory factory = null;
	boolean formcache = false;
    private final VisitLog visitLog = new VisitLog();
    DinamicaUser user = null;

	// cache to store protected services and its authorized roles
	HashMap<String, String[]> protectedRes = new HashMap<String, String[]>();

	HashMap<String, String> excludeUrls = new HashMap<String, String>();
	HashMap<String, String> excludeFullUrls = new HashMap<String, String>();

	/**
	 * Init filter
	 **/
	public void init(FilterConfig config) throws ServletException {

		_config = config;
		String contextName = _config.getServletContext()
				.getServletContextName();

		try {

			// get filter config parameters
			_dataSource = _config.getInitParameter("datasource");
			_loginForm = _config.getInitParameter("loginform");
			_ssl = _config.getInitParameter("ssl");
			_appAlias = _config.getInitParameter("app-alias");
			String debug = _config.getInitParameter("debug");

			if (debug != null && debug.equals("true"))
				_debug = true;

			// get prefix for jndi lookups
			String _jndiPrefix = config.getServletContext().getInitParameter(
					"jndi-prefix");
			if (_jndiPrefix == null)
				_jndiPrefix = "";

			String jndiName = _jndiPrefix + _dataSource;

			// get filter datasource
			_ds = Jndi.getDataSource(jndiName);

			// add by zhangchuan init exclude urls begin
			String urls = _config.getInitParameter("exclude-urls");
			if (urls != null && urls.length() > 0) {
				String[] array_url = urls.split(";");
				for (String url : array_url) {
					excludeUrls.put(url, url);
				}
			}
			// add by zhangchuan init exclude urls end
			
			urls = _config.getInitParameter("exclude-full-urls");
			if (urls != null && urls.length() > 0) {
				String[] array_url = urls.split(";");
				for (String url : array_url) {
					excludeFullUrls.put(url, url);
				}
			}

			// init security cache (protected resources)
			//loadProtectedResources();

			// save datasource JNDI name as a context attribute
			// to be used by other modules
			_config.getServletContext().setAttribute(
					"dinamica.security.datasource", jndiName);

			_config.getServletContext().log(
					"[Dinamica] SecurityFilter started for context: "
							+ contextName);
			
			factory = CacheFactory.getInstance();
			String form_cache = _config.getInitParameter("form-cache");
			if(form_cache!=null && form_cache.equals("true")){
				formcache = true;
			}

		} catch (Throwable e) {
			_config.getServletContext().log(
					"[Dinamica] SecurityFilter FAILED for context: "
							+ contextName);
			throw new ServletException(e);
		}

	}

	/**
	 * Intercept request
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain next) throws IOException, ServletException

	{

		// flag used to indicate if the request can proceed
		boolean isOK = false;

		// get http request/response
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		// get path - is this an include?
		String uri = (String) req.getAttribute("javax.servlet.include.request_uri");
		if (uri == null)
			uri = req.getRequestURI();
		String context = req.getContextPath(); // patch 2006-01-12

		HttpSession s = req.getSession(true);

		req.setAttribute("dinamica.error.remote_addr", req.getRemoteAddr());
		// get authenticated principal
		user = (DinamicaUser) s.getAttribute("dinamica.security.login");
		
		request.setCharacterEncoding(InitializerServlet.getContext().getInitParameter("request-encoding"));
		
		String agentid = req.getParameter("agentid");
		String sid = req.getParameter("sid");
		if( null != sid && !"".equals(sid) ){
			s.setAttribute("dinamica.security.weixin_service_id", sid);
		}

		URI uri4;
		try {
			uri4 = new URI(uri);
		} catch (URISyntaxException e1) {
			throw new ServletException(e1);
		}

		String uri3 = uri4.normalize().toString();
		uri3 = uri3.substring(context.length());

		//当级级目录
		String prefix_url1 = (uri3.indexOf("?") > 0) ? uri3.substring(0,
				uri3.indexOf("?")) : uri3;
		//上推一级目录
		String prefix_url2 = prefix_url1.substring(0,
				prefix_url1.lastIndexOf("/"));
		//上推二级目录
		String prefix_url3 = prefix_url2.substring(0,
				prefix_url2.lastIndexOf("/"));
		
		boolean b_isExcludeUrl = excludeUrls.containsKey(prefix_url1);
		if (!b_isExcludeUrl) {
			b_isExcludeUrl = excludeUrls.containsKey(prefix_url2);
		}
		if (!b_isExcludeUrl) {
			b_isExcludeUrl = excludeUrls.containsKey(prefix_url3);
		}
		if( null != excludeFullUrls ){
			Iterator<String> iterUrls = excludeFullUrls.keySet().iterator();
			while( iterUrls.hasNext() ){
				String excUri = iterUrls.next();
				if( prefix_url1.startsWith(excUri) ){
					b_isExcludeUrl = true;
					break;
				}
			}
		}

		URI uri2;
		try {
			uri2 = new URI(uri);
		} catch (URISyntaxException e1) {
			throw new ServletException(e1);
		}

		uri = uri2.normalize().toString();
		uri = uri.substring(context.length());
		
		//如果微信号为空，并且是从微信回调网站，则取微信号
		String weixin_userid = (String) s.getAttribute("dinamica.security.weixin_userid");
		if(weixin_userid==null && (null != agentid || null != sid&&b_isExcludeUrl==false)) {
			// 模拟微信登录
			Connection con = null;
			try {
				con = _ds.getConnection();
				Db db = new Db(con);
				if (null != agentid || null != sid) {
					String code = req.getParameter("code");
					if (null == code) {
						throw new Throwable("微信回传code不能为空");
					}
					if (null != agentid && agentid.length() > 0) {	//企业号
						// 企业号参数
						String companyTuid = req.getParameter("state");
						if (companyTuid == null || companyTuid.length() == 0) {
							throw new Throwable("企业号不能为空");
						}
						if (agentid == null || agentid.length() == 0) {
							throw new Throwable("APP不能为空");
						}

						// 获取企业号相关信息
						String getcorpSql = StringUtil.replace(getLocalResource("/transactions/project/weixin/login/getcorp.sql"),"${tuid}", companyTuid);
						Recordset corpRs = db.get(getcorpSql);
						if (corpRs.getRecordCount() == 0) {
							throw new Throwable("企业号不存在");
						}
						corpRs.first();
						String corpId = corpRs.getString("corp_id");
						String secret = corpRs.getString("secret");
						String accessToken = corpRs.getString("access_token");
						Date tokenCreated = corpRs.getDate("token_created");
						Integer tokenExpires = 7000;
						if (null != corpRs.getString("token_expires")) {
							tokenExpires = corpRs.getInteger("token_expires");
						}
						// access_token 为空或者过期都需要重新获取
						boolean isUpdate = false;
						if (accessToken == null) {
							accessToken = WeixinUtil.getWeixinAccessTokenForCorp(corpId, secret);
							isUpdate = true;
						}
						if (tokenCreated != null && tokenExpires != null) {
							Date now = new Date();
							if ((now.getTime() - tokenCreated.getTime()) > (tokenExpires * 1000)) {// 过期
								accessToken = WeixinUtil.getWeixinAccessTokenForCorp(corpId,secret);
								isUpdate = true;
							}
						}
						if (isUpdate) {
							String updateTokenSql = StringUtil.replace(getLocalResource("/transactions/project/weixin/login/update-token.sql"),"${tuid}", companyTuid);
							updateTokenSql = StringUtil.replace(updateTokenSql,"${access_token}", accessToken);
							db.exec(updateTokenSql);
						}
						// 获取到用户的微信ID
						try {
							weixin_userid = WeixinUtil.getWeixinUserIdForCorp(accessToken, code, agentid);
						} catch (JSONException e) {
							logger.error(e.getMessage());
							res.sendRedirect(uri);
							return;
						}
					} else {	//服务号
						String queryServiceTokenSql = StringUtil.replace(getLocalResource("/transactions/project/weixin/login/query-service.sql"),"${service_id}", sid);
						Recordset getServiceInfo = db.get(queryServiceTokenSql);
						getServiceInfo.first();
						String appid = getServiceInfo.getString("appid");
						String secret = getServiceInfo.getString("secret");
						String accessToken = getServiceInfo.getString("access_token");
						Date tokenCreated = getServiceInfo.getDate("token_created");
						Integer tokenExpires = 7000;
						if (null != getServiceInfo.getString("token_expires")) {
							tokenExpires = getServiceInfo.getInteger("token_expires");
						}

						boolean isUpdate = false;
						if (accessToken == null) {
							accessToken = WeixinUtil.getAccessTokenForService(db, sid);
							isUpdate = true;
						}

						if (tokenCreated != null && tokenExpires != null) {
							Date now = new Date();
							if ((now.getTime() - tokenCreated.getTime()) > (tokenExpires * 1000)) {// 过期
								accessToken = WeixinUtil.getWeixinAccessTokenForService(appid,secret);
								isUpdate = true;
							}
						}
						if (isUpdate) {
							String updateTokenSql = StringUtil
									.replace(getLocalResource("/transactions/project/weixin/login/update-service.sql"),"${service_id}", sid);
							updateTokenSql = StringUtil.replace(updateTokenSql,"${access_token}", accessToken);
							db.exec(updateTokenSql);
						}

						try {
							weixin_userid = WeixinUtil.getWeixinUserIdForService(appid, secret,code);
							s.setAttribute("dinamica.security.weixin_userid",weixin_userid);

							if (null != agentid && agentid.length() > 0) {
								s.setAttribute("dinamica.security.weixin_type","corp");
							} else {
								s.setAttribute("dinamica.security.weixin_type","service");
							}

						} catch (JSONException e) {
							logger.error(e.getMessage());
							res.sendRedirect(uri);
							return;
						}
					}
				}
			}catch (Throwable e) {
				throw new ServletException(e);
			} finally {
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						throw new ServletException(e);
					}
				}
			}
		}		

		String weixin_type = (String) s.getAttribute("dinamica.security.weixin_type");
		if(user!=null){
			//System.out.println("user:"+user.getName());
		}
		//System.out.println("weixin_type:"+weixin_type);

		//登录后，自动绑定一次微信号
		if (user != null && weixin_type != null && weixin_type.length() > 0) {
			req.setAttribute("dinamica.error.user", user.getName());
			// 判断是否需要绑定微信帐号
			weixin_userid = (String) s.getAttribute("dinamica.security.weixin_userid");
			if (weixin_userid != null && weixin_userid.length() > 0) {
				Connection con = null;
				try {
					con = _ds.getConnection();
					Db db = new Db(con);
					String updateUseridSql = getLocalResource("/transactions/project/weixin/login/update-weixin.sql");
					String deleteWeixinSql = getLocalResource("/transactions/project/weixin/login/delete-weixin.sql");
					String insertWeixinSql = getLocalResource("/transactions/project/weixin/login/insert-weixin.sql");

					updateUseridSql = StringUtil.replace(updateUseridSql,"${weixin_userid}", weixin_userid);
					deleteWeixinSql = StringUtil.replace(deleteWeixinSql,"${weixin_userid}", weixin_userid);
					insertWeixinSql = StringUtil.replace(insertWeixinSql,"${weixin_userid}", weixin_userid);

					updateUseridSql = StringUtil.replace(updateUseridSql,"${userlogin}", user.getName());
					deleteWeixinSql = StringUtil.replace(deleteWeixinSql,"${userlogin}", user.getName());
					insertWeixinSql = StringUtil.replace(insertWeixinSql,"${userlogin}", user.getName());

					String _sid = "";
					if(null != s.getAttribute("dinamica.security.weixin_service_id")){
						_sid = (String) s.getAttribute("dinamica.security.weixin_service_id");
					}
					_sid = (StringUtils.isNotBlank(_sid) ? _sid : "null");
					insertWeixinSql = StringUtil.replace(insertWeixinSql,"${weixin_service_id}", _sid);
					updateUseridSql = StringUtil.replace(updateUseridSql,"${weixin_service_id}", _sid);
					
					TemplateEngine t = new TemplateEngine(_config.getServletContext(), req, "");
					t.setTemplate(insertWeixinSql);
					insertWeixinSql = t.getSql(null);
					
					db.exec(updateUseridSql);
					db.exec(deleteWeixinSql);
					db.exec(insertWeixinSql);
					s.removeAttribute("dinamica.security.weixin_type");
				} catch (Throwable e) {
					throw new ServletException(e);
				} finally {
					if (con != null) {
						try {
							con.close();
						} catch (SQLException e) {
							throw new ServletException(e);
						}
					}
				}
			}
		} 
		

		// 判断是否已经绑定现有帐号，如果绑定则模拟自动登录，如果没有绑定则跳转到登录页面
		if(user==null && weixin_userid!=null &&!weixin_userid.equals("")){
			Connection con = null;
			try {
				con = _ds.getConnection();
				Db db = new Db(con);
				if (!b_isExcludeUrl) {
					String loginSql = null;
					loginSql = StringUtil.replace(getLocalResource("/transactions/project/weixin/login/login_weixin.sql"),"${weixin_userid}", weixin_userid);
					TemplateEngine t = new TemplateEngine(_config.getServletContext(), req, "");
					t.setTemplate(loginSql);
					loginSql = t.getSql(null);
					Recordset loginRs = db.get(loginSql);
					
					if (loginRs.getRecordCount() == 0) {
						// 将跳转地址进行Base64加密
						//String loginPage = context + _loginForm + (uri.indexOf("/action/ccms/home")>=0 || uri.toString().equalsIgnoreCase("/action/ccms/redirect")?"":("#"+ WeixinUtil.base64Encode(uri)));
						String loginPage = context + _loginForm + "#"+ WeixinUtil.base64Encode(uri);

						/*String message = "请在消息框内输入 0#手机号#密码#姓名 进行会员微信绑定!";
						String to_user = (String) s.getAttribute("dinamica.security.weixin_userid");
						if(to_user!=null){
							send_msg(sid,to_user,message);
						}*/
						res.sendRedirect(loginPage);
						return;
					} else {
						loginRs.first();
						int enabled = loginRs.getInt("enabled");
						if (enabled == 0) {
							throw new Throwable("账号已停用");
						}
						String sqlLog = getLocalResource("/transactions/project/weixin/login/insert-loginlog.sql");
						t.setTemplate(sqlLog);
						sqlLog = t.getSql(loginRs);
						db.exec(sqlLog);

						String sqlRoles = getLocalResource("/transactions/project/weixin/login/roles.sql");
						t.setTemplate(sqlRoles);
						sqlRoles = t.getSql(loginRs);
						sqlRoles = StringUtil.replace(sqlRoles,"${application}", _appAlias);

						Recordset rs2 = db.get(sqlRoles);

						String roles[] = new String[rs2.getRecordCount()];
						int i = 0;
						while (rs2.next()) {
							roles[i] = rs2.getString("roleid");
							i++;
						}

						// create user object
						user = new DinamicaUser(loginRs.getString("userlogin"), roles);

						// store user object into session attribute
						s.setAttribute("dinamica.security.login", user);

						// set user locale
						java.util.Locale locale = new java.util.Locale(loginRs.getString("locale"));
						s.setAttribute("dinamica.user.locale", locale);
						s.setAttribute("dinamica.user.org", loginRs.getInteger("org_id"));
							if (loginRs.containsField("tenantry_id")) {
								s.setAttribute("dinamica.user.tenantry",loginRs.getString("tenantry_id"));
								s.setAttribute("dinamica.user.subject",	loginRs.getString("subject_id"));
							}
						}
					}
				}catch (Throwable e) {
					throw new ServletException(e);
				} finally {
					if (con != null) {
						try {
							con.close();
						} catch (SQLException e) {
							throw new ServletException(e);
						}
					}
				}
		}

			
		// create request wrapper
		RequestWrapper rw = new RequestWrapper(req);
		rw.setUserPrincipal(user);

		// set default attributes related to security settings
		rw.setAttribute("dinamica.security.application", _appAlias);
		rw.setAttribute("dinamica.security.passpolicy", _passwordPolicy);
		
		if (_debug) {
			debug(uri, "Intercepting request...");
			debug(uri, "Session Cookie: " + s.getId());
		}

		// requires SSL?
		if (_ssl.equals("true") && !req.isSecure()) {
			// PATCH 2006-11-08 - log not authenticated access
			if (_debug)
				debug(uri, "Request rejected! - SSL required.");

			// not an ssl request - reject it
			res.sendError(401, "Requires secure (HTTPS) access.");
			return;
		}

		try {
			// add by zhangchuan if user is null then back to login begin
			if (user == null) {
				if (!b_isExcludeUrl	/*&& ((null != agentid || null != sid))*/) {
					if (_debug)
						debug(uri,
								"Request not authenticated! - redirecting to login page.");

					// redirect to login page
					String loginPage = null;
					String requestType = req.getHeader("X-Requested-With"); 
					if(requestType != null && requestType.equals("XMLHttpRequest")){
						loginPage = context+"/action/security/loginDialog";
					}else{
						//loginPage = context + _loginForm + (uri.indexOf("/action/ccms/home")>=0 || uri.toString().equalsIgnoreCase("/action/ccms/redirect")?"":("#"+ WeixinUtil.base64Encode(uri)));
						loginPage = context + _loginForm + "#"+ WeixinUtil.base64Encode(uri);
					}

					/*String message = "请在消息框内输入 0#手机号#密码#姓名 进行会员微信绑定!";
					String to_user = (String) s.getAttribute("dinamica.security.weixin_userid");
					if(to_user!=null){
						send_msg(sid,to_user,message);
					}*/
					res.sendRedirect(loginPage);

					return;
				}
			}
			// add by zhangchuan if user is null then back to login end
			// is protected?
			if (protectedRes.containsKey(uri) && !b_isExcludeUrl) {
				// get authorized roles
				String roles_p[] = protectedRes.get(uri);
				
				// authenticated? can't access protected
				// resource without proper authentication
				if (user == null) {
					// PATCH 2006-11-08 - log not authenticated access
					if (_debug)
						debug(uri,
								"Request not authenticated! - redirecting to login page.");
					
					String loginPage = null;
					String requestType = req.getHeader("X-Requested-With"); 
					if(requestType != null && requestType.equals("XMLHttpRequest")){
						loginPage = context+"/action/security/loginDialog";
					}else{
						//loginPage = context + _loginForm + (uri.indexOf("/action/ccms/home")>=0 || uri.toString().equalsIgnoreCase("/action/ccms/redirect")?"":("#"+ WeixinUtil.base64Encode(uri)));
						loginPage = context + _loginForm + "#"+ WeixinUtil.base64Encode(uri);
					}
					/*String message = "请在消息框内输入 0#手机号#密码#姓名 进行会员微信绑定!";
					String to_user = (String) s.getAttribute("dinamica.security.weixin_userid");
					if(to_user!=null){
						send_msg(sid,to_user,message);
					}*/
					res.sendRedirect(loginPage);

					return; // patch 2006-02-17 - failed with Tomcat
				} else {

					// 2005-06-09 - debug to stderr
					if (_debug) {
						debug(uri, roles_p, user);
					}

					// authorized?
					for (int j = 0; j < roles_p.length; j++) {
						// check user roles
						if (rw.isUserInRole(roles_p[j])) {
							// OK - authorized
							isOK = true;
							break;
						}
					}
				}

			} else {

				// 2006-02-16 - debug to stderr
				if (_debug) {
					debug(uri, user);
				}

				// not a protected resource - let it pass
				isOK = true;
			}
			// 微信访问且不在首页显示则需跳转到首页显示
			
			if (uri.indexOf("/action/ccms/home")>=0 && !b_isExcludeUrl && (null != agentid || null != sid)) {
				res.sendRedirect(context + "/action/ccms/home#" + WeixinUtil.base64Encode(uri));
				return;
			}
			

			if (isOK){
				String strCacheKey = CacheConst.RESPONSE_CACHE_PREFIX + (user==null?"":user.getName()) +  calculateKey(req);
				if (req.getMethod().equals("GET") &&  strCacheKey.indexOf("_=")<0 && formcache){
					//增加缓存判断
					//查看缓存中是否存在
					PageInfo page = factory.getValue(strCacheKey);
					if(page == null){	//无缓存
				        PageInfo pageInfo = buildPageInfo(req, res, next);
				        if (pageInfo.isOk()) {
				        	/*if (response.isCommitted()) {
				                throw new AlreadyCommittedException(
				                        "Response already committed after doing buildPage"
				                                + " but before writing response from PageInfo.");
				            }*/
							//next.doFilter(rw, response);
				        }
					}else{
			            writeResponse(req, res, page);
					}
				}else{
					
					next.doFilter(rw, response);
				}
			}else{
				res.sendError(403, "NOT AUTHORIZED");
			}
		} catch (Throwable e) {
			throw new ServletException(e);
		}
	}


	/**
	 * Load into cache the list of protected resources and authorized roles for
	 * each resource
	 * 
	 * @throws Throwable
	 */
	void loadProtectedResources() throws Throwable {

		TemplateEngine t = new TemplateEngine(_config.getServletContext(),
				null, "");
		;
		Connection con = null;
		try {

			String sql = "";
			String clearSessions = _config.getInitParameter("clear-sessions");

			con = _ds.getConnection();
			Db db = new Db(con);

			// limpiar tabla s_session (sesiones activas) por si acaso se
			// quedaron registros huerfanos
			if (clearSessions != null && clearSessions.equalsIgnoreCase("true")) {
				sql = "delete from ${schema}s_session where context_alias = '"
						+ _appAlias + "'";
				t.setTemplate(sql);
				sql = t.getSql(null);
				db.exec(sql);
			}

			// get default password expiration policy for this webapp
			String sqlpass = "select pwd_policy from ${schema}s_application where app_alias = '"
					+ _appAlias + "'";
			t.setTemplate(sqlpass);
			sqlpass = t.getSql(null);

			Recordset rspass = db.get(sqlpass);

			if (rspass.getRecordCount() == 0)
				throw new Throwable(
						"Security configuration not found for this application alias: "
								+ _appAlias
								+ " - please check your web.xml configuration regarding the security filter.");

			rspass.next();
			_passwordPolicy = rspass.getString("pwd_policy");

			// get list of protected resource for this app-alias
			sql = "select service_id, path"
					+ " from ${schema}s_service s, ${schema}s_application a"
					+ " where s.app_id = a.app_id" + " and a.app_alias = '"
					+ _appAlias + "'";

			t.setTemplate(sql);
			sql = t.getSql(null);

			Recordset rs = db.get(sql);

			// get authorized roles for each resource
			String query = "select r.role_id from"
					+ " ${schema}s_service_role sr, ${schema}s_role r"
					+ " where sr.role_id = r.role_id" + " and sr.service_id = ";

			t.setTemplate(query);
			query = t.getSql(null);

			while (rs.next()) {

				sql = query + rs.getString("service_id");

				Recordset rs2 = db.get(sql);

				// store rolenames into array
				String roles[] = new String[rs2.getRecordCount()];
				int i = 0;
				while (rs2.next()) {
					roles[i] = rs2.getString("role_id");
					i++;
				}

				// save resource + roles array into hashmap
				protectedRes.put(rs.getString("path"), roles);

			}
		} catch (Throwable error) {
			throw error;
		} finally {
			if (con != null)
				con.close();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {

		_config = null;

	}

	/**
	 * Print debug information to stderr about resource access authorization
	 * 
	 * @param uri
	 *            Action being checked for authorization
	 * @param uriRoles
	 *            Autorized roles
	 * @param user
	 *            Current authenticated user
	 */
	void debug(String uri, String[] uriRoles, DinamicaUser user) {
		try {
			StringBuilder buf = new StringBuilder();
			buf.append("[Dinamica_DEBUG_SecurityFilter] ");
			buf.append("URI (" + uri + ") ");
			buf.append("Authorized Roles (");
			for (int i = 0; i < uriRoles.length; i++) {
				buf.append(uriRoles[i] + ";");
			}
			buf.append(") ");
			buf.append("USER (" + user.getName() + ") ");
			buf.append("User Roles (");
			String userRoles[] = user.getRoles();
			for (int i = 0; i < userRoles.length; i++) {
				buf.append(userRoles[i] + ";");
			}
			buf.append(") ");

			_config.getServletContext().log(buf.toString());

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Print debug information to stderr about access to public (non-protected)
	 * resources
	 * 
	 * @param uri
	 *            Action being checked for authorization
	 * @param user
	 *            Current authenticated user
	 */
	void debug(String uri, DinamicaUser user) {
		try {
			StringBuilder buf = new StringBuilder();
			buf.append("[Dinamica_DEBUG_SecurityFilter] ");
			buf.append("PUBLIC URI (" + uri + ") ");

			if (user != null) {
				buf.append("USER (" + user.getName() + ") ");
				buf.append("User Roles (");
				String userRoles[] = user.getRoles();
				for (int i = 0; i < userRoles.length; i++) {
					buf.append(userRoles[i] + ";");
				}
				buf.append(") ");
			}
			_config.getServletContext().log(buf.toString());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Print debug information to stderr about resource access
	 * 
	 * @param uri
	 *            Action being checked for authorization
	 * @param msg
	 *            Custom debug message
	 */
	void debug(String uri, String msg) {
		try {
			StringBuilder buf = new StringBuilder();
			buf.append("[Dinamica_DEBUG_SecurityFilter] ");
			buf.append("URI (" + uri + ") ");
			buf.append(msg);
			_config.getServletContext().log(buf.toString());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param path
	 * @return 获取本地文件
	 * @throws Throwable
	 */
	protected String getLocalResource(String path) {

		StringBuffer buf = new StringBuffer(5000);
		byte[] data = new byte[5000];

		InputStream in = null;

		in = this.getClass().getResourceAsStream(path);

		try {
			if (in != null) {
				while (true) {
					int len = in.read(data);
					if (len != -1) {
						buf.append(new String(data, 0, len));
					} else {
						break;
					}
				}
				return buf.toString();
			} else {
				throw new Throwable("Invalid path to resource: " + path);
			}

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
		}
		return "";
	}

	/*public void send_msg(String service_tuid, String to_user,String message) throws Throwable
	{
		Connection con = null;
		con = _ds.getConnection();
		Db db = new Db(con);
		try {
			JSONObject jsonObject = null;
			String accessToken = WeixinUtil.getAccessTokenForService(db,
					service_tuid);
			jsonObject = WeixinUtil.sendWeixinMessageTextForService(
					accessToken, to_user, message);

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}*/

    /**
     * Build page info either using the cache or building the page directly.
     * <p/>
     * Some requests are for page fragments which should never be gzipped, or
     * for other pages which are not gzipped.
     */
    protected PageInfo buildPageInfo(final HttpServletRequest request,
            final HttpServletResponse response, final FilterChain chain)
            throws Exception {
        // Look up the cached page
        final String key = CacheConst.RESPONSE_CACHE_PREFIX + (user==null?"":user.getName()) + calculateKey(request);
        PageInfo pageInfo = null;
        try {
            checkNoReentry(request);
            Object element = factory.getCache().get(key);
            if (element == null) {
                try {
                    // Page is not cached - build the response, cache it, and
                    // send to client
                    pageInfo = buildPage(request, response, chain);
                    if (pageInfo.isOk()) {
                        if (logger.isDebugEnabled()) {
                        	logger.debug("PageInfo ok. Adding to cache "
                                    + ((Category) factory.getCache()).getName() + " with key "
                                    + key);
                        }
                        factory.addCache(key, pageInfo);
                    } else {
                        if (logger.isDebugEnabled()) {
                        	logger.debug("PageInfo was not ok(200). Putting null into cache "
                                    + ((Category) factory.getCache()).getName()
                                    + " with key "
                                    + key);
                        }
                        //factory.addCache(key, null);
                    }
                } catch (final Throwable throwable) {
                    // Must unlock the cache if the above fails. Will be logged
                    // at Filter
                	//factory.addCache(key, null);
                    throw new Exception(throwable);
                }
            } else {
                pageInfo = (PageInfo) element;
            }
        } catch (LockException e) {
            // do not release the lock, because you never acquired it
            throw e;
        } finally {
            // all done building page, reset the re-entrant flag
            visitLog.clear();
        }
        return pageInfo;
    }
	
    /**
     * Pages are cached based on their key. The key for this cache is the URI followed by the query string. An example
     * is <code>/admin/SomePage.jsp?id=1234&name=Beagle</code>.
     * <p/>
     * This key technique is suitable for a wide range of uses. It is independent of hostname and port number, so will
     * work well in situations where there are multiple domains which get the same content, or where users access
     * based on different port numbers.
     * <p/>
     * A problem can occur with tracking software, where unique ids are inserted into request query strings. Because
     * each request generates a unique key, there will never be a cache hit. For these situations it is better to
     * parse the request parameters and override {@link #calculateKey(javax.servlet.http.HttpServletRequest)} with
     * an implementation that takes account of only the significant ones.
     * <p/>
     * The key should be unique.
     *
     * Implementers should differentiate between GET and HEAD requests otherwise blank pages
     * can result.
     *
     * @param httpRequest
     * @return the key, generally the URI plus request parameters
     */
    protected String calculateKey(HttpServletRequest httpRequest) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(httpRequest.getMethod()).append(httpRequest.getRequestURI()).append(httpRequest.getQueryString());
        String key = stringBuffer.toString();
        return key;
    }

    /**
     * Writes the response from a PageInfo object.
     * <p/>
     * Headers are set last so that there is an opportunity to override
     * 
     * @param request
     * @param response
     * @param pageInfo
     * @throws IOException
     * @throws DataFormatException
     * @throws ResponseHeadersNotModifiableException
     * 
     */
    protected void writeResponse(final HttpServletRequest request,
            final HttpServletResponse response, final PageInfo pageInfo)
            throws IOException, DataFormatException,
            ResponseHeadersNotModifiableException {
        boolean requestAcceptsGzipEncoding = acceptsGzipEncoding(request);

        setStatus(response, pageInfo);
        setContentType(response, pageInfo);
        setCookies(pageInfo, response);
        // do headers last so that users can override with their own header sets
        setHeaders(pageInfo, requestAcceptsGzipEncoding, response);
        writeContent(request, response, pageInfo);
    }

    /**
     * Writes the response content. This will be gzipped or non gzipped
     * depending on whether the User Agent accepts GZIP encoding.
     * <p/>
     * If the body is written gzipped a gzip header is added.
     * 
     * @param response
     * @param pageInfo
     * @throws IOException
     */
    protected void writeContent(final HttpServletRequest request,
            final HttpServletResponse response, final PageInfo pageInfo)
            throws IOException, ResponseHeadersNotModifiableException {
        byte[] body;

        boolean shouldBodyBeZero = ResponseUtil.shouldBodyBeZero(request,
                pageInfo.getStatusCode());
        if (shouldBodyBeZero) {
            body = new byte[0];
        } else if (acceptsGzipEncoding(request)) {
            body = pageInfo.getGzippedBody();
            if (ResponseUtil.shouldGzippedBodyBeZero(body, request)) {
                body = new byte[0];
            } else {
                ResponseUtil.addGzipHeader(response);
            }

        } else {
            body = pageInfo.getUngzippedBody();
        }

        response.setContentLength(body.length);
        OutputStream out = new BufferedOutputStream(response.getOutputStream());
        out.write(body);
        out.flush();
    }

    /**
     * Check that this caching filter is not being reentered by the same
     * recursively. Recursive calls will block indefinitely because the first
     * request has not yet unblocked the cache.
     * <p/>
     * This condition usually indicates an error in filter chaining or
     * RequestDispatcher dispatching.
     * 
     * @param httpRequest
     * @throws FilterNonReentrantException
     *             if reentry is detected
     */
    protected void checkNoReentry(final HttpServletRequest httpRequest)
            throws FilterNonReentrantException {
        String filterName = getClass().getName();
        if (visitLog.hasVisited()) {
            throw new FilterNonReentrantException(
                    "The request thread is attempting to reenter" + " filter "
                            + filterName + ". URL: "
                            + httpRequest.getRequestURL());
        } else {
            // mark this thread as already visited
            visitLog.markAsVisited();
            if (logger.isDebugEnabled()) {
                logger.debug("Thread {}  has been marked as visited.");
            }
        }
    }

    /**
     * Builds the PageInfo object by passing the request along the filter chain
     * 
     * @param request
     * @param response
     * @param chain
     * @return a Serializable value object for the page or page fragment
     * @throws AlreadyGzippedException
     *             if an attempt is made to double gzip the body
     * @throws Exception
     */
    protected PageInfo buildPage(final HttpServletRequest request,
            final HttpServletResponse response, final FilterChain chain)
            throws AlreadyGzippedException, Exception {

        // Invoke the next entity in the chain
        final ByteArrayOutputStream outstr = new ByteArrayOutputStream();
        final GenericResponseWrapper wrapper = new GenericResponseWrapper(
                response, outstr);

        RequestWrapper rw = new RequestWrapper(request);
		rw.setUserPrincipal(user);
        chain.doFilter(rw, wrapper);
        wrapper.flush();
        if(!response.isCommitted()){
            chain.doFilter(rw, response);
        }

        //long timeToLiveSeconds = ((PageInfo) cache.getRuntimeConfiguration()).getTimeToLiveSeconds();
        long timeToLiveSeconds = 0L;
        
        // Return the page info
        return new PageInfo(wrapper.getStatus(), wrapper.getContentType(),
                wrapper.getCookies(), outstr.toByteArray(), true,
                timeToLiveSeconds, wrapper.getAllHeaders());
    }

    /**
     * threadlocal class to check for reentry
     * 
     * @author hhuynh
     * 
     */
    private static class VisitLog extends ThreadLocal<Boolean> {
        @Override
        protected Boolean initialValue() {
            return false;
        }

        public boolean hasVisited() {
            return get();
        }

        public void markAsVisited() {
            set(true);
        }

        public void clear() {
            super.remove();
        }
    }

    /**
     * Set the content type.
     * 
     * @param response
     * @param pageInfo
     */
    protected void setContentType(final HttpServletResponse response,
            final PageInfo pageInfo) {
        String contentType = pageInfo.getContentType();
        if (contentType != null && contentType.length() > 0) {
            response.setContentType(contentType);
        }
    }

    /**
     * Set the serializableCookies
     * 
     * @param pageInfo
     * @param response
     */
    protected void setCookies(final PageInfo pageInfo,
            final HttpServletResponse response) {

        final Collection cookies = pageInfo.getSerializableCookies();
        for (Iterator iterator = cookies.iterator(); iterator.hasNext();) {
            final Cookie cookie = ((SerializableCookie) iterator.next())
                    .toCookie();
            response.addCookie(cookie);
        }
    }

    /**
     * Status code
     * 
     * @param response
     * @param pageInfo
     */
    protected void setStatus(final HttpServletResponse response,
            final PageInfo pageInfo) {
        response.setStatus(pageInfo.getStatusCode());
    }

    /**
     * Set the headers in the response object, excluding the Gzip header
     * 
     * @param pageInfo
     * @param requestAcceptsGzipEncoding
     * @param response
     */
    protected void setHeaders(final PageInfo pageInfo,
            boolean requestAcceptsGzipEncoding,
            final HttpServletResponse response) {

        final Collection<Header<? extends Serializable>> headers = pageInfo
                .getHeaders();

        // Track which headers have been set so all headers of the same name
        // after the first are added
        final TreeSet<String> setHeaders = new TreeSet<String>(
                String.CASE_INSENSITIVE_ORDER);

        for (final Header<? extends Serializable> header : headers) {
            final String name = header.getName();

            switch (header.getType()) {
            case STRING:
                if (setHeaders.contains(name)) {
                    response.addHeader(name, (String) header.getValue());
                } else {
                    setHeaders.add(name);
                    response.setHeader(name, (String) header.getValue());
                }
                break;
            case DATE:
                if (setHeaders.contains(name)) {
                    response.addDateHeader(name, (Long) header.getValue());
                } else {
                    setHeaders.add(name);
                    response.setDateHeader(name, (Long) header.getValue());
                }
                break;
            case INT:
                if (setHeaders.contains(name)) {
                    response.addIntHeader(name, (Integer) header.getValue());
                } else {
                    setHeaders.add(name);
                    response.setIntHeader(name, (Integer) header.getValue());
                }
                break;
            default:
                throw new IllegalArgumentException("No mapping for Header: "
                        + header);
            }
        }
    }

    /**
     * Determine whether the user agent accepts GZIP encoding. This feature is part of HTTP1.1.
     * If a browser accepts GZIP encoding it will advertise this by including in its HTTP header:
     * <p/>
     * <code>
     * Accept-Encoding: gzip
     * </code>
     * <p/>
     * Requests which do not accept GZIP encoding fall into the following categories:
     * <ul>
     * <li>Old browsers, notably IE 5 on Macintosh.
     * <li>Search robots such as yahoo. While there are quite a few bots, they only hit individual
     * pages once or twice a day. Note that Googlebot as of August 2004 now accepts GZIP.
     * <li>Internet Explorer through a proxy. By default HTTP1.1 is enabled but disabled when going
     * through a proxy. 90% of non gzip requests are caused by this.
     * <li>Site monitoring tools
     * </ul>
     * As of September 2004, about 34% of requests coming from the Internet did not accept GZIP encoding.
     *
     * @param request
     * @return true, if the User Agent request accepts GZIP encoding
     */
    protected boolean acceptsGzipEncoding(HttpServletRequest request) {
        return acceptsEncoding(request, "gzip");
    }

    /**
     * Checks if request accepts the named encoding.
     */
    protected boolean acceptsEncoding(final HttpServletRequest request, final String name) {
        final boolean accepts = headerContains(request, "Accept-Encoding", name);
        return accepts;
    }

    /**
     * Checks if request contains the header value.
     */
    private boolean headerContains(final HttpServletRequest request, final String header, final String value) {

        logRequestHeaders(request);

        final Enumeration accepted = request.getHeaders(header);
        while (accepted.hasMoreElements()) {
            final String headerValue = (String) accepted.nextElement();
            if (headerValue.indexOf(value) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Logs the request headers, if debug is enabled.
     *
     * @param request
     */
    protected void logRequestHeaders(final HttpServletRequest request) {
        if (logger.isDebugEnabled()) {
            Map headers = new HashMap();
            Enumeration enumeration = request.getHeaderNames();
            StringBuffer logLine = new StringBuffer();
            logLine.append("Request Headers");
            while (enumeration.hasMoreElements()) {
                String name = (String) enumeration.nextElement();
                String headerValue = request.getHeader(name);
                headers.put(name, headerValue);
                logLine.append(": ").append(name).append(" -> ").append(headerValue);
            }
            logger.debug(logLine.toString());
        }
    }

}