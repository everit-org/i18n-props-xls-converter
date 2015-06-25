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
package org.everit.i18n.propsxlsconverter.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.everit.i18n.propsxlsconverter.dto.WorkbookRowDTO;
import org.everit.i18n.propsxlsconverter.exception.LanguageException;
import org.everit.i18n.propsxlsconverter.exception.LanguageException.LanguageErrorCode;

/**
 * Helper class to help manipulate workbook with read.
 */
public class ImportWorkbookHelper extends WorkbookHelper {

  public ImportWorkbookHelper() {
    super(null);
  }

  /**
   * Get next row in the sheet. Read rows between second to last row.
   *
   * @return the {@link WorkbookRowDTO}.
   *
   * @throws LanguageException
   *           when not opened workbook.
   */
  public WorkbookRowDTO getNextRow() {
    if (sheet == null) {
      throw new LanguageException(LanguageErrorCode.NOT_OPENED_WORKBOOK,
          "Not opened workbook yet.");
    }

    HSSFRow row = sheet.getRow(rowNumber++);
    HSSFCell fileAccesCell = row.getCell(COLUMN_FILE_ACCESS);
    String fileAccess = fileAccesCell.getStringCellValue();

    HSSFCell propKeyCell = row.getCell(COLUMN_PROPERTY_KEY);
    HSSFCell defaultLangCell = row.getCell(COLUMN_DEFAULT_LANG);
    HashMap<String, String> langValues = new HashMap<String, String>();

    langColumnNumber.forEach((key, value) -> {
      HSSFCell langCell = row.getCell(value);
      langValues.put(key, langCell.getStringCellValue());
    });

    return new WorkbookRowDTO()
        .fileAccess(fileAccess)
        .propKey(propKeyCell.getStringCellValue())
        .defaultLangValue(defaultLangCell.getStringCellValue())
        .langValues(langValues);
  }

  /**
   * Open workbook with the first sheet and read the first row to collect language informations.
   *
   * @param fileName
   *          the file name.
   */
  public void openWorkbook(final String fileName) {

    try (FileInputStream file = new FileInputStream(new File(fileName))) {
      workbook = new HSSFWorkbook(file);
      // TODO maybe name?
      sheet = workbook.getSheetAt(0);

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

      int size = langColumnNumber.size();
      languages = langColumnNumber.keySet().toArray(new String[size]);
    } catch (IOException e) {
      throw new LanguageException(LanguageErrorCode.IO_PROBLEM,
          "Has IO problem when try to open XLS file.", e);
    }
  }
}
