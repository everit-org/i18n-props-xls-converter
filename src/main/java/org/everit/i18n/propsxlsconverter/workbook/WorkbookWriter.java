/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.i18n.propsxlsconverter.workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

/**
 * Helper class to help manipulate workbook with create, insert, update and to save workbook.
 */
public class WorkbookWriter extends AbstractWorkbook {

  public WorkbookWriter(final String[] languages) {
    super(languages);
  }

  /**
   * Create the first row to workbook.
   */
  public void createFirstRow() {
    HSSFRow firstRow = sheet.createRow(rowNumber++);
    HSSFCell firstCell = firstRow.createCell(COLUMN_FILE_ACCESS);
    firstCell.setCellValue("Relative file path with the working directory");
    HSSFCell secondCell = firstRow.createCell(COLUMN_PROPERTY_KEY);
    secondCell.setCellValue("Property key name");
    HSSFCell thirdCell = firstRow.createCell(COLUMN_DEFAULT_LANG);
    thirdCell.setCellValue("Default value");

    int nextColumnNumber = COLUMN_DEFAULT_LANG + 1;
    for (String lang : languages) {
      langColumnNumber.put(lang, nextColumnNumber);
      HSSFCell cell = firstRow.createCell(nextColumnNumber++);
      cell.setCellValue(lang);
    }
  }

  /**
   * Insert a new row to workbook.
   *
   * @param fileAccess
   *          the file access column value.
   * @param propKey
   *          the property key column value.
   * @param lang
   *          the lang to find the column.
   * @param propValue
   *          the propety value.
   * @return the inserted row number.
   */
  public int insertRow(final String fileAccess, final String propKey, final String lang,
      final String propValue) {
    int insertedRowNumber = rowNumber;
    rowNumber++;

    HSSFRow row = sheet.createRow(insertedRowNumber);

    HSSFCell first = row.createCell(COLUMN_FILE_ACCESS);
    first.setCellValue(fileAccess);

    HSSFCell second = row.createCell(COLUMN_PROPERTY_KEY);
    second.setCellValue(propKey);

    row.createCell(COLUMN_DEFAULT_LANG);

    int nextColumnNumber = COLUMN_DEFAULT_LANG + 1;
    for (int i = 0; i < languages.length; i++) {
      row.createCell(nextColumnNumber++);
    }

    updateRow(insertedRowNumber, lang, propValue);

    return insertedRowNumber;
  }

  /**
   * Update row.
   *
   * @param rowNumber
   *          the row number which want to update.
   * @param lang
   *          the lang to find the column.
   * @param propValue
   *          the propety value.
   */
  public void updateRow(final int rowNumber, final String lang, final String propValue) {
    HSSFRow row = sheet.getRow(rowNumber);
    if ("".equals(lang)) {
      row.getCell(COLUMN_DEFAULT_LANG).setCellValue(propValue);
    } else {
      Integer columnNumber = langColumnNumber.get(lang);
      row.getCell(columnNumber).setCellValue(propValue);
    }
  }

  /**
   * Write workbook to file.
   *
   * @param xlsFileName
   *          the name of the file.
   */
  public void writeWorkbookToFile(final String xlsFileName) {
    try {
      workbook.close();
      try (FileOutputStream out = new FileOutputStream(new File(xlsFileName))) {
        workbook.write(out);
      }
    } catch (IOException e) {
      throw new RuntimeException("Has problem when try to save XLS files.", e);
    }
  }
}
