package com.ccms.report2.lucaslee.report.grouparithmetic;

import java.text.DecimalFormat;

/**
 * 求和算法。
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
public class SumArithmetic implements GroupArithmetic {
	public SumArithmetic() {
	}

	/**
	 * 参考父类文档。
	 * 
	 * @param values
	 * @return
	 */
	public String getResult(String[] values) {
		double result = 0.00;
		for (int i = 0; i < values.length; i++) {
			values[i] = values[i].replace(",", "");	//去掉千位分隔符
			if(isNumeric(values[i])){
				result += Double.parseDouble(values[i]);
			}
		}
		if(result == 0.00 && values.length > 0){
			if(!isNumeric(values[0])){
				return values[0]==null?"":values[0];
			}
		}
		DecimalFormat f = new DecimalFormat();
		f.setMaximumFractionDigits(2);
		f.setMinimumFractionDigits(0);	//百分比保留两位小数

		return f.format((Double.parseDouble(Double.toString(result))));
	}
	public boolean isNumeric(String str){
		if(str == null || str.trim().length() == 0) return false;
		if(str.matches("\\d*") || str.matches(("^\\d{1,3}(,\\d{3})*(\\.\\d+)?|\\d+(\\.\\d+)?$"))){
			return true;
		}else{
			return false;
		}
	}
}