package com.ccms.workflow;

import java.io.InputStream;

public class BasicWfmAction {

	/**
	 * @param path
	 * @return 获取本地文件
	 * @throws Throwable
	 */
	protected String getLocalResource(String path) 
	{

		StringBuffer buf = new StringBuffer(5000);
		byte[] data = new byte[5000];

		InputStream in = null;
		
		in = this.getClass().getResourceAsStream(path);
        
		try
		{
			if (in!=null)
			{
				while (true)
				{
					int len = in.read(data);
					if (len!=-1)
					{
						buf.append(new String(data,0,len));
					}
					else
					{
						break;
					}
				}
				return buf.toString();
			}
			else
			{
				throw new Throwable("Invalid path to resource: " + path);
			}
            
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (in!=null)
			{
				try{
					in.close();
				} catch (Exception e){}
			}
		}
		return "";
	}
}
