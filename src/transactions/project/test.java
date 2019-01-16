package transactions.project;

public class test {

	public static void main(String[] args) {
		/***
		 * 比如预约2场，分别是09:00-12:00   15:00-17:00
		 * 那么当天预约时间的最小时间是9点，最大时间是17点，预约时段分别是9,10,11,15,16
		 */
		int begintime = 1;	// 起始时间
		int endtime = 17;	// 
		String timeStr = ",1,3,9,10,11,15,16,";
		System.out.println(timeStr);
		
		Integer ftime = null;
		Integer ttime = null;
		for(int i = begintime; i <= endtime; i++){
			if( null == ftime && timeStr.indexOf(","+i+",") >= 0 ){
				ftime = i;
				System.out.println("from: " + ftime);
			}
			if( null != ftime && null == ttime && timeStr.indexOf(","+i+",") < 0 ){
				ttime = i;
				System.out.println("to: " + ttime);
				ftime = null;
				ttime = null;
				System.out.println("--------------------------------");
			}
		}
	}
}
