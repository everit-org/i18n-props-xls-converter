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
package org.everit.i18n.propsxlsconverter.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.everit.i18n.propsxlsconverter.dto.WorkbookRowDTO;
import org.everit.i18n.propsxlsconverter.exception.LanguageException;
import org.everit.i18n.propsxlsconverter.exception.LanguageException.LanguageErrorCode;
import org.everit.i18n.propsxlsconverter.helper.ImportWorkbookHelper;

/**
 * Import XLS files to the files where exported.
 */
public class ImportLanguageFiles {

  private String importedFileName;

  private String prevFileAccess = null;

  private String workingDirectory;

  /**
   * Constructor.
   *
   * @param importedFileName
   *          the name of the imported file. Cannot be <code>null</code> or empty.
   * @param workingDirectory
   *          the working directory (Example: c:\\temp or /tmp). Cannot be <code>null</code> or
   *          empty. Must be directory.
   */
  public ImportLanguageFiles(final String importedFileName, final String workingDirectory) {
    validateParameters(importedFileName, workingDirectory);

    this.importedFileName = importedFileName;
    this.workingDirectory = workingDirectory;
  }

  private String calculateLangFileName(final String lang, final int lastIndexOfFolderSeparator) {
    String fileName = lastIndexOfFolderSeparator > -1
        ? prevFileAccess.substring(lastIndexOfFolderSeparator)
        : prevFileAccess;
    int lastDotIndex = fileName.lastIndexOf(".");
    return fileName.substring(0, lastDotIndex) + "_" + lang
        + fileName.substring(lastDotIndex);
  }

  private int getLastIndexOfFolderSeparator() {
    int lastIndexOf = prevFileAccess.lastIndexOf("/");
    if (lastIndexOf == -1) {
      lastIndexOf = prevFileAccess.lastIndexOf("\\");
    }
    return lastIndexOf;
  }

  private String getPathWhitoutFileName(final int lastIndexOfFolderSeparator) {
    if (lastIndexOfFolderSeparator > -1) {
      return prevFileAccess.substring(0, lastIndexOfFolderSeparator);
    }
    return "";
  }

  private void makeDirectories(final String pathWithoutFileName) {
    File file = new File(workingDirectory, pathWithoutFileName);
    if (!file.exists() && !file.mkdirs()) {
      throw new LanguageException(LanguageErrorCode.IO_PROBLEM, "Cannot create directories.");
    }
  }

  /**
   * Start the import file to files.
   */
  public void start() {
    ImportWorkbookHelper importWorkbookHelper = new ImportWorkbookHelper();

    importWorkbookHelper.openWorkbook(importedFileName);

    Map<String, Properties> langProperties = new HashMap<String, Properties>();
    langProperties.put("", new Properties());

    String[] languages = importWorkbookHelper.getLanguages();
    for (String lang : languages) {
      langProperties.put(lang, new Properties());
    }

    int lastRowNumber = importWorkbookHelper.getLastRowNumber();
    for (int i = 1; i <= lastRowNumber; i++) {
      WorkbookRowDTO nextRow = importWorkbookHelper.getNextRow();

      if (prevFileAccess == null) {
        prevFileAccess = nextRow.fileAccess;
      }

      if (!prevFileAccess.equals(nextRow.fileAccess)) {
        writePropertiesToFiles(langProperties);
        prevFileAccess = nextRow.fileAccess;
      } else {
        langProperties.get("").setProperty(nextRow.propKey, nextRow.defaultLangValue);
        for (String lang : languages) {
          langProperties.get(lang).setProperty(nextRow.propKey, nextRow.langValues.get(lang));
        }
      }

    }

    writePropertiesToFiles(langProperties);
  }

  /**
   * Validate parameters.
   *
   * @param importedFileName
   *          the name of the imported file.
   * @param workingDirectory
   *          the working directory (Example: c:\\temp or /tmp).
   *
   * @throws NullPointerException
   *           if one of parameter is null.
   * @throws IllegalArgumentException
   *           if importedFileName or workingDirectory is empty. If workingDirectory is not
   *           directory.
   */
  private void validateParameters(final String importedFileName, final String workingDirectory) {
    Objects.requireNonNull(importedFileName, "Cannot be null importedFileName.");
    Objects.requireNonNull(workingDirectory, "Cannot be null workingDirectoryName.");

    if (importedFileName.trim().isEmpty()) {
      throw new IllegalArgumentException("The importedFileName is empty. Cannot be empty.");
    }
    if (workingDirectory.trim().isEmpty()) {
      throw new IllegalArgumentException("The workingDirectoryName is empty. Cannot be empty.");
    }

    File workingDirectoryFile = new File(workingDirectory);
    if (!workingDirectoryFile.isDirectory()) {
      throw new LanguageException(LanguageErrorCode.WORKING_DIRECTORY_NOT_DIRECTORY,
          "The working directory is not directory.");
    }
  }

  private void writePropertiesToFiles(final Map<String, Properties> langProperties) {
    langProperties.forEach((key, value) -> {
      File langFile;
      if ("".equals(key)) {
        int lastIndexOfFolderSeparator = getLastIndexOfFolderSeparator();
        String pathWithoutFileName = getPathWhitoutFileName(lastIndexOfFolderSeparator);
        makeDirectories(pathWithoutFileName);

        langFile = new File(workingDirectory, prevFileAccess);
      } else {
        int lastIndexOfFolderSeparator = getLastIndexOfFolderSeparator();
        String pathWhitoutFileName = getPathWhitoutFileName(lastIndexOfFolderSeparator);
        makeDirectories(pathWhitoutFileName);

        String langFileName = calculateLangFileName(key, lastIndexOfFolderSeparator);
        langFile = new File(workingDirectory, pathWhitoutFileName + langFileName);
      }

      try (OutputStream out = new FileOutputStream(langFile)) {
        value.store(out, null);
      } catch (IOException e) {
        throw new LanguageException(LanguageErrorCode.IO_PROBLEM,
            "Has IO problem when try to save language file.", e);
      }

      value = new Properties();
    });
  }

}
