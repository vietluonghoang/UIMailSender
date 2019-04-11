package exceltool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import entities.Recipient;

public class WorkingWithExcel {
	private XSSFWorkbook workbook;
	private HashMap<String, String> recipientInfo;
	private ArrayList<Recipient> recipients;
	

	public WorkingWithExcel(String fileName, String sheetName) {
		dataBinding(fileName, sheetName);
	}

	private void dataBinding(String fileName, String sheetName) {
		recipientInfo = new HashMap<>();
		recipients = new ArrayList<>();
		String excelFile = System.getProperty("user.dir") + "/data/" + fileName;

		// contains all excel data in table
		JTable dataTable = getDataTable(excelFile, sheetName);
		int recipientIndex = 0;
		for (int index = 0; index < dataTable.getRowCount(); index++) {
			String firstName = getValue("First Name", index, dataTable);
			String email = getValue("Email Address", index, dataTable);
			if(!isEmailExisted(email)) {
				recipientIndex ++;
				recipients.add(new Recipient(recipientIndex,email, firstName));
				recipientInfo.put(email, firstName);
			}
		}
	}
	
	private boolean isEmailExisted(String email) {
		if(recipientInfo.get(email) == null) {
			return false;
		}
		return true;
	}

	public ArrayList<Recipient> getRecipientInfo() {
		return recipients;
	}

	public ArrayList<Recipient> getRecipientInfo(int rangeFrom, int rangeTo) {
		ArrayList<Recipient> re = new ArrayList<>();
		for (Recipient recipient : recipients) {
			if(recipient.getIndex() > rangeFrom && recipient.getIndex() <= rangeTo) {
				re.add(recipient);
			}
			if(re.size() > rangeTo) {
				break;
			}
		}
		return re;
	}
	
	public JTable getDataTable(String filePath, String sheetName) {
		try {
			workbook = new XSSFWorkbook(new FileInputStream(new File(filePath)));
		} catch (FileNotFoundException ex) {
			Logger.getLogger(WorkingWithExcel.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(WorkingWithExcel.class.getName()).log(Level.SEVERE, null, ex);
		}
		XSSFSheet sheet = workbook.getSheet(sheetName);

		XSSFRow row;
		XSSFCell cell;
		Iterator<?> rows = null;

		try {
			rows = sheet.rowIterator();
		} catch (NullPointerException e) {
			throw new NullPointerException("The sheetname '" + sheetName + "' does not exist in " + filePath);
		}

		if (sheet.getRow(0) == null) {
			return null;
		}

		String[] columnNames = new String[sheet.getRow(0).getLastCellNum()];
		for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
			columnNames[i] = sheet.getRow(0).getCell(i).getStringCellValue().trim();
		}

		Object[][] data = new Object[sheet.getLastRowNum()][sheet.getRow(0).getLastCellNum()];

		for (int r = 1; r < sheet.getLastRowNum() + 1; r++) {
			row = sheet.getRow(r);

			if (row == null) {
				for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
					data[r - 1][i] = "";
				}
			} else {
				for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
					cell = row.getCell(i);
					if (cell == null) {
						data[r - 1][i] = "";
						continue;
					}

					switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_NUMERIC:
						data[r - 1][i] = String.valueOf(BigDecimal.valueOf(cell.getNumericCellValue()).longValue())
								.trim();
						break;
					case HSSFCell.CELL_TYPE_STRING:
						data[r - 1][i] = cell.getStringCellValue().trim();
						break;
					case HSSFCell.CELL_TYPE_FORMULA:
						data[r - 1][i] = cell.getCellFormula();
						break;
					case HSSFCell.CELL_TYPE_BLANK:
						data[r - 1][i] = "";
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						data[r - 1][i] = cell.getBooleanCellValue();
						break;
					case HSSFCell.CELL_TYPE_ERROR:
						data[r - 1][i] = cell.getErrorCellValue();
						break;
					default:
						data[r - 1][i] = cell.getStringCellValue().trim();
						break;
					}
				}
			}

		}

		if (workbook != null) {
			try {
				workbook.close();
			} catch (IOException ex) {
				Logger.getLogger(WorkingWithExcel.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		DefaultTableModel model = new DefaultTableModel() {

			public String[] getColumnNames() {
				return columnNames;
			}

			public int getColumnCount() {
				return columnNames.length;
			}

			public int getRowCount() {
				return data.length;
			}

			public String getColumnName(int col) {
				return columnNames[col];
			}

			public Object getValueAt(int row, int col) {
				return data[row][col];
			}

		};

		return new JTable(model);
	}

	public String getValue(String key, int rowIndex, JTable table) {
		return (String) table.getValueAt(rowIndex, table.getColumn(key).getModelIndex());
	}
}
