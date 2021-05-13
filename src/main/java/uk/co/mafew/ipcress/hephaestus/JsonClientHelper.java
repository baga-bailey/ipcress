package uk.co.mafew.ipcress.hephaestus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class JsonClientHelper {
	
	HttpURLConnection httpCon;
	String soapRequest;

	public JsonClientHelper()
	{

	}

	public String downloadJsonToFile(String urlString, String file)
	{
		BufferedWriter writer = null;
		String retString="ERROR";
		try
		{
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", "Basic SkJhaWxleTE6RmViQGluZm9y");
													 
			if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299)
			{
				retString = "ERROR : HTTP code : " + conn.getResponseCode();
			}
			else
			{
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				String output;
				String jsonString = "";
				
				while ((output = br.readLine()) != null)
				{
					jsonString += output;
				}

				conn.disconnect();

				writer = new BufferedWriter(new FileWriter(file));
				writer.write(jsonString);
				writer.close();
				
				retString = "SUCCESS : HTTP code : " + conn.getResponseCode();
			}

		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			retString="ERROR - Malformed URL Exception";
		}
		catch (ProtocolException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			retString="ERROR - Protocol Exception";
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			retString="ERROR - IO Exception";
		}
		finally
		{
			try
			{
				if (writer != null)
					writer.close();
			}
			catch (IOException e)
			{
			}
		}
		
		return retString;
	}

}
