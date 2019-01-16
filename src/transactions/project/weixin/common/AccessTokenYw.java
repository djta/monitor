package transactions.project.weixin.common;

import java.sql.Timestamp;





public class AccessTokenYw {
	
	private String access_token;//凭证
	private int expires_in;//凭证有效时间
	private Timestamp addTime;//添加时间
	private String id;
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String accessToken) {
		access_token = accessToken;
	}
	public int getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(int expiresIn) {
		expires_in = expiresIn;
	}
	public Timestamp getAddTime() {
		return addTime;
	}
	public void setAddTime(Timestamp addTime) {
		this.addTime = addTime;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
