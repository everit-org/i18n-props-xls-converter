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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.everit.i18n.propsxlsconverter.internal.dto.WorkbookRowDTO;

/**
 * Helper class to help manipulate workbook with read.
 */
public class WorkbookReader extends AbstractWorkbook {

  /**
   * Constructor.
   *
   * @param xlsFileName
   *          the file name of the xls to read
   */
  public WorkbookReader(final String xlsFileName) {
    super(xlsFileName);

    HSSFRow firstRow = sheet.getRow(rowNumber++);

    int columnNumber = COLUMN_DEFAULT_LANG + 1;
    HSSFCell cell = null;
    while (((cell = firstRow.getCell(columnNumber)) != null)) {
      if (!cell.getStringCellValue().trim().isEmpty()) {
        String lang = cell.getStringCellValue();
        langColumnNumber.put(lang, columnNumber);
        columnNumber++;
      }
    }

  }

  /**
   * Get next row in the sheet. Read rows between second to last row.
   *
   * @return the {@link WorkbookRowDTO}.
   */
  public WorkbookRowDTO getNextRow() {
    if (sheet == null) {
      throw new RuntimeException("Not opened workbook yet.");
    }

    HSSFRow row = sheet.getRow(rowNumber++);
    HSSFCell propertiesFileNameCell = row.getCell(COLUMN_PROPERTIES_FILE_NAME);
    String propertiesFileName = propertiesFileNameCell.getStringCellValue();

    HSSFCell propKeyCell = row.getCell(COLUMN_PROPERTY_KEY);
    HSSFCell defaultLangCell = row.getCell(COLUMN_DEFAULT_LANG);
    HashMap<String, String> langValues = new HashMap<String, String>();

    langColumnNumber.forEach((key, value) -> {
      HSSFCell langCell = row.getCell(value);
      langValues.put(key, langCell.getStringCellValue());
    });

    return new WorkbookRowDTO()
        .propertiesFile(propertiesFileName)
        .propKey(propKeyCell.getStringCellValue())
        .defaultLangValue(defaultLangCell.getStringCellValue())
        .langValues(langValues);
  }

  @Override
  protected HSSFSheet initSheet() {
    return workbook.getSheet(SHEET_NAME);
  }

  @Override
  protected HSSFWorkbook initWorkbook() {
    try (FileInputStream file = new FileInputStream(xlsFileName)) {
      return new HSSFWorkbook(file);
    } catch (IOException e) {
      throw new RuntimeException("Failed to open XLS file [" + xlsFileName + "].", e);
    }
  }

}
