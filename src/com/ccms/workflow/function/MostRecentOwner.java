package com.ccms.workflow.function;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.spi.Step;
import com.opensymphony.workflow.spi.WorkflowEntry;
import com.opensymphony.workflow.spi.WorkflowStore;

public class MostRecentOwner implements FunctionProvider {

    @SuppressWarnings("unchecked")
	public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {

    	String stepIdString = (String) args.get("next_step");
        WorkflowEntry entry = (WorkflowEntry) transientVars.get("entry");

        if (stepIdString == null) {
            throw new WorkflowException("This function expects a stepId!");
        }

        WorkflowStore store = (WorkflowStore) transientVars.get("store");
        List historySteps = store.findHistorySteps(entry.getId());

        for (Iterator iterator = historySteps.iterator(); iterator.hasNext();) {
            Step step = (Step) iterator.next();

            if (stepIdString.equals(String.valueOf(step.getStepId()))) {
                transientVars.put("owner", step.getOwner());
                break;
            }
        }
    }
}
