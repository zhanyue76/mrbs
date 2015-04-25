package com.alex.mrbs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;

import com.alex.mrbs.entity.Meetingbook;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;
import jxl.Workbook;

public class ExcelUtil {
	private String filePath;
	
	public String getFilePath() {
		return filePath;
	}


	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}


	public ExcelUtil(String filePath) {
		this.filePath = filePath;
	}


	private List<Meetingbook> loadExcel(File excelFile) throws Exception{
		
		List<Meetingbook> list = new ArrayList<Meetingbook>();
		
		FileInputStream inputStream = new FileInputStream(excelFile);
		Workbook wb = Workbook.getWorkbook(inputStream);
		Sheet sheet0 = wb.getSheet(0);
		Integer rowsNum = sheet0.getRows();
		System.out.println(rowsNum);
		for(int r=1;r<rowsNum;r++){
			Cell[] cellArray = sheet0.getRow(r);
			Meetingbook book = new Meetingbook();
			for(int i=0;i<cellArray.length;i++){
				switch (i){
					case 0:
						String roomIdStr = cellArray[i].getContents();
						book.setBookmeeting(Integer.parseInt(roomIdStr));
						break;
					case 1:
						String stateStr = cellArray[i].getContents();
						book.setState(stateStr);
						break;
					case 2:
						String bookDateStr =cellArray[i].getContents();
						Cell c =cellArray[i];
						if(c.getType()==CellType.DATE){
							DateCell dc = (DateCell) c;
	                        Date date = dc.getDate();
	                        TimeZone zone = TimeZone.getTimeZone("GMT");
	                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	                        sdf.setTimeZone(zone);
	                        bookDateStr =  sdf.format(date);

						}
						book.setBooktime(bookDateStr);
						break;
					case 3:
						String bookstartDateStr = cellArray[i].getContents();
						Cell d =cellArray[i];
						if(d.getType()==CellType.DATE){
							DateCell dc = (DateCell)d;
	                        Date date = dc.getDate();
	                        TimeZone zone = TimeZone.getTimeZone("GMT");
	                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	                        sdf.setTimeZone(zone);
	                        bookstartDateStr =  sdf.format(date);

						}
						book.setBookstartdate(bookstartDateStr);
						break;
					case 4:
						String bookstartTimeStr = cellArray[i].getContents();
						book.setBookstarttime(bookstartTimeStr);
						break;
					case 5:
						String bookendTimeStr = cellArray[i].getContents();
						book.setBookendtime(bookendTimeStr);
						break;
					case 6:
						String titleStr = cellArray[i].getContents();
						book.setBooktitle(titleStr);
						break;
					case 7:
						String contextStr = cellArray[i].getContents();
						book.setBookdetails(contextStr);
						break;
					case 8:
						String attendUserStr = cellArray[i].getContents();
						book.setAttendmeetusers(attendUserStr);
						break;
					case 9:
						String bookerUserStr = cellArray[i].getContents();
						book.setBookername(bookerUserStr);
						break;
				}				
				
			}
			list.add(book);
			
		}
		wb.close();
		System.gc();
		for(Meetingbook book : list){
			book.myString();
		}
		
		return list;
		
	}
	
	
	
	



	
	public List<Meetingbook> loadExcel() throws Exception{
		File file = new File(this.filePath);
		return loadExcel(file);
	}
	
	@Test
	public void Test2() throws Exception{
		File file = new File("C:\\Users\\gw00083380\\Desktop\\test97.xls");
		 loadExcel(file);
	}

}
