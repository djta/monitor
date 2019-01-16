package com.ccms.workflow.function;

import java.util.Map;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowContext;

public class SetAutoOwnerFunction implements FunctionProvider {

	@SuppressWarnings("unchecked")
	public void execute(Map transientVars, Map args, PropertySet ps) {
		WorkflowContext context = (WorkflowContext) transientVars.get("context");
		transientVars.put("owner", context.getCaller());
	}
}
