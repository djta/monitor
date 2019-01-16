package com.ccms.report2.lucaslee.report.grouparithmetic;

import java.util.Arrays;

import com.ccms.report2.lucaslee.report.ReportException;


/**
 * 取最大值。
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:Lucas-lee Soft
 * </p>
 * 
 * @author Lucas Lee
 * @version 1.0
 */
public class MaxArithmetic implements GroupArithmetic {
	public MaxArithmetic() {
	}

	/**
	 * 参考父类文档。
	 * 
	 * @param values
	 * @return
	 * @throws ReportException
	 */
	public String getResult(String[] values) throws ReportException {
		if (values == null) {
			throw new ReportException("values can not be null.");
		}
		String[] temp = (String[]) values.clone();
		Arrays.sort(temp);
		return values[temp.length - 1];
	}
}