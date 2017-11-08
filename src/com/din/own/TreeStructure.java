package com.din.own;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
public class TreeStructure {

	/**
	 * dineshr93@gmail.com.
	 * 
	 * @param folder
	 *            must be a folder.
	 * @return
	 * @throws IOException 
	 */

	static String root = null,DirectoryName = null,BesidesPath = null;;
	static int rowCount = 0;
	static int columnCount =0;
	static XSSFSheet sheet = null;
	static Row row = null;
	static Cell cell = null;
	static HashSet<String> allExtensions = null;
	//hash to array
	static String[] allExtensionsArray = null;
	static String tempExtension = null;
	static int tempExtensionPositioninArray = 0;
	static String currentFilePath = null;
	static Multimap<String, String> maps = null,sortedmaps=null; 
	static String outputPath = null;
	static HashSet<String> extensions = new HashSet<String>();

	public static void performer(String directory) throws IOException {
		root=directory;
		DirectoryName = new File(directory).getName();
		BesidesPath = new File(directory).getParent();
		System.out.println(directory);
		outputPath = BesidesPath+"\\Files_Extension_Queries_Sheet_of_"+DirectoryName+".xlsx";
		pushDirectory(directory); //fetches all the extensions in allExtensionsArray
		allExtensionsArray = new String[extensions.size()];
		maps = ArrayListMultimap.create();
		int c = 0;
		for(String extension : extensions) allExtensionsArray[c++] = extension;	
		System.out.println("Total Sheet should be:"+allExtensionsArray.length);
		System.out.println(Arrays.asList(allExtensionsArray));
		pushDirectorysecondtime(root);
		//sorting
		sortedmaps=sortedByDescendingFrequency(maps);
		//sorting
		printInExcel(sortedmaps);
		JOptionPane.showMessageDialog (null, "Check output in "+ directory, "Info", JOptionPane.INFORMATION_MESSAGE);
		Desktop.getDesktop().open(new File(outputPath));
	}
	//Sorting Desc order
	/**
	 * @return a {@link Multimap} whose entries are sorted by descending frequency
	 */
	public static Multimap<String, String> sortedByDescendingFrequency(Multimap<String, String> multimap) {
	    // ImmutableMultimap.Builder preserves key/value order
	    ImmutableMultimap.Builder<String, String> result = ImmutableMultimap.builder();
	    for (Multiset.Entry<String> entry : DESCENDING_COUNT_ORDERING.sortedCopy(multimap.keys().entrySet())) {
	        result.putAll(entry.getElement(), multimap.get(entry.getElement()));
	    }
	    return result.build();
	}

	/**
	 * An {@link Ordering} that orders {@link Multiset.Entry Multiset entries} by ascending count.
	 */
	private static final Ordering<Multiset.Entry<?>> ASCENDING_COUNT_ORDERING = new Ordering<Multiset.Entry<?>>() {
	    @Override
	    public int compare(Multiset.Entry<?> left, Multiset.Entry<?> right) {
	        return Ints.compare(left.getCount(), right.getCount());
	    }
	};

	/**
	 * An {@link Ordering} that orders {@link Multiset.Entry Multiset entries} by descending count.
	 */
	private static final Ordering<Multiset.Entry<?>> DESCENDING_COUNT_ORDERING = ASCENDING_COUNT_ORDERING.reverse();
	//sorting Desc

	private static void pushDirectory(String directory) {
		if (!new File(directory).isDirectory()) {
			throw new IllegalArgumentException("folder is not a Directory");
		}
		File[] files = new File(directory).listFiles();

		for (File file : files) {
			if (file.isDirectory()) {
				pushDirectory(file.getAbsolutePath());
			} else {
				getAllFileExtensions(file);
			}
		}
	}

	private static void pushDirectorysecondtime(String directory) throws IOException {
		if (!new File(directory).isDirectory()) {
			throw new IllegalArgumentException("folder is not a Directory");
		}
		File[] files = new File(directory).listFiles();

		for (File file : files) {
			if (file.isDirectory()) {
				pushDirectorysecondtime(file.getAbsolutePath());
			} else {
				PrepareHashMap(file); //1 file at a time
			}
		}
	}

	static 	XSSFWorkbook workbook = null;
	static CellStyle stylebold = null,stylecolor=null,stylewrap=null,styleCommon=null;
	static XSSFFont font = null;
	private static void printInExcel(Multimap maps) throws IOException {
		workbook = new XSSFWorkbook();
		FileOutputStream outputStream = new FileOutputStream(outputPath);


		for(Object Extkey : maps.keySet()){ //Iterate over extension keys
			System.out.println("Extkey is:"+Extkey);
			//String currentfileextkey = (String)Extkey;
			sheet = workbook.createSheet((String)Extkey+" ("+maps.get(Extkey).size()+")");//new sheet

			//commonstyling
			font = workbook.createFont();
			font.setBold(true);
			styleCommon = workbook.createCellStyle();
			styleCommon.setFont(font);
			styleCommon.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			styleCommon.setFillPattern(CellStyle.SOLID_FOREGROUND);
			styleCommon.setWrapText(true);
			styleCommon.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			styleCommon.setBorderTop(HSSFCellStyle.BORDER_THIN);
			styleCommon.setBorderRight(HSSFCellStyle.BORDER_THIN);
			styleCommon.setBorderLeft(HSSFCellStyle.BORDER_THIN);

			stylecolor = workbook.createCellStyle(); //cell.setCellStyle(stylecolor);
			stylecolor.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
			stylecolor.setFillPattern(CellStyle.SOLID_FOREGROUND);
			stylecolor.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			stylecolor.setBorderTop(HSSFCellStyle.BORDER_THIN);
			stylecolor.setBorderRight(HSSFCellStyle.BORDER_THIN);
			stylecolor.setBorderLeft(HSSFCellStyle.BORDER_THIN);

			stylewrap = workbook.createCellStyle();
			stylewrap.setWrapText(true);
			stylewrap.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			stylewrap.setBorderTop(HSSFCellStyle.BORDER_THIN);
			stylewrap.setBorderRight(HSSFCellStyle.BORDER_THIN);
			stylewrap.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			//common styling

			rowCount=0;
			columnCount=0;
			//--------------------------------------------------------------------header row
			row = sheet.createRow(rowCount++); //new row for header

			cell = row.createCell(columnCount++);
			cell.setCellValue("Sno");
			//header formating
			cell.setCellStyle(styleCommon);
			CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
			CellUtil.setVerticalAlignment(cell, VerticalAlignment.CENTER);//both sets the font to middle
			//header formating

			cell = row.createCell(columnCount++);
			cell.setCellValue("List of Files with "+"'"+Extkey+"'"+" extension");// value heading 2nd column
			//header formating
			cell.setCellStyle(styleCommon);
			CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
			CellUtil.setVerticalAlignment(cell, VerticalAlignment.CENTER);//both sets the font to middle
			//header formating

			cell = row.createCell(columnCount++);
			cell.setCellValue("Ship Status\r\n(Part of final release)\r\n[Yes/No]\r\nIf 'no' rest of the columns are not needed");
			//header formating
			cell.setCellStyle(styleCommon);
			CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
			CellUtil.setVerticalAlignment(cell, VerticalAlignment.CENTER);//both sets the font to middle
			//header formating

			cell = row.createCell(columnCount++);
			cell.setCellValue("Is this commercially procured\r\n[Yes/No]");
			//header formating
			cell.setCellStyle(styleCommon);
			CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
			CellUtil.setVerticalAlignment(cell, VerticalAlignment.CENTER);//both sets the font to middle
			//header formating

			cell = row.createCell(columnCount++);
			cell.setCellValue("origin of the component");
			//header formating
			cell.setCellStyle(styleCommon);
			CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
			CellUtil.setVerticalAlignment(cell, VerticalAlignment.CENTER);//both sets the font to middle
			//header formating

			cell = row.createCell(columnCount++);
			cell.setCellValue("Linking method\r\n(Dynamic/\r\nStatic/\r\nSeparateWork)");
			//header formating
			cell.setCellStyle(styleCommon);
			CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
			CellUtil.setVerticalAlignment(cell, VerticalAlignment.CENTER);//both sets the font to middle
			//header formating

			cell = row.createCell(columnCount++);
			cell.setCellValue("Modification Status\r\n(Modified/\r\nAS-IS)");
			//header formating
			cell.setCellStyle(styleCommon);
			CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
			CellUtil.setVerticalAlignment(cell, VerticalAlignment.CENTER);//both sets the font to middle
			//header formating

			cell = row.createCell(columnCount++);
			cell.setCellValue("Remarks\r\n(if commercially  procured mention vendor name  &  more details if code taken from other websites liike copied or only idea taken from the site etc..)");
			//header formating
			cell.setCellStyle(styleCommon);
			CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
			CellUtil.setVerticalAlignment(cell, VerticalAlignment.CENTER);//both sets the font to middle
			//header formating
			//--------------------------------------------------------------------header row
			
			//column width
			sheet.setColumnWidth(0, 1500);
			sheet.setColumnWidth(1, 30000);
			sheet.setColumnWidth(2, 5000);
			sheet.setColumnWidth(3, 5000);
			sheet.setColumnWidth(4, 4000);
			sheet.setColumnWidth(5, 4000);
			sheet.setColumnWidth(6, 4000);
			sheet.setColumnWidth(7, 10000);
			//column width

			Collection<String> collections = maps.get(Extkey); //get value of the keys
			for(String val:collections){  //iterate over the key   row

				columnCount=0;
				row = sheet.createRow(rowCount++);//new row
				//sno
				cell = row.createCell(columnCount++);
				cell.setCellValue(rowCount-1);
				cell.setCellStyle(stylewrap);
				CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
				//sno

				//----------------------------------------------------------------------------------------------------------------------------------------------
				//===value
				cell = row.createCell(columnCount++);
				cell.setCellValue(val);
				cell.setCellStyle(stylewrap);
				//value
				//----------------------------------------------------------------------------------------------------------------------------------------------	
				cell = row.createCell(columnCount++);
				cell.setCellStyle(stylecolor);
				cell = row.createCell(columnCount++);
				cell.setCellStyle(stylecolor);
				cell = row.createCell(columnCount++);
				cell.setCellStyle(stylecolor);
				cell = row.createCell(columnCount++);
				cell.setCellStyle(stylecolor);
				cell = row.createCell(columnCount++);
				cell.setCellStyle(stylecolor);
				cell = row.createCell(columnCount++);
				cell.setCellStyle(stylecolor);
			}

			//sheet.autoSizeColumn(0);
			sheet.setAutoFilter(new CellRangeAddress(0,rowCount , 0, 7));
			row=null;
			sheet=null;
			cell=null;
		}
		workbook.write(outputStream);
		outputStream.close();
		workbook.close();

	}

	private static void PrepareHashMap(File file) {
		currentFilePath = file.getAbsolutePath().replace(root, "");
		tempExtension = getExtension(file.getAbsolutePath());
		tempExtensionPositioninArray = getExtSArrayPosition(tempExtension);
		System.out.println("Before -1:"+tempExtensionPositioninArray+"for "+tempExtension);
		if(tempExtensionPositioninArray != -1){
			System.out.println(tempExtension+" passed"+" position is "+tempExtensionPositioninArray);
			maps.put(allExtensionsArray[tempExtensionPositioninArray], currentFilePath);
		}
	}

	private static int getExtSArrayPosition(String tempExtension2) {
		for(int i = 0; i < allExtensionsArray.length; i++) {
			if(allExtensionsArray[i].equalsIgnoreCase(tempExtension2)) {
				return i;
			}
		}
		return -1;
	}

	private static void getAllFileExtensions(File file) {// 1 file at a time
		extensions.add(getExtension(file.getAbsolutePath()));

	}

	private static final String getExtension(final String filename) {
		if (filename == null) return null;
		final String afterLastSlash = filename.substring(filename.lastIndexOf('/') + 1);
		final int afterLastBackslash = afterLastSlash.lastIndexOf('\\') + 1;
		final int lastindexofdot = afterLastSlash.lastIndexOf('.')+1;
		final int dotIndex = afterLastSlash.indexOf('.', afterLastBackslash);
		return (dotIndex == -1) ? "noextension" : afterLastSlash.substring(lastindexofdot);
	}





}