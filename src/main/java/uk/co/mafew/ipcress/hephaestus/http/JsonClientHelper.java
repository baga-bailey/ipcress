package uk.co.mafew.ipcress.hephaestus.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import uk.co.mafew.ipcress.apollo.format.*;

public class JsonClientHelper
{

	HttpURLConnection httpCon;
	HttpsURLConnection httpsCon;
	URL url;
	String soapRequest;

	public JsonClientHelper()
	{

	}

	// #region Main method for testing
	public static void main(String args[])
	{
	}

	// #endregion

	// #region HTTP
	public Node jsonToXml(String urlString, String jsonString, String method)
	{
		Node node = jsonToXml(jsonRequest(urlString, jsonString, method));
		return node;
	}
	
	public Node jsonToXmlAuthorised(String urlString, String jsonString, String method, String authValue)
	{
		Node node = jsonToXml(jsonRequestAuthorised(urlString, jsonString, method, authValue));
		return node;
	}
	
	public String jsonToFile(String urlString, String jsonString, String method, String file)
	{
		String retString = "ERROR";
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			retString = jsonRequest(urlString, jsonString, method);
			if (!(retString.startsWith("ERROR")))
			{
				writer.write(retString);
				writer.close();
				retString = "SUCCESS";
			}

		}
		catch (IOException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		return retString;
	}
	
	

	public String jsonRequest(String urlString, String jsonString, String method)
	{
		String retString = "ERROR";

		try
		{
			URL url = new URL(urlString);
			httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setRequestMethod(method);
			retString = sendRequest(jsonString);
		}
		catch (MalformedURLException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (ProtocolException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (IOException e)
		{
			retString = "ERROR: " + e.getMessage();
		}

		return retString;
	}

	public String jsonToFileAuthorised(String urlString, String jsonString, String method, String file, String authValue)
	{
		String retString = "ERROR";
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			retString = jsonRequestAuthorised(urlString, jsonString, method, authValue);
			if (!(retString.startsWith("ERROR")))
			{
				writer.write(retString);
				writer.close();
				retString = "SUCCESS";
			}

		}
		catch (IOException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		return retString;
	}

	public String jsonRequestAuthorised(String urlString, String jsonString, String method, String authValue)
	{
		String retString = "ERROR";

		try
		{
			URL url = new URL(urlString);
			httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setRequestMethod(method);
			httpCon.setRequestProperty("Authorization", authValue);
			retString = sendRequest(jsonString);
		}
		catch (MalformedURLException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (ProtocolException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (IOException e)
		{
			retString = "ERROR: " + e.getMessage();
		}

		return retString;
	}

	private String sendRequest(String jsonString)
	{
		String retString = "";
		try
		{
			// httpCon.setRequestProperty("Content-Type", "application/json");
			httpCon.setRequestProperty("Content-Type", "application/json");
			httpCon.setDoOutput(true);

			OutputStream os = httpCon.getOutputStream();
			os.write(jsonString.getBytes());
			os.flush();

			if (httpCon.getResponseCode() < 200 || httpCon.getResponseCode() > 299)
			{
				retString = "Error : HTTP code : " + httpCon.getResponseCode();
			}
			else
			{
				BufferedReader br = new BufferedReader(new InputStreamReader((httpCon.getInputStream())));

				String output;

				while ((output = br.readLine()) != null)
				{
					retString += output;
				}

			}

			httpCon.disconnect();

		}
		catch (Exception e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		finally
		{
			httpCon.disconnect();
		}
		return retString;
	}

	// #endregion

	// #region HTTPS
	public String jsonToFileSecure(String urlString, String jsonString, String method, String file)
	{
		String retString = "ERROR";
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			retString = jsonRequestSecure(urlString, jsonString, method);
			if (!(retString.startsWith("ERROR")))
			{
				writer.write(retString);
				writer.close();
				retString = "SUCCESS";
			}

		}
		catch (IOException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		return retString;
	}

	public String jsonRequestSecure(String urlString, String jsonString, String method)
	{
		String retString = "ERROR";

		try
		{
			URL url = new URL(urlString);
			httpsCon = (HttpsURLConnection) url.openConnection();
			httpsCon.setRequestMethod(method);
			retString = sendSecureRequest(jsonString);
		}
		catch (MalformedURLException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (ProtocolException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (IOException e)
		{
			retString = "ERROR: " + e.getMessage();
		}

		return retString;
	}

	public String jsonToFileSecureAuthorised(String urlString, String jsonString, String method, String file,
			String authValue)
	{
		String retString = "ERROR";
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			retString = jsonRequestSecureAuthorised(urlString, jsonString, method, authValue);
			if (!(retString.startsWith("ERROR")))
			{
				writer.write(retString);
				writer.close();
				retString = "SUCCESS";
			}

		}
		catch (IOException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		return retString;
	}

	public String jsonRequestSecureAuthorised(String urlString, String jsonString, String method, String authValue)
	{
		String retString = "ERROR";

		try
		{
			URL url = new URL(urlString);
			httpsCon = (HttpsURLConnection) url.openConnection();
			httpsCon.setRequestMethod(method);
			httpsCon.setRequestProperty("Authorization", authValue);
			retString = sendSecureRequest(jsonString);
		}
		catch (MalformedURLException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (ProtocolException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (IOException e)
		{
			retString = "ERROR: " + e.getMessage();
		}

		return retString;
	}

	private String sendSecureRequest(String jsonString)
	{
		String retString = "";
		try
		{
			httpsCon.setRequestProperty("Content-Type", "application/json");
			httpsCon.setDoOutput(true);

			OutputStream os = httpsCon.getOutputStream();
			os.write(jsonString.getBytes());
			os.flush();

			if (httpsCon.getResponseCode() < 200 || httpsCon.getResponseCode() > 299)
			{
				retString = "Error : HTTP code : " + httpsCon.getResponseCode();
			}
			else
			{
				BufferedReader br = new BufferedReader(new InputStreamReader((httpsCon.getInputStream())));

				String output;

				while ((output = br.readLine()) != null)
				{
					retString += output;
				}

			}

			httpsCon.disconnect();

		}
		catch (Exception e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		finally
		{
			httpsCon.disconnect();
		}
		return retString;
	}

	// #endregion

	// #region legacy
	public String downloadJsonToFileAuthorised(String urlString, String file, String headerName, String headerValue)
	{
		BufferedWriter writer = null;
		String retString = "ERROR";
		try
		{
			URL url = new URL(urlString);
			httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setRequestMethod("GET");
			httpCon.setRequestProperty("Accept", "application/json");
			// httpCon.setRequestProperty("Authorization",
			// "Basic SkJhaWxleTE6RmViQGluZm9y");
			httpCon.setRequestProperty(headerName, headerValue);

			if (httpCon.getResponseCode() < 200 || httpCon.getResponseCode() > 299)
			{
				retString = "ERROR : HTTP code : " + httpCon.getResponseCode();
			}
			else
			{
				BufferedReader br = new BufferedReader(new InputStreamReader((httpCon.getInputStream())));

				String output;
				String jsonString = "";

				while ((output = br.readLine()) != null)
				{
					jsonString += output;
				}

				httpCon.disconnect();

				writer = new BufferedWriter(new FileWriter(file));
				writer.write(jsonString);
				writer.close();

				retString = "SUCCESS : HTTP code : " + httpCon.getResponseCode();
			}

		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			retString = "ERROR - Malformed URL Exception";
		}
		catch (ProtocolException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			retString = "ERROR - Protocol Exception";
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			retString = "ERROR - IO Exception";
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

	public String downloadJsonToFile(String urlString, String file)
	{
		BufferedWriter writer = null;
		String retString = "ERROR";
		try
		{
			URL url = new URL(urlString);
			httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setRequestMethod("GET");
			httpCon.setRequestProperty("Accept", "application/json");

			if (httpCon.getResponseCode() < 200 || httpCon.getResponseCode() > 299)
			{
				retString = "ERROR : HTTP code : " + httpCon.getResponseCode();
			}
			else
			{
				BufferedReader br = new BufferedReader(new InputStreamReader((httpCon.getInputStream())));

				String output;
				String jsonString = "";

				while ((output = br.readLine()) != null)
				{
					jsonString += output;
				}

				httpCon.disconnect();

				writer = new BufferedWriter(new FileWriter(file));
				writer.write(jsonString);
				writer.close();

				retString = "SUCCESS : HTTP code : " + httpCon.getResponseCode();
			}

		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			retString = "ERROR - Malformed URL Exception";
		}
		catch (ProtocolException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			retString = "ERROR - Protocol Exception";
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			retString = "ERROR - IO Exception";
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

	// #endregion

	public Node jsonToXml(String jsonString)
	{
		JsonReader reader = Json.createReader(new StringReader(jsonString));

		JsonObject jsonObject = reader.readObject();

		reader.close();

		Document doc = null;
		try
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("jsonObject");
			doc.appendChild(rootElement);

			parseJsonObject(jsonObject, (Element) doc.getElementsByTagName("jsonObject").item(0));

		}
		catch (Exception e)
		{
			Node node = doc.createElement("error");
			node.setTextContent(e.getMessage());
			doc.getElementsByTagName("jsonObject").item(0).appendChild(node);
		}
		return doc.getElementsByTagName("jsonObject").item(0);

	}

	private void parseJsonObject(JsonObject jsonObject, Element elem)
	{
		for (Entry<String, JsonValue> entry : jsonObject.entrySet())
		{
			parseJsonValue(entry.getValue(), entry.getKey(), elem);
		}

	}

	private void parseJsonArray(JsonArray jsonArray, Element elem)
	{
		for (JsonValue jsonValue : jsonArray)
		{
			parseJsonValue(jsonValue, jsonValue.toString(), elem);
		}
	}

	private void parseJsonValue(JsonValue jsonValue, String name, Element elem)
	{
		Document document = elem.getOwnerDocument();

		if (jsonValue.getValueType() == JsonValue.ValueType.FALSE)
		{
			// Create a new Element
			Element element = document.createElement(getValidElementName(name));
			element.setTextContent("false");
			element.setAttribute("type", "boolean");

			elem.appendChild(element);
		}
		else if (jsonValue.getValueType() == JsonValue.ValueType.TRUE)
		{
			// Create a new Element
			Element element = document.createElement(getValidElementName(name));
			element.setTextContent("true");
			element.setAttribute("type", "boolean");

			elem.appendChild(element);
		}
		else if (jsonValue.getValueType() == JsonValue.ValueType.NUMBER)
		{
			// Create a new Element
			Element element = document.createElement(getValidElementName(name));
			element.setTextContent(jsonValue.toString());
			element.setAttribute("type", "number");

			elem.appendChild(element);
		}
		else if (jsonValue.getValueType() == JsonValue.ValueType.STRING)
		{
			// Create a new Element
			Element element = document.createElement(getValidElementName(name));
			element.setTextContent(jsonValue.toString());
			element.setAttribute("type", "string");

			elem.appendChild(element);
		}
		else if (jsonValue.getValueType() == JsonValue.ValueType.NULL)
		{
			// Create a new Element
			Element element = document.createElement(getValidElementName(name));
			element.setTextContent("null");
			element.setAttribute("type", "null");

			elem.appendChild(element);
		}
		else if (jsonValue.getValueType() == JsonValue.ValueType.OBJECT)
		{
			// Create a new Element
			Element element = document.createElement(getValidElementName(name));
			element.setAttribute("type", "object");

			elem.appendChild(element);
			JsonObject jo = (JsonObject) jsonValue;
			parseJsonObject(jo, element);
		}

		else if (jsonValue.getValueType() == JsonValue.ValueType.ARRAY)
		{
			// Create a new Element
			Element element = document.createElement(getValidElementName(name));
			element.setAttribute("type", "array");

			elem.appendChild(element);
			JsonArray ja = (JsonArray) jsonValue;
			parseJsonArray(ja, element);
		}
	}

	private String getValidElementName(String name)
	{
		String returnString = "";

		// Remove invalid characters
		Pattern p = Pattern.compile("[a-zA-Z0-9\\.\\-_]");
		for (int i = 0; i < name.length(); i++)
		{
			Matcher m = p.matcher(name.substring(i, i + 1));
			if (m.find())
			{
				returnString += name.substring(i, i + 1);
			}
		}
		returnString = returnString.replaceAll("(?i)xml", "");

		// Does the name start will a letter
		p = Pattern.compile("[a-zA-Z]");
		Matcher m = p.matcher(returnString);
		if (!m.find())
		{
			returnString = "X_" + returnString;
		}

		return returnString;
	}
}
