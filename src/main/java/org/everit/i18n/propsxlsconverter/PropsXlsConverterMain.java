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
package org.everit.i18n.propsxlsconverter;

import org.everit.i18n.propsxlsconverter.internal.I18nConverterImpl;

/**
 * The main class to manage export or import function.
 */
public final class PropsXlsConverterMain {

  /**
   * The Main class starter method.
   */
  public static void main(final String[] args) {

    PropsXlsConverterConfig config = new PropsXlsConverterConfig(args);

    String xlsFileName = config.getXlsFileName();
    String workingDirectory = config.getWorkingDirectory();

    I18nConverter i18nConverter = new I18nConverterImpl();

    if (config.isImportFunction()) {

      i18nConverter.importFromXls(xlsFileName, workingDirectory);

    } else if (config.isExportFunction()) {

      String fileRegularExpression = config.getFileRegularExpression();
      String[] languages = config.getLanguages();

      i18nConverter.exportToXls(xlsFileName, workingDirectory,
          fileRegularExpression, languages);

    } else {
      config.printHelp();
    }
  }

  private PropsXlsConverterMain() {
  }

}
