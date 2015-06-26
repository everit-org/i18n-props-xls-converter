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

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Helper class to read, insert, and update row in the application.
 */
public abstract class AbstractWorkbook {

  protected static final int COLUMN_DEFAULT_LANG = 2;

  protected static final int COLUMN_PROPERTIES_FILE_NAME = 0;

  protected static final int COLUMN_PROPERTY_KEY = 1;

  protected static final String SHEET_NAME = "translations";

  protected final Map<String, Integer> langColumnNumber = new LinkedHashMap<>();

  protected int rowNumber = 0;

  protected final HSSFSheet sheet;

  protected final HSSFWorkbook workbook;

  protected final String xlsFileName;

  /**
   * Constructor.
   */
  public AbstractWorkbook(final String xlsFileName) {
    this.xlsFileName = xlsFileName;
    workbook = initWorkbook();
    sheet = initSheet();
  }

  public String[] getLanguages() {
    return langColumnNumber.keySet().toArray(new String[langColumnNumber.size()]);
  }

  public int getLastRowNumber() {
    return sheet.getLastRowNum();
  }

  protected abstract HSSFSheet initSheet();

  protected abstract HSSFWorkbook initWorkbook();
}
