package uk.co.mafew.ipcress.apollo.file.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import uk.co.mafew.ipcress.apollo.format.*;

public class XmlHelper
{

	public boolean saveDocToFile(Document doc, String outputDir, String filename)
	{
		try
		{
			String exportFile = outputDir + File.separator + filename;
			FileWriter targetFile = new FileWriter(exportFile);
			targetFile.write(Convert.docToString(doc));
			targetFile.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}

	public boolean transformFromAndToFile(String inputFile, String exportFile, String xsltFile)
	{

		try
		{
			File xmlDocument = Paths.get(inputFile).toFile();
			File stylesheet = Paths.get(xsltFile).toFile();
			FileWriter targetFile = new FileWriter(exportFile);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));
			// transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new StreamSource(xmlDocument), new StreamResult(writer));

			targetFile.write(writer.toString());
			targetFile.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	public boolean transformFromAndToFile(String inputFile, String exportFile, String xsltFile, String paramName, String paramValue)
	{

		try
		{
			File xmlDocument = Paths.get(inputFile).toFile();
			File stylesheet = Paths.get(xsltFile).toFile();
			FileWriter targetFile = new FileWriter(exportFile);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));
			transformer.setParameter(paramName, paramValue);
			StringWriter writer = new StringWriter();
			transformer.transform(new StreamSource(xmlDocument), new StreamResult(writer));

			targetFile.write(writer.toString());
			targetFile.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean splitSmallFile(String inputFile, String outputDir, String elementToSplitOn)
	{
		Document doc = null;

		try
		{
			String filename = Paths.get(inputFile).getFileName().toString();

			File xmlDocument = Paths.get(inputFile).toFile();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(xmlDocument);
			NodeList splitList = doc.getElementsByTagName(elementToSplitOn);
			for (int i = 0; i < splitList.getLength(); i++)
			{
				FileWriter targetFile = new FileWriter(outputDir + "//"
						+ filename.substring(0, filename.lastIndexOf(".")) + i + "."
						+ filename.substring(filename.lastIndexOf(".") + 1));
				DOMSource source = new DOMSource(splitList.item(i));
				TransformerFactory tFactory = TransformerFactory.newInstance();
				Transformer transformer = tFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				StringWriter writer = new StringWriter();
				transformer.transform(source, new StreamResult(writer));
				targetFile.write(writer.toString());
				targetFile.close();
			}

		}
		catch (Exception e)
		{
			System.out.println(e.getLocalizedMessage());
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public String echoValue(String val)
	{
		return val;
	}

}
