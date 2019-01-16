package com.ccms.dinamica.domain.utils;

import dinamica.*;

/**
 * Genera los script SQL de seguridad para que sean 
 * usados en base de datos que ya han sido pre-inicializadas
 * con otras aplicaciones<br><br>
 * Fecha de creacion: 2010-08-06<br>
 * Fecha de actualizacion: 2010-08-06<br> 
 * @author Martin Cordova y Asociados C.A
 */
public class ExportSecurityScript extends GenericTransaction 
{

	@Override
	public int service(Recordset inputParams) throws Throwable {
		super.service(inputParams);
		String resp = generateSQL(inputParams);
		getRequest().setAttribute("script", resp);
		return 0;
	}
	
	/**
	 * Genera SQL
	 * @param inputParams Recordset que contiene parametros del request
	 * @return Script SQL
	 * @throws Throwable
	 */
	String generateSQL(Recordset inputParams) throws Throwable
	{
		
		StringBuilder resp = new StringBuilder();
		
		//aplicacion
		Recordset rs1 = getDb().get(getSQL(getResource("getapp.sql"), inputParams));
		rs1.next();
		String sql1 = getSQL(getResource("insert-app.sql"), rs1);
		resp.append(sql1);
		
		//roles
		Recordset rs2 = getDb().get(getSQL(getResource("getroles.sql"), inputParams));
		while (rs2.next()){
			String sql2 = getSQL(getResource("insert-role.sql"), rs2);
			resp.append(sql2);
		}

		//menus
		Recordset rs3 = getDb().get(getSQL(getResource("getmenus.sql"), inputParams));
		while (rs3.next()){
			String sql3 = getSQL(getResource("insert-menu.sql"), rs3);
			resp.append(sql3);
		}
		
		//menu roles
		Recordset rs4 = getDb().get(getSQL(getResource("getmenurole.sql"), inputParams));
		while (rs4.next()){
			String sql4 = getSQL(getResource("insert-menurole.sql"), rs4);
			resp.append(sql4);
		}
		
		//servicios
		Recordset rs5 = getDb().get(getSQL(getResource("getservices.sql"), inputParams));
		while (rs5.next()){
			String sql5 = getSQL(getResource("insert-service.sql"), rs5);
			resp.append(sql5);
		}
		
		//menu items
		Recordset rs6 = getDb().get(getSQL(getResource("getmenuitem.sql"), inputParams));
		while (rs6.next()){
			String sql6 = getSQL(getResource("insert-menuitem.sql"), rs6);
			resp.append(sql6);
		}

		//service role
		Recordset rs7 = getDb().get(getSQL(getResource("getservicerole.sql"), inputParams));
		while (rs7.next()){
			String sql7 = getSQL(getResource("insert-servicerole.sql"), rs7);
			resp.append(sql7);
		}
		
		//panel
		Recordset rs8 = getDb().get(getSQL(getResource("getpanel.sql"), inputParams));
		while (rs8.next()){
			String sql8 = getSQL(getResource("insert-panel.sql"), rs8);
			resp.append(sql8);
		}
		
		if(!inputParams.isNull("rol_usuario")) {
			//user role
			Recordset rs9 = getDb().get(getSQL(getResource("getuserrole.sql"), inputParams));
			while (rs9.next()){
				String sql9 = getSQL(getResource("insert-userrole.sql"), rs9);
				resp.append(sql9);
			}
		}
		
		resp.append(getResource("end.txt"));
		
		return resp.toString();
		
	}
	
}
