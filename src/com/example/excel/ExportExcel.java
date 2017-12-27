package com.example.excel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import com.example.domain.Observation;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ExportExcel {

	private String mDestXmlFilename;
	private SQLiteDatabase mDb;

	public ExportExcel(SQLiteDatabase db, String destXml) {
		mDb = db;
		mDestXmlFilename = destXml;
	}

	public void exportData() {
		try {
			// get the tables out of the given sqlite database
			String sql = "SELECT * FROM sqlite_master";
			Cursor cur = mDb.rawQuery(sql, new String[0]);
			cur.moveToFirst();
			String tableName;
			while (cur.getPosition() < cur.getCount()) {
				tableName = cur.getString(cur.getColumnIndex("name"));
				// don't process these two tables since they are used
				// for metadata
				if (!tableName.equals("android_metadata")
						&& !tableName.equals("sqlite_sequence")) {
					// writeExcel(tableName);
				}
				cur.moveToNext();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成一个Excel文件
	 * 
	 * @param fileName
	 *            要生成的Excel文件名
	 */

	public void exportObservation(Observation obser) {

		WritableWorkbook wwb = null;
		String fileName;
		fileName = "/sdcard/Excel/" + obser.getObservationName() + ".xls";
		int r = 0;
		String sql = "select * from obserContent where observationID = "
				+ obser.getObservationID();

		Cursor cur = mDb.rawQuery(sql, new String[0]);
		int numcols = 3;
		int numrows = cur.getCount() + 4;
		String records[][] = new String[numrows + 1][3];// 存放答案，多一行标题行

		records[0][0] = "Observation Name";
		records[0][1] = "Create Date";
		records[0][2] = "Delete Deadline";
		records[1][0] = obser.getObservationName();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		records[1][1] = sdf.format(obser.getCreateTime());
		if (obser.getDeleteTime() == null)
			records[1][2] = "No Deadline";
		else
			records[1][2] = sdf.format(obser.getDeleteTime());
		records[2][0] = "Trait List";
		records[2][1] = "User";
		records[2][2] = "Photo";
		records[3][0] = findTraitListNameById(obser.getTraitListID());
		records[3][1] = obser.getUsername();
		if (obser.getPhotoPath().equals(""))
			records[3][2] = "No Photo";
		else {
			String str = obser.getPhotoPath();
			records[3][2] = str.substring(40, str.length() - 1);
		}

		records[4][0] = "Trait Name:";
		records[4][1] = "Trait Value:";
		records[4][2] = "Trait Editable";
		int row = 5;

		while (cur.moveToNext()) {
			records[row][0] = findNameById(Integer.parseInt(cur.getString(2)));
			records[row][1] = cur.getString(3).replace(",", "");
			if (cur.getString(4).equals("1"))
				records[row][2] = "True";
			else
				records[row][2] = "False";
			row++;
		}
		cur.close();
		try {
			// 首先要使用Workbook类的工厂方法创建一个可写入的工作薄(Workbook)对象
			wwb = Workbook.createWorkbook(new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (wwb != null) {
			// 创建一个可写入的工作表
			// Workbook的createSheet方法有两个参数，第一个是工作表的名称，第二个是工作表在工作薄中的位置
			WritableSheet ws = wwb.createSheet("sheet1", 0);
			// 下面开始添加单元格
			for (int i = 0; i < numrows + 1; i++) {
				for (int j = 0; j < numcols; j++) {
					// 这里需要注意的是，在Excel中，第一个参数表示列，第二个表示行
					Label labelC = new Label(j, i, records[i][j]);
					// Log.i("Newvalue" + i + " " + j, records[i][j]);
					try {
						// 将生成的单元格添加到工作表中
						ws.addCell(labelC);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
			}
			try {
				// 从内存中写入文件中
				wwb.write();
				// 关闭资源，释放内存
				wwb.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}

		}

	}

	public String findNameById(Integer id) {
		String name;
		String sqlString = "SELECT traitName FROM Trait WHERE traitID = " + id;
		Cursor cursor = mDb.rawQuery(sqlString, null);
		cursor.moveToNext();
		name = cursor.getString(0);
		return name;
	}

	public String findTraitListNameById(Integer id) {
		String name;
		String sqlString = "SELECT traitListName FROM TraitList WHERE traitListID = "
				+ id;
		Cursor cursor = mDb.rawQuery(sqlString, null);
		cursor.moveToNext();
		name = cursor.getString(0);
		return name;
	}
}
