package uk.co.mafew.ipcress.hephaestus.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import uk.co.mafew.ipcress.apollo.format.*;

public class CmdHelper {

	public static void main(String args[]) {
		CmdHelper ch = new CmdHelper();
		Node returnNode = ch.runCommand("ping localhost", true, 5);
		System.out.println(Convert.elementToString((Element) returnNode));

		// ch.runCommand("C:\\Users\\jbailey1\\Documents\\temp\\test.bat", false);

		// C:\Users\jbailey1\Documents\temp\test.bat
	}

	public Node runCommand(String command) {
		return runCommand(command, false);
	}

	public Node runCommand(String command, boolean waitFor) {
		return runCommand(command, waitFor, 1000);
	}

	public Node runCommand(String command, boolean waitFor, int returnLineCount) {
		BufferedReader in = null;
		BufferedReader errStream = null;
		Node cmdResult = null;
		Document doc = null;
		String outputLine = "";
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<cmdResult></cmdResult>");
			InputSource is = new InputSource(sr);

			doc = db.parse(is);

			Runtime r = Runtime.getRuntime();
			Process p = r.exec(command);
			int code = 0;

			in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			errStream = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			while ((outputLine = in.readLine()) != null) {
				outputLine = outputLine.replaceAll("&", "&amp;");
				outputLine = outputLine.replaceAll("<", "&lt;");
				outputLine = outputLine.replaceAll(">", "&gt;");
				Node node = doc.createElement("line");
				node.setTextContent(outputLine);
				doc.getElementsByTagName("cmdResult").item(0).appendChild(node);
				if(doc.getElementsByTagName("cmdResult").item(0).getChildNodes().getLength() > returnLineCount) {
					doc.getElementsByTagName("cmdResult").item(0).removeChild(doc.getElementsByTagName("cmdResult").item(0).getFirstChild());
				}
			}

			while ((outputLine = errStream.readLine()) != null) {
				outputLine = outputLine.replaceAll("&", "&amp;");
				outputLine = outputLine.replaceAll("<", "&lt;");
				outputLine = outputLine.replaceAll(">", "&gt;");
				Node node = doc.createElement("error");
				node.setTextContent(outputLine);
				doc.getElementsByTagName("cmdResult").item(0).appendChild(node);
			}
			

			if (waitFor) {
				code = p.waitFor();
				Node node = doc.createElement("returnCode");
				node.setTextContent(Integer.toString(code));
				doc.getElementsByTagName("cmdResult").item(0).appendChild(node);
			}

		} catch (Exception e) {
			outputLine = e.getMessage();
			outputLine = outputLine.replaceAll("&", "&amp;");
			outputLine = outputLine.replaceAll("<", "&lt;");
			outputLine = outputLine.replaceAll(">", "&gt;");
			Node node = doc.createElement("error");
			node.setTextContent(outputLine);
			doc.getElementsByTagName("cmdResult").item(0).appendChild(node);
		} finally {
			try {
				in.close();
				errStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		cmdResult = doc.getElementsByTagName("cmdResult").item(0);
		return cmdResult;
	}
}
