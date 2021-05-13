package uk.co.mafew.ipcress.apollo.file.office;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CsvToXslx
{

	public void csvToXLSX(String csvFile, String xlsxFile)
	{
		BufferedReader br = null;
		try
		{
			XSSFWorkbook workBook = new XSSFWorkbook();
			XSSFSheet sheet = workBook.createSheet("sheet1");
			String currentLine = null;
			int RowNum = 0;
			br = new BufferedReader(new FileReader(csvFile));
			while ((currentLine = br.readLine()) != null)
			{
				String str[] = currentLine.split(",");
				RowNum++;
				XSSFRow currentRow = sheet.createRow(RowNum);
				for (int i = 0; i < str.length; i++)
				{
					currentRow.createCell(i).setCellValue(str[i]);
				}
			}

			FileOutputStream fileOutputStream = new FileOutputStream(xlsxFile);
			workBook.write(fileOutputStream);
			fileOutputStream.close();
			System.out.println("Done");
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage() + "Exception in try");
		}
		finally
		{
			try {
				br.close();
			} catch (IOException e) {
			}
		}
	}

}
