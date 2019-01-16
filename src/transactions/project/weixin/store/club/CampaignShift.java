package transactions.project.weixin.store.club;

import dinamica.*;

public class CampaignShift extends GenericTableManager
{

	public synchronized int service(Recordset inputParams) throws Throwable
	{
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
    		GenericTransaction t = (GenericTransaction) loader.loadClass("com.ccms.InsertMaster").newInstance();
			t.init(this.getContext(), this.getRequest(), this.getResponse());
			t.setConfig(this.getConfig());
			t.setConnection(this.getConnection());
            t.service(inputParams);
		} catch (Throwable e) {
			throw e;
		}

		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
    		GenericTransaction t = (GenericTransaction) loader.loadClass("transactions.project.weixin.store.club.VipReserveValidate").newInstance();
			t.init(this.getContext(), this.getRequest(), this.getResponse());
			t.setConfig(this.getConfig());
			t.setConnection(this.getConnection());
            t.service(inputParams);
		} catch (Throwable e) {
			throw e;
		}
		
		
		return 0;
		
	}

	
}
