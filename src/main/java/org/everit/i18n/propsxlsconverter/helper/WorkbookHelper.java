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

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Helper class to read, insert, and update row in the application.
 */
public abstract class WorkbookHelper {

  protected static final int COLUMN_DEFAULT_LANG = 2;

  protected static final int COLUMN_FILE_ACCESS = 0;

  protected static final int COLUMN_PROPERTY_KEY = 1;

  protected Map<String, Integer> langColumnNumber = new HashMap<String, Integer>();

  protected String[] languages;

  protected int rowNumber;

  protected HSSFSheet sheet;

  protected HSSFWorkbook workbook;

  /**
   * Constructor.
   *
   * @param languages
   *          the languages which want to find or create to workbook.
   */
  public WorkbookHelper(final String[] languages) {
    workbook = new HSSFWorkbook();
    sheet = workbook.createSheet();
    rowNumber = 0;
    this.languages = languages;
  }

  public String[] getLanguages() {
    return languages.clone();
  }

  public int getLastRowNumber() {
    return sheet.getLastRowNum();
  }
}
