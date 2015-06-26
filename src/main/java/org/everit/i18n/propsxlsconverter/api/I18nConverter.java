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
package org.everit.i18n.propsxlsconverter.api;

/**
 * Define the property xls converter functions.
 */
public interface I18nConverter {

  /**
   * Export language files to one XLS file.
   *
   * @param xlsFileName
   *          the name of the exported XLS file. Cannot be <code>null</code> or empty.
   * @param workingDirectory
   *          the working directory (Example: c:\\temp or /tmp). Cannot be <code>null</code> or
   *          empty. Must be directory.
   * @param fileRegularExpression
   *          the regex expression to find files which want to export to XLS file. Example:
   *          .*\.properties$ to find all properties files. Cannot be <code>null</code> or empty.
   *          Must be valid expression.
   * @param languages
   *          the languages which want to search. Cannot be <code>null</code>.
   */
  void exportToXls(final String xlsFileName, final String workingDirectory,
      final String fileRegularExpression, final String[] languages);

  /**
   * Import XLS file to langauges files.
   *
   * @param xlsFileName
   *          the name of the imported XLS file. Cannot be <code>null</code> or empty.
   * @param workingDirectory
   *          the working directory (Example: c:\\temp or /tmp). Cannot be <code>null</code> or
   *          empty. Must be directory.
   */
  void importFromXls(final String xlsFileName, final String workingDirectory);
}
