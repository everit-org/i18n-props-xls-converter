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
package org.everit.i18n.propsxlsconverter.internal.workbook;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Helper class to help manipulate workbook with create, insert, update and to save workbook.
 */
public class WorkbookWriter extends AbstractWorkbook {

  /**
   * Constructor.
   */
  public WorkbookWriter(final String xlsFileName, final String[] languages) {

    super(xlsFileName);

    HSSFRow firstRow = sheet.createRow(rowNumber++);

    HSSFCell firstCell = firstRow.createCell(COLUMN_PROPERTIES_FILE_NAME);
    firstCell.setCellValue("Properties file (default language)");

    HSSFCell secondCell = firstRow.createCell(COLUMN_PROPERTY_KEY);
    secondCell.setCellValue("Key");

    HSSFCell thirdCell = firstRow.createCell(COLUMN_DEFAULT_LANG);
    thirdCell.setCellValue("Default value");

    int nextColumnNumber = COLUMN_DEFAULT_LANG + 1;
    for (String lang : languages) {
      langColumnNumber.put(lang, nextColumnNumber);
      HSSFCell cell = firstRow.createCell(nextColumnNumber++);
      cell.setCellValue(lang);
    }

  }

  @Override
  protected final HSSFSheet initSheet() {
    return workbook.createSheet(SHEET_NAME);
  }

  @Override
  protected final HSSFWorkbook initWorkbook() {
    return new HSSFWorkbook();
  }

  /**
   * Insert a new row to workbook.
   *
   * @param propertiesFile
   *          the properties file column value.
   * @param propKey
   *          the property key column value.
   * @param lang
   *          the lang to find the column.
   * @param propValue
   *          the propety value.
   * @return the inserted row number.
   */
  public int insertRow(final String propertiesFile, final String propKey, final String lang,
      final String propValue) {
    int insertedRowNumber = rowNumber;
    rowNumber++;

    HSSFRow row = sheet.createRow(insertedRowNumber);

    HSSFCell first = row.createCell(COLUMN_PROPERTIES_FILE_NAME);
    first.setCellValue(propertiesFile);

    HSSFCell second = row.createCell(COLUMN_PROPERTY_KEY);
    second.setCellValue(propKey);

    row.createCell(COLUMN_DEFAULT_LANG);

    int nextColumnNumber = COLUMN_DEFAULT_LANG + 1;
    for (int i = 0; i < getLanguages().length; i++) {
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
   */
  public void writeWorkbookToFile() {
    try (FileOutputStream out = new FileOutputStream(xlsFileName)) {
      workbook.close();
      workbook.write(out);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save XLS file [" + xlsFileName + "].", e);
    }
  }
}
