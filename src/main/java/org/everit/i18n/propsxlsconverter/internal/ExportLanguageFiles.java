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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.everit.i18n.propsxlsconverter.dto.PropKeyRowNumberDTO;
import org.everit.i18n.propsxlsconverter.exception.LanguageException;
import org.everit.i18n.propsxlsconverter.exception.LanguageException.LanguageErrorCode;
import org.everit.i18n.propsxlsconverter.helper.ExportWorkBookHelper;

/**
 * Export file(s) (with search fileRegularExpression) to one XLS file.
 */
public class ExportLanguageFiles {

  private static final String UNDERLINE = "_";

  private String exportedFileName;

  /**
   * Map key is fileAccces.
   */
  private Map<String, List<PropKeyRowNumberDTO>> fileAccessPropertyKeyRowNumber;

  private String fileRegularExpression;

  private String[] languages;

  private String workingDirectory;

  /**
   * Constructor.
   *
   * @param exportedFileName
   *          the name of the exported file. Cannot be <code>null</code> or empty.
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
  public ExportLanguageFiles(final String exportedFileName, final String workingDirectory,
      final String fileRegularExpression, final String[] languages) {
    validateParameters(exportedFileName, workingDirectory, fileRegularExpression, languages);

    this.exportedFileName = exportedFileName;
    this.workingDirectory = workingDirectory;
    this.fileRegularExpression = fileRegularExpression;
    this.languages = languages;
    fileAccessPropertyKeyRowNumber = new HashMap<String, List<PropKeyRowNumberDTO>>();
  }

  private void addPropKeyRowNumberToWorkbookKeyMap(final String relativePathToDefaultLanguageFile,
      final String propKey, final int rowNumber) {
    List<PropKeyRowNumberDTO> list = fileAccessPropertyKeyRowNumber
        .get(relativePathToDefaultLanguageFile);
    PropKeyRowNumberDTO propKeyRowNumber = new PropKeyRowNumberDTO()
        .propKey(propKey)
        .rowNumber(rowNumber);
    if (list == null) {
      list = new ArrayList<PropKeyRowNumberDTO>();
      list.add(propKeyRowNumber);
      fileAccessPropertyKeyRowNumber.put(relativePathToDefaultLanguageFile, list);
    } else {
      list.add(propKeyRowNumber);
    }
  }

  private String calculateDefaultLangFileName(final String fileName, final String searchLang,
      final int lastIndexOf) {
    String fileNameFirstPart = fileName.substring(0, lastIndexOf);
    String fileNameSecondPart = fileName.substring(lastIndexOf + searchLang.length());
    return fileNameFirstPart + fileNameSecondPart;
  }

  /**
   * Calculate file access between the working directory and language file.
   *
   * @param languageFile
   *          the language file.
   * @return the calculated file access path.
   */
  private String calculateFileAccess(final File languageFile) {
    Path workingDirectoryPath = Paths.get(workingDirectory);
    String languageFileAbsolutePath = languageFile.getAbsolutePath();
    Path languageFilePath = Paths.get(languageFileAbsolutePath);

    String fileName = languageFile.getName();
    for (String lang : languages) {
      String searchLang = UNDERLINE + lang;
      int lastIndexOf = fileName.lastIndexOf(searchLang);
      if (lastIndexOf > -1) {
        String defaultLangFileName = calculateDefaultLangFileName(fileName,
            searchLang,
            lastIndexOf);
        String defaultLangFileAbsolutePath = languageFileAbsolutePath.replace(fileName,
            defaultLangFileName);
        languageFilePath = Paths.get(defaultLangFileAbsolutePath);
      }
    }

    Path relativize = workingDirectoryPath.relativize(languageFilePath);
    return relativize.toString();
  }

  private Integer findRowNumber(final String relativePathToDefaultLanguageFile,
      final String propKey) {
    List<PropKeyRowNumberDTO> list = fileAccessPropertyKeyRowNumber
        .get(relativePathToDefaultLanguageFile);
    if ((list == null) || list.isEmpty()) {
      return null;
    }
    for (PropKeyRowNumberDTO pkrn : list) {
      if (pkrn.propKey.equals(propKey)) {
        return pkrn.rowNumber;
      }
    }
    return null;
  }

  /**
   * Gets language from file name.
   *
   * @param fileName
   *          the file name.
   * @return the language (hu, de) or if default language "" (empty string).
   */
  private String getLanguage(final String fileName) {
    for (String lang : languages) {
      if (fileName.contains(UNDERLINE + lang)) {
        return lang;
      }
    }
    return "";
  }

  /**
   * Start the export files to one XLS file.
   */
  public void start() {
    File workingDirectoryFile = new File(workingDirectory);

    Collection<File> files = FileUtils.listFiles(workingDirectoryFile,
        new RegexFileFilter(fileRegularExpression),
        DirectoryFileFilter.DIRECTORY);

    ExportWorkBookHelper exportWorkbookHelper = new ExportWorkBookHelper(languages);

    exportWorkbookHelper.createFirstRow();

    if (files.isEmpty()) {
      exportWorkbookHelper.writeWorkbookToFile(exportedFileName);
      return;
    }

    for (File file : files) {
      String lang = getLanguage(file.getName());
      String fileAccess = calculateFileAccess(file);

      try (InputStream inputStream = new FileInputStream(file)) {
        Properties properties = new Properties();
        properties.load(inputStream);

        Set<String> propertyNames = properties.stringPropertyNames();
        for (String propKey : propertyNames) {
          Integer updatedRowNumber = findRowNumber(fileAccess, propKey);
          String propValue = properties.getProperty(propKey);

          if (updatedRowNumber == null) {
            int rowNumber = exportWorkbookHelper.insertRow(fileAccess,
                propKey,
                lang,
                propValue);
            addPropKeyRowNumberToWorkbookKeyMap(fileAccess, propKey,
                rowNumber);
          } else {
            exportWorkbookHelper.updateRow(updatedRowNumber,
                lang,
                propValue);
          }
        }
      } catch (IOException e) {
        throw new LanguageException(LanguageErrorCode.IO_PROBLEM,
            "Has problem with IO when try to load properties files.", e);
      }
    }

    exportWorkbookHelper.writeWorkbookToFile(exportedFileName);
  }

  /**
   * Validate parameters.
   *
   * @param exportedFileName
   *          the name of the exported file.
   * @param workingDirectory
   *          the working directory (Example: c:\\temp or /tmp).
   * @param fileRegularExpression
   *          the regex expression to find files which want to export to XLS file. Example:
   *          .*\.properties$ to find all properties files.
   * @param languages
   *          the languages which want to search.
   *
   * @throws NullPointerException
   *           if one of parameter is null.
   * @throws IllegalArgumentException
   *           if exportedFileName or workingDirectory or fileRegularExpression is empty. If
   *           workingDirectory is not directory.
   * @throws java.util.regex.PatternSyntaxException
   *           if fileRegularExpression is not valid.
   */
  private void validateParameters(final String exportedFileName, final String workingDirectory,
      final String fileRegularExpression, final String[] languages) {
    Objects.requireNonNull(exportedFileName, "Cannot be null exportedFileName.");
    Objects.requireNonNull(workingDirectory, "Cannot be null workingDirectoryName.");
    Objects.requireNonNull(fileRegularExpression, "Cannot be null fileRegularExpression.");
    Objects.requireNonNull(languages, "Cannot be null languages.");

    if (exportedFileName.trim().isEmpty()) {
      throw new IllegalArgumentException("The exportedFileName is empty. Cannot be empty.");
    }
    if (workingDirectory.trim().isEmpty()) {
      throw new IllegalArgumentException("The workingDirectoryName is empty. Cannot be empty.");
    }
    if (fileRegularExpression.trim().isEmpty()) {
      throw new IllegalArgumentException("The fileRegularExpression is empty. Cannot be empty.");
    }

    File workingDirectoryFile = new File(workingDirectory);
    if (!workingDirectoryFile.isDirectory()) {
      throw new LanguageException(LanguageErrorCode.WORKING_DIRECTORY_NOT_DIRECTORY,
          "The working directory is not directory.");
    }

    Pattern.compile(fileRegularExpression);
  }
}
