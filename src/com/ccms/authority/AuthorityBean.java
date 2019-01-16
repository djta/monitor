package com.ccms.authority;

import java.io.Serializable;

import javax.servlet.ServletContext;

import com.ccms.context.InitializerServlet;

import dinamica.Db;
import dinamica.Recordset;
import dinamica.StringUtil;

public class AuthorityBean implements Serializable {

	private static final long serialVersionUID = -2837791177230594905L;
	private ServletContext _ctx = null;
	private Db db = null;
	private String relationStaff = null;
	
	private String queryGroupAuthSql = null;
	private String queryEntitySql = null;
	private String queryListStaff = null;
	private String queryListSkill = null;
	private String queryListTeam = null;
	private String queryListOrg = null;
	private String queryListOrgGrade = null;
	private String queryListPost = null;
	private String queryListPostType = null;
	private String queryListBenren = null;
	private String queryListBengangwei = null;
	private String queryListBenbumen = null;
	private String queryListBendanwei = null;
	private String queryListXiajibumen = null;
	private String queryListShangjibumen = null;
	private String queryListShangjidanwei = null;
	
	public String getQueryEntitySql() {
		return queryEntitySql;
	}

	public void setQueryEntitySql(String queryEntitySql) {
		this.queryEntitySql = queryEntitySql;
	}

	public String getQueryListStaff() {
		return queryListStaff;
	}

	public void setQueryListStaff(String queryListStaff) {
		this.queryListStaff = queryListStaff;
	}

	public String getQueryListSkill() {
		return queryListSkill;
	}

	public void setQueryListSkill(String queryListSkill) {
		this.queryListSkill = queryListSkill;
	}

	public String getQueryListTeam() {
		return queryListTeam;
	}

	public void setQueryListTeam(String queryListTeam) {
		this.queryListTeam = queryListTeam;
	}

	public String getQueryListOrg() {
		return queryListOrg;
	}

	public void setQueryListOrg(String queryListOrg) {
		this.queryListOrg = queryListOrg;
	}

	public String getQueryListPost() {
		return queryListPost;
	}

	public void setQueryListPost(String queryListPost) {
		this.queryListPost = queryListPost;
	}

	public String getQueryListPostType() {
		return queryListPostType;
	}

	public void setQueryListPostType(String queryListPostType) {
		this.queryListPostType = queryListPostType;
	}

	public String getQueryListBenren() {
		return queryListBenren;
	}

	public void setQueryListBenren(String queryListBenren) {
		this.queryListBenren = queryListBenren;
	}

	public String getQueryListBengangwei() {
		return queryListBengangwei;
	}

	public void setQueryListBengangwei(String queryListBengangwei) {
		this.queryListBengangwei = queryListBengangwei;
	}

	public String getQueryListBenbumen() {
		return queryListBenbumen;
	}

	public void setQueryListBenbumen(String queryListBenbumen) {
		this.queryListBenbumen = queryListBenbumen;
	}

	public String getQueryListBendanwei() {
		return queryListBendanwei;
	}

	public void setQueryListBendanwei(String queryListBendanwei) {
		this.queryListBendanwei = queryListBendanwei;
	}

	public String getQueryListXiajibumen() {
		return queryListXiajibumen;
	}

	public void setQueryListXiajibumen(String queryListXiajibumen) {
		this.queryListXiajibumen = queryListXiajibumen;
	}

	public String getQueryListShangjibumen() {
		return queryListShangjibumen;
	}

	public void setQueryListShangjibumen(String queryListShangjibumen) {
		this.queryListShangjibumen = queryListShangjibumen;
	}

	public String getQueryListShangjidanwei() {
		return queryListShangjidanwei;
	}

	public void setQueryListShangjidanwei(String queryListShangjidanwei) {
		this.queryListShangjidanwei = queryListShangjidanwei;
	}

	public String getQueryListOrgGrade() {
		return queryListOrgGrade;
	}

	public void setQueryListOrgGrade(String queryListOrgGrade) {
		this.queryListOrgGrade = queryListOrgGrade;
	}

	public String getQueryGroupAuthSql() {
		return queryGroupAuthSql;
	}

	public void setQueryGroupAuthSql(String queryGroupAuthSql) {
		this.queryGroupAuthSql = queryGroupAuthSql;
	}

	public AuthorityBean(Db db, String relationStaff) throws Throwable{
    	this._ctx = InitializerServlet.getContext();
    	this.db = db;
    	this.relationStaff = relationStaff;
    	initSql(relationStaff);
	}
	
	public void initSql(String relationStaff) throws Throwable{
		queryGroupAuthSql  = getResource("/WEB-INF/action/ccms/module/hr/authority/template/query-group-auth.sql");
		queryEntitySql  = getResource("/WEB-INF/action/ccms/module/hr/authority/template/query-entity.sql");
		queryListStaff  = getResource("/WEB-INF/action/ccms/module/hr/authority/template/query-list-staff.sql");
		queryListSkill  = getResource("/WEB-INF/action/ccms/module/hr/authority/template/query-list-skill.sql");
		queryListTeam  = getResource("/WEB-INF/action/ccms/module/hr/authority/template/query-list-team.sql");
		queryListOrg  = getResource("/WEB-INF/action/ccms/module/hr/authority/template/query-list-org.sql");
		queryListOrgGrade = getResource("/WEB-INF/action/ccms/module/hr/authority/template/query-list-org-grade.sql");
		queryListPost  = getResource("/WEB-INF/action/ccms/module/hr/authority/template/query-list-post.sql");
		queryListPostType  = getResource("/WEB-INF/action/ccms/module/hr/authority/template/query-list-post-type.sql");
		queryListBenren  = getResource("/WEB-INF/action/ccms/module/hr/authority/template/query-list-benren.sql");
		queryListBenren = StringUtil.replace(queryListBenren, "${staff}", relationStaff);
		queryListBengangwei  = getResource("/WEB-INF/action/ccms/module/hr/authority/template/query-list-bengangwei.sql");
		queryListBengangwei = StringUtil.replace(queryListBengangwei, "${staff}", relationStaff);
		queryListBenbumen  = getResource("/WEB-INF/action/ccms/module/hr/authority/template/query-list-benbumen.sql");
		queryListBenbumen = StringUtil.replace(queryListBenbumen, "${staff}", relationStaff);
		queryListBendanwei  = getResource("/WEB-INF/action/ccms/module/hr/authority/template/query-list-bendanwei.sql");
		queryListBendanwei = StringUtil.replace(queryListBendanwei, "${staff}", relationStaff);
		queryListXiajibumen  = getResource("/WEB-INF/action/ccms/module/hr/authority/template/query-list-xiajibumen.sql");
		queryListXiajibumen = StringUtil.replace(queryListXiajibumen, "${staff}", relationStaff);
		queryListShangjibumen  = getResource("/WEB-INF/action/ccms/module/hr/authority/template/query-list-shangjibumen.sql");
		queryListShangjibumen = StringUtil.replace(queryListShangjibumen, "${staff}", relationStaff);
		queryListShangjidanwei  = getResource("/WEB-INF/action/ccms/module/hr/authority/template/query-list-shangjidanwei.sql");
		queryListShangjidanwei = StringUtil.replace(queryListShangjidanwei, "${staff}", relationStaff);
		
	}
	
	/**
	 * @param group_id
	 * @param type 返回类型（0:org;1:post;2:staff;）
	 * @param isExist 判断返回总数
	 * @return
	 * @throws Throwable
	 */
	public String spliceGroupSql(Integer group_id, String type, boolean isExist) throws Throwable{
		StringBuffer sbRtn = new StringBuffer(1024);
		if("0".equalsIgnoreCase(type)){
			if(isExist == false){
				sbRtn.append("select distinct org_id as id,org_name as name,show_order from hr_org hr where hr.tenantry_id=(select tenantry_id from hr_staff where userlogin='")
				.append(this.relationStaff).append("') ");
			}else{
				sbRtn.append("select count(1) as is_pass from hr_org hr where exists(select 1 from hr_staff where org_id=hr.org_id and userlogin='")
				.append(this.relationStaff).append("') ");
			}
		}else if("1".equalsIgnoreCase(type)){
			if(isExist == false){
				sbRtn.append("select distinct tuid as id,org_post_name as name,show_order from hr_org_post hr where 1=1 "/*
						+ "exists(select 1 from hr_org g inner join hr_staff s on g.org_id=s.org_id where g.tenantry_id=s.tenantry_id and hr.org_id=g.org_id and s.userlogin='"*/)
				/*.append(this.relationStaff).append("') ")*/;
			}else{
				sbRtn.append("select count(1) as is_pass from hr_org_post hr where exists(select 1 from hr_post_staff ps where ps.org_post_id=hr.tuid and ps.userlogin='")
				.append(this.relationStaff).append("') ");
			}
		}else{
			if(isExist == false){
				sbRtn.append("select distinct userlogin as id,name,show_order from hr_staff hr where hr.tenantry_id=(select tenantry_id from hr_staff where userlogin='")
				.append(this.relationStaff).append("') ");
			}else{
				sbRtn.append("select count(1) as is_pass from hr_staff hr where hr.userlogin = '").append(this.relationStaff).append("' ");
			}
		}
		if(group_id != null){
			String groupSql = StringUtil.replace(queryGroupAuthSql, "${group_id}", group_id.toString());
			StringBuffer sbAllowAnd = new StringBuffer(256);
			StringBuffer sbAllowOr = new StringBuffer(256);
			StringBuffer sbRefuseAnd = new StringBuffer(256);
			StringBuffer sbRefuseOr = new StringBuffer(256);
			Recordset rsGroup = db.get(groupSql);
			while(rsGroup.next()){
				Integer authority_id = rsGroup.getInteger("authority_id");
				String access_type = rsGroup.getString("access_type");
				String logic_type = rsGroup.getString("logic_type");
				String authSql = spliceAuthSql(authority_id);
				if(authSql.length() > 0){
					if("0".equalsIgnoreCase(access_type)){//允许
						if("0".equalsIgnoreCase(logic_type)){//0:限定;1:包含;
							sbAllowAnd.append("(").append(authSql).append(") union ");
						}else{
							sbAllowOr.append("(").append(authSql).append(") union ");
						}
					}else{
						if("0".equalsIgnoreCase(logic_type)){//0:限定;1:包含;
							sbRefuseAnd.append("(").append(authSql).append(") union ");
						}else{
							sbRefuseOr.append("(").append(authSql).append(") union ");
						}
					}
				}
			}
			if(sbAllowAnd.length() > 0){
				sbAllowAnd.delete(sbAllowAnd.lastIndexOf(" union "), sbAllowAnd.length());
				sbRtn.append(" and exists(select 1 from (").append(sbAllowAnd.toString()).append(") ttt where ");
				if("0".equalsIgnoreCase(type)){
					sbRtn.append(" ttt.org_id=hr.org_id ");
				}else if("1".equalsIgnoreCase(type)){
					sbRtn.append(" ttt.post_id=hr.tuid ");
				}else{
					sbRtn.append(" ttt.userlogin=hr.userlogin ");
				}
				sbRtn.append(") ");
			}
			if(sbAllowOr.length() > 0){
				sbAllowOr.delete(sbAllowOr.lastIndexOf(" union "), sbAllowOr.length());
				sbRtn.append(" and exists(select 1 from (").append(sbAllowOr.toString()).append(") ttt where ");
				if("0".equalsIgnoreCase(type)){
					sbRtn.append(" ttt.org_id=hr.org_id ");
				}else if("1".equalsIgnoreCase(type)){
					sbRtn.append(" ttt.post_id=hr.tuid ");
				}else{
					sbRtn.append(" ttt.userlogin=hr.userlogin ");
				}
				sbRtn.append(") ");
			}
			if(sbRefuseAnd.length() > 0){
				sbRefuseAnd.delete(sbRefuseAnd.lastIndexOf(" union "), sbRefuseAnd.length());
				sbRtn.append(" and not exists(select 1 from (").append(sbRefuseAnd.toString()).append(") ttt where ");
				if("0".equalsIgnoreCase(type)){
					sbRtn.append(" ttt.org_id=hr.org_id ");
				}else if("1".equalsIgnoreCase(type)){
					sbRtn.append(" ttt.post_id=hr.tuid ");
				}else{
					sbRtn.append(" ttt.userlogin=hr.userlogin ");
				}
				sbRtn.append(") ");
			}
			if(sbRefuseOr.length() > 0){
				sbRefuseOr.delete(sbRefuseOr.lastIndexOf(" union "), sbRefuseOr.length());
				sbRtn.append(" and not exists(select 1 from (").append(sbRefuseOr.toString()).append(") ttt where ");
				if("0".equalsIgnoreCase(type)){
					sbRtn.append(" ttt.org_id=hr.org_id ");
				}else if("1".equalsIgnoreCase(type)){
					sbRtn.append(" ttt.post_id=hr.tuid ");
				}else{
					sbRtn.append(" ttt.userlogin=hr.userlogin ");
				}
				sbRtn.append(") ");
			}
		}
		return sbRtn.toString();
	}
	
	public String spliceAuthSql(Integer authority_id) throws Throwable{
		StringBuffer sb = new StringBuffer(256);
		Recordset rsEntity = db.get(StringUtil.replace(queryEntitySql, "${authority_id}", authority_id.toString()));
		while(rsEntity.next()){
			String entity_type = rsEntity.getString("entity_type");
			Integer entity_id = rsEntity.getInteger("entity_id");
			if("0".equalsIgnoreCase(entity_type)){
				sb.append("(").append(StringUtil.replace(this.queryListStaff, "${entity_id}", entity_id.toString())).append(")");
			}else if("1".equalsIgnoreCase(entity_type)){
				sb.append("(").append(StringUtil.replace(this.queryListPost, "${entity_id}", entity_id.toString())).append(")");
			}else if("2".equalsIgnoreCase(entity_type)){
				sb.append("(").append(StringUtil.replace(this.queryListPostType, "${entity_id}", entity_id.toString())).append(")");
			}else if("3".equalsIgnoreCase(entity_type)){
				sb.append("(").append(StringUtil.replace(this.queryListOrg, "${entity_id}", entity_id.toString())).append(")");
			}else if("4".equalsIgnoreCase(entity_type)){
				sb.append("(").append(StringUtil.replace(this.queryListOrg, "${entity_id}", entity_id.toString())).append(")");
			}else if("5".equalsIgnoreCase(entity_type)){
				sb.append("(").append(StringUtil.replace(this.queryListOrgGrade, "${entity_id}", entity_id.toString())).append(")");
			}else if("6".equalsIgnoreCase(entity_type)){
				sb.append("(").append(StringUtil.replace(this.queryListSkill, "${entity_id}", entity_id.toString())).append(")");
			}else if("7".equalsIgnoreCase(entity_type)){
				sb.append("(").append(StringUtil.replace(this.queryListTeam, "${entity_id}", entity_id.toString())).append(")");
			}else if("8".equalsIgnoreCase(entity_type)){
				sb.append("(").append(this.queryListBenren).append(")");
			}else if("9".equalsIgnoreCase(entity_type)){
				sb.append("(").append(this.queryListBengangwei).append(")");
			}else if("10".equalsIgnoreCase(entity_type)){
				sb.append("(").append(this.queryListBenbumen).append(")");
			}else if("11".equalsIgnoreCase(entity_type)){
				sb.append("(").append(this.queryListXiajibumen).append(")");
			}else if("12".equalsIgnoreCase(entity_type)){
				sb.append("(").append(this.queryListShangjibumen).append(")");
			}else if("13".equalsIgnoreCase(entity_type)){
				sb.append("(").append(this.queryListBendanwei).append(")");
			}else if("14".equalsIgnoreCase(entity_type)){
				sb.append("(").append(this.queryListShangjidanwei).append(")");
			}
			sb.append(" union ");
		}
		if(sb.length() > 0){
			sb.delete(sb.lastIndexOf(" union "), sb.length());
		}
		return sb.toString();
	}
	
	public String getResource(String fileName) throws Throwable
	{
	    String path = fileName;
	    String encoding = _ctx.getInitParameter("file-encoding");
	    if(encoding != null && encoding.trim().equals(""))
	        encoding = null;
	    if(encoding != null)
	        return StringUtil.getResource(_ctx, path, encoding);
	    else
	        return StringUtil.getResource(_ctx, path);
	}
}
