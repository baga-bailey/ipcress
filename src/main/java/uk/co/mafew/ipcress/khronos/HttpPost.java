package uk.co.mafew.ipcress.khronos;

import uk.co.mafew.ipcress.apollo.logging.*;

public class HttpPost
{
	Logger logger;
	
	public HttpPost()
	{
		logger = new Logger(this.getClass().getName());
	}

	public static void main(String args1[])
	{
	}

	public void addHeaders()
	{
		if (sUrl != null && properties != null)
		{
			for (int i = 0; i < properties.length; i++)
				;
		}
	}

	public void setProperties(String properties[])
	{
		this.properties = properties;
	}

	public void setSoapEnvelope(String soapEnvelope)
	{
		this.soapEnvelope = soapEnvelope;
	}

	public void setSUrl(String url)
	{
		sUrl = url;
	}

	String sUrl;
	String properties[];
	String soapEnvelope;
}
