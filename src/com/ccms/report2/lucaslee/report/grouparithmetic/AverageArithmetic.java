package com.ccms.report2.lucaslee.report.grouparithmetic;

/**
 * 求平均值算法。
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
public class AverageArithmetic implements GroupArithmetic {
	public AverageArithmetic() {
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
			if(isNumeric(values[i])){
				result += Double.parseDouble(values[i]);
			}
		}
		if(result == 0.00 && values.length > 0){
			if(!isNumeric(values[0])){
				return values[0]==null?"":values[0];
			}
		}
		if(values.length==0){
			return "";
		}else{
			return Double.toString(result / values.length);
		}
	}
	public boolean isNumeric(String str){
		if(str == null || str.trim().length() == 0) return false;
		if(str.matches("\\d*") || str.matches(("^\\d{1,4}(\\.\\d+)$"))){
			return true;
		}else{
			return false;
		}
	}
}