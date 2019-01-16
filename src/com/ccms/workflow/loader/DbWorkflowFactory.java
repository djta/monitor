package com.ccms.workflow.loader;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import com.ccms.context.InitializerServlet;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.loader.WorkflowLoader;
import com.opensymphony.workflow.loader.XMLWorkflowFactory;

import dinamica.Db;
import dinamica.Jndi;
import dinamica.Recordset;
import dinamica.StringUtil;

public class DbWorkflowFactory extends XMLWorkflowFactory implements FunctionProvider {

	private static final long serialVersionUID = -1641051431311667315L;
	protected Map<String, WfConfig> workflows;

	private String workflow = null;
	private String step = null;
	private String action = null;
	private String split = null;
	private String join = null;
	private String condition = null;
	private String preFunction = null;
	private String postFunction = null;
	private String result = null;
	private String quartzFunction = null;
	private String triggerFunction = null;
	private String queryWfm = null;
	private String queryWfmByName = null;
	private String queryWfmStep = null;
	private String queryWfmAction = null;

	public DbWorkflowFactory() {
		this.workflow = getLocalResource("/WEB-INF/action/ccms/module/workflow/template/osworkflow.xml");
		this.step = getLocalResource("/WEB-INF/action/ccms/module/workflow/template/step.xml");
		this.action = getLocalResource("/WEB-INF/action/ccms/module/workflow/template/action.xml");
		this.split = getLocalResource("/WEB-INF/action/ccms/module/workflow/template/split.xml");
		this.join = getLocalResource("/WEB-INF/action/ccms/module/workflow/template/join.xml");
		this.condition = getLocalResource("/WEB-INF/action/ccms/module/workflow/template/condition.xml");
		this.preFunction = getLocalResource("/WEB-INF/action/ccms/module/workflow/template/pre-function.xml");
		this.postFunction = getLocalResource("/WEB-INF/action/ccms/module/workflow/template/post-function.xml");
		this.result = getLocalResource("/WEB-INF/action/ccms/module/workflow/template/result.xml");
		this.quartzFunction = getLocalResource("/WEB-INF/action/ccms/module/workflow/template/quartz-function.xml");
		this.triggerFunction = getLocalResource("/WEB-INF/action/ccms/module/workflow/template/trigger-function.xml");
		this.queryWfm = getLocalResourceClass("/com/ccms/workflow/sql/loader/query-wfm.sql");
		this.queryWfmByName = getLocalResourceClass("/com/ccms/workflow/sql/loader/query-wfm-byname.sql");
		this.queryWfmStep = getLocalResourceClass("/com/ccms/workflow/sql/loader/query-wfm-node.sql");
		this.queryWfmAction = getLocalResourceClass("/com/ccms/workflow/sql/loader/query-wfm-node-to.sql");
	}

	public WorkflowDescriptor getWorkflow(String name, boolean validate) throws FactoryException {
		WfConfig c = (WfConfig) workflows.get(name);

		if (c == null || c.descriptor == null) {
			throw new RuntimeException("Unknown workflow name \"" + name + "\"");
		}
		return c.descriptor;
	}

	public String[] getWorkflowNames() {
		int i = 0;
		String[] res = new String[workflows.keySet().size()];
		Iterator<String> it = workflows.keySet().iterator();

		while (it.hasNext()) {
			res[i++] = (String) it.next();
		}
		return res;
	}

	public boolean saveWorkflow(String wfmName, WorkflowDescriptor descriptor, boolean replace) {
		Connection conn = null;
		try {
			workflows = new HashMap<String, WfConfig>();

			String jndiPrefix = null;
			String name = null;

			if (InitializerServlet.getContext() != null) {
				name = InitializerServlet.getContext().getInitParameter("def-datasource");
				jndiPrefix = InitializerServlet.getContext().getInitParameter("jndi-prefix");
			} else
				throw new Throwable("This method can't return a datasource if servlet the context is null.");

			if (jndiPrefix == null)
				jndiPrefix = "java:comp/env/";

			DataSource ds = Jndi.getDataSource(jndiPrefix + name);

			conn = ds.getConnection();
			Db db = new Db(conn);
			String query = StringUtil.replace(queryWfmByName, "${name}", wfmName);
			Recordset rsWfm = db.get(query);
    		if(rsWfm.next()){
    			String wfm_status = rsWfm.getString("wfm_status");
    			if("1".equals(wfm_status)){//启用
    				Integer wfm_id = rsWfm.getInteger("wfm_id");
        			loadWfmByName(db, wfm_id, wfmName);
    			}else{//删除流程
    				workflows.remove(wfmName);
    			}
    		}
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				conn.close();
			} catch (Exception ex) {
			}
		}
		return true;
	}

	public void loadWfmByName(Db db, Integer wfm_id, String wfm_name) throws FactoryException {
		try {

			Recordset rsStep = db.get(StringUtil.replace(queryWfmStep, "${wfm_id}", wfm_id.toString()));

			StringBuffer stepSb = new StringBuffer();
			StringBuffer splitSb = new StringBuffer();
			StringBuffer joinSb = new StringBuffer();
			StringBuffer triggerSb = new StringBuffer();
			String workflowStr = workflow;

			while (rsStep.next()) {
				String is_start_node = rsStep.getString("is_start_node");
				String step_id = rsStep.getString("step_id");
				String step_name = rsStep.getString("step_name");
				String old_status = rsStep.getString("old_status");
				String status = rsStep.getString("status");
				String step_type = rsStep.getString("step_type");

				String query = StringUtil.replace(queryWfmAction, "${step_id}", step_id);
				Recordset rsAction = db.get(query);
				StringBuffer actionSb = new StringBuffer();

				while (rsAction.next()) {
					String action_id = rsAction.getString("action_id");
					String action_name = rsAction.getString("action_name");
					String next_step = rsAction.getString("next_step");
					String next_step_type = rsAction.getString("step_type");
					String pre_class = rsAction.getString("pre_class");
					String post_class = rsAction.getString("post_class");
					String condition_class = rsAction.getString("condition_class");
					String is_auto = rsAction.getString("is_auto");
					String authority_id = rsAction.getString("authority_id");
					String quartz_class = rsAction.getString("quartz_class");
					String quartz_delay = rsAction.getString("quartz_delay");
					String quartz_cron = rsAction.getString("quartz_cron");

					if ("step".equalsIgnoreCase(step_type)) {
						String actionStr = StringUtil.replace(action, "${action_id}", action_id);
						actionStr = StringUtil.replace(actionStr, "${action_name}", action_name == null ? "" : action_name);
						actionStr = StringUtil.replace(actionStr, "${old_status}", old_status);
						actionStr = StringUtil.replace(actionStr, "${status}", status);
						actionStr = StringUtil.replace(actionStr, "${step_type}", next_step_type);
						actionStr = StringUtil.replace(actionStr, "${next_step}", next_step);
						actionStr = StringUtil.replace(actionStr, "${auto}", "1".equals(is_auto) ? "auto=\"true\"" : "");

						if (condition_class != null && condition_class.length() > 0) {
							String conditionStr = StringUtil.replace(condition, "${class}", condition_class);
							conditionStr = StringUtil.replace(conditionStr, "${step_id}", step_id);
							conditionStr = StringUtil.replace(conditionStr, "${authority}", authority_id == null ? "" : authority_id);
							actionStr = StringUtil.replace(actionStr, "${condition}", "<restrict-to>" + conditionStr + "</restrict-to>");
						} else {
							actionStr = StringUtil.replace(actionStr, "${condition}", "");
						}
						if (pre_class != null && pre_class.length() > 0) {
							String pre = StringUtil.replace(preFunction, "${class}", pre_class);
							pre = StringUtil.replace(pre, "${next_step}", next_step);
							actionStr = StringUtil.replace(actionStr, "${pre_function}", pre);
						} else {
							actionStr = StringUtil.replace(actionStr, "${pre_function}", "");
						}
						if (post_class != null && post_class.length() > 0) {
							String post = StringUtil.replace(postFunction, "${class}", post_class);
							actionStr = StringUtil.replace(actionStr, "${post_function}", post);
							actionStr = StringUtil.replace(actionStr, "${action_id}", action_id);
						} else {
							// 如果有定时任务类则放到后处理类里面
							if (quartz_class != null && quartz_class.length() > 0
									&& ((quartz_delay != null && quartz_delay.length() > 0) || (quartz_cron != null && quartz_cron.length() > 0))) {
								String quartz = StringUtil.replace(quartzFunction, "${action_id}", action_id);
								quartz = StringUtil.replace(quartz, "${quartz_delay}", quartz_delay == null ? "" : quartz_delay);
								quartz = StringUtil.replace(quartz, "${quartz_cron}", quartz_cron == null ? "" : quartz_cron);
								actionStr = StringUtil.replace(actionStr, "${post_function}", quartz);

								String trigger = StringUtil.replace(triggerFunction, "${action_id}", action_id);
								trigger = StringUtil.replace(trigger, "${quartz_class}", quartz_class);
								triggerSb.append(trigger);
							} else {
								actionStr = StringUtil.replace(actionStr, "${post_function}", "");
							}
						}
						actionSb.append(actionStr);
					} else if ("split".equalsIgnoreCase(step_type)) {
						String resultStr = StringUtil.replace(result, "${old_status}", old_status);
						resultStr = StringUtil.replace(resultStr, "${status}", status);
						resultStr = StringUtil.replace(resultStr, "${step_type}", next_step_type);
						resultStr = StringUtil.replace(resultStr, "${next_step}", next_step);
						actionSb.append(resultStr);
					} else if ("join".equalsIgnoreCase(step_type)) {
						String joinStr = StringUtil.replace(join, "${old_status}", old_status);
						joinStr = StringUtil.replace(joinStr, "${status}", status);
						joinStr = StringUtil.replace(joinStr, "${step_type}", next_step_type);
						joinStr = StringUtil.replace(joinStr, "${next_step}", next_step);
						joinStr = StringUtil.replace(joinStr, "${step_id}", step_id);
						if (condition_class != null && condition_class.length() > 0) {
							String conditionStr = StringUtil.replace(condition, "${class}", condition_class);
							conditionStr = StringUtil.replace(conditionStr, "${authority}", authority_id == null ? "" : authority_id);
							conditionStr = StringUtil.replace(conditionStr, "${step_id}", step_id);
							joinStr = StringUtil.replace(joinStr, "${condition}", conditionStr);
						} else {
							joinStr = StringUtil.replace(joinStr, "${condition}", "");
						}
						actionSb.append(joinStr);
					}
				}

				if ("0".equals(is_start_node)) {// 初始
					workflowStr = StringUtil.replace(workflowStr, "${init_action}", actionSb.toString());
				} else {
					if ("step".equalsIgnoreCase(step_type)) {
						String stepStr = StringUtil.replace(step, "${action}",
								actionSb.length() > 0 ? ("<actions>" + actionSb.toString() + "</actions>") : "");
						stepStr = StringUtil.replace(stepStr, "${step_id}", step_id);
						stepStr = StringUtil.replace(stepStr, "${step_name}", step_name == null ? "" : step_name);
						stepSb.append(stepStr);
					} else if ("split".equalsIgnoreCase(step_type)) {
						String splitStr = StringUtil.replace(split, "${step_id}", step_id);
						splitStr = StringUtil.replace(splitStr, "${result}", actionSb.toString());
						splitSb.append(splitStr);
					} else if ("join".equalsIgnoreCase(step_type)) {
						joinSb.append(actionSb.toString());
					}
				}
			}
			workflowStr = StringUtil.replace(workflowStr, "${step}", stepSb.length() > 0 ? ("<steps>" + stepSb.toString() + "</steps>") : "");
			workflowStr = StringUtil.replace(workflowStr, "${split}", splitSb.length() > 0 ? ("<splits>" + splitSb.toString() + "</splits>") : "");
			workflowStr = StringUtil.replace(workflowStr, "${join}", joinSb.length() > 0 ? ("<joins>" + joinSb.toString() + "</joins>") : "");
			workflowStr = StringUtil.replace(workflowStr, "${trigger}",
					triggerSb.length() > 0 ? ("<trigger-functions>" + triggerSb.toString() + "</trigger-functions>") : "");

			WfConfig config = new WfConfig(wfm_name);
			ByteArrayInputStream is = new ByteArrayInputStream(workflowStr.getBytes());
			WorkflowDescriptor descriptor = WorkflowLoader.load(is, false);
			config.descriptor = descriptor;
			workflows.put(wfm_name, config);
		} catch (Throwable e) {
			throw new FactoryException(e.toString());
		}
	}

	public void initDone() throws FactoryException {
		Connection conn = null;
		try {
			workflows = new HashMap<String, WfConfig>();

			String jndiPrefix = null;
			String name = null;

			if (InitializerServlet.getContext() != null) {
				name = InitializerServlet.getContext().getInitParameter("def-datasource");
				jndiPrefix = InitializerServlet.getContext().getInitParameter("jndi-prefix");
			} else
				throw new Throwable("This method can't return a datasource if servlet the context is null.");

			if (jndiPrefix == null)
				jndiPrefix = "java:comp/env/";

			DataSource ds = Jndi.getDataSource(jndiPrefix + name);

			conn = ds.getConnection();
			Db db = new Db(conn);
			Recordset rsWfm = db.get(queryWfm);
    		while(rsWfm.next()){
    			String wfm_name = rsWfm.getString("wfm_name");
    			Integer wfm_id = rsWfm.getInteger("wfm_id");
    			try{
    				loadWfmByName(db, wfm_id, wfm_name);
    			}catch(Throwable e){
    				e.printStackTrace();
    			}
    		}
		} catch (Throwable e) {
			throw new FactoryException(e.toString());
		} finally {
			try {
				conn.close();
			} catch (Exception ex) {
			}
		}
	}

	protected String getLocalResource(String path) {

		StringBuffer buf = new StringBuffer(5000);
		byte[] data = new byte[5000];

		InputStream in = null;

		try {
			in = new FileInputStream(InitializerServlet.getContext().getRealPath(path));
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

	protected String getLocalResourceClass(String path) {

		StringBuffer buf = new StringBuffer(5000);
		byte[] data = new byte[5000];

		InputStream in = null;
		try {
			in = this.getClass().getResourceAsStream(path);
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

	class WfConfig {
		String wfName;
		WorkflowDescriptor descriptor;
		long lastModified;

		public WfConfig(String name) {
			wfName = name;
		}
	}

	@Override
	public void execute(Map arg0, Map arg1, PropertySet arg2) throws WorkflowException {

	}
}
