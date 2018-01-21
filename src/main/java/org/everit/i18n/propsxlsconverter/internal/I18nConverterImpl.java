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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.everit.i18n.propsxlsconverter.I18nConverter;
import org.everit.i18n.propsxlsconverter.internal.dto.PropKeyRowNumberDTO;
import org.everit.i18n.propsxlsconverter.internal.dto.WorkbookRowDTO;
import org.everit.i18n.propsxlsconverter.internal.workbook.WorkbookReader;
import org.everit.i18n.propsxlsconverter.internal.workbook.WorkbookWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * The {@link I18nConverter} implementation.
 */
public class I18nConverterImpl implements I18nConverter {

  private static final int SEPARATOR_SIZE = 5;

  private static final String UNDERLINE = "_";

  private static final String EQUALSIGN = "=";

  /**
   * Map key is fileAccces.
   */
  private Map<String, List<PropKeyRowNumberDTO>> fileAccessPropertyKeyRowNumber =
      new HashMap<String, List<PropKeyRowNumberDTO>>();

  private void addPropKeyRowNumberToWorkbookKeyMap(final String fileAccess,
      final String propKey, final int rowNumber) {
    List<PropKeyRowNumberDTO> list = fileAccessPropertyKeyRowNumber.get(fileAccess);
    PropKeyRowNumberDTO propKeyRowNumber = new PropKeyRowNumberDTO()
        .propKey(propKey)
        .rowNumber(rowNumber);
    if (list == null) {
      list = new ArrayList<PropKeyRowNumberDTO>();
      list.add(propKeyRowNumber);
      fileAccessPropertyKeyRowNumber.put(fileAccess, list);
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
  private String calculateFileAccess(final File languageFile, final String[] languages,
      final String workingDirectory) {
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

  private String calculateLangFileName(final String fileAccess, final String lang,
      final int lastIndexOfFolderSeparator) {
    String fileName = lastIndexOfFolderSeparator > -1
        ? fileAccess.substring(lastIndexOfFolderSeparator)
        : fileAccess;
    int lastDotIndex = fileName.lastIndexOf(".");
    return fileName.substring(0, lastDotIndex) + UNDERLINE + lang
        + fileName.substring(lastDotIndex);
  }

  @Override
  public void exportToXls(final String xlsFileName, final String workingDirectory,
      final String fileRegularExpression, final String[] languages) {

    validateExportParameters(xlsFileName, workingDirectory, fileRegularExpression, languages);

    File workingDirectoryFile = new File(workingDirectory);

    Collection<File> files = getFilesWithSorted(fileRegularExpression, workingDirectoryFile);

    WorkbookWriter workbookWriter = new WorkbookWriter(xlsFileName, languages);

    if (files.isEmpty()) {
      workbookWriter.writeWorkbookToFile();
      return;
    }

    for (File file : files) {
      String lang = getLanguage(file.getName(), languages);
      String fileAccess = calculateFileAccess(file, languages, workingDirectory);

      try (FileInputStream fileInputStream = new FileInputStream(file);
          InputStreamReader inputStreamReader =
              new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
          BufferedReader br = new BufferedReader(inputStreamReader)) {
        String line = null;
        while ((line = br.readLine()) != null) {
          // ignore empty and comment lines
          if (!"".equals(line) && (line.charAt(0) != '#')) {
            String unescapedLine = trimLineAroundEqualSign(StringEscapeUtils.unescapeJava(line));
            int separatorIndex = getPropertySeparatorIndex(unescapedLine);
            String propKey = unescapedLine.substring(0, separatorIndex);
            String propValue = unescapedLine.substring(separatorIndex + 1);

            insertOrUpdateWorkbookRow(workbookWriter, lang, fileAccess, propKey, propValue);
          }
        }
      } catch (IOException e) {
        throw new RuntimeException("Has problem with IO when try to load/process properties "
            + "files.", e);
      }
    }

    workbookWriter.writeWorkbookToFile();
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

  private Collection<File> getFilesWithSorted(final String fileRegularExpression,
      final File workingDirectoryFile) {
    Collection<File> files = FileUtils.listFiles(workingDirectoryFile,
        new RegexFileFilter(fileRegularExpression),
        DirectoryFileFilter.DIRECTORY);

    if (files instanceof List<?>) {
      // guarantees that the first file is the default language file.
      Collections.sort((List<File>) files,
          (file1, file2) -> file1.getName().compareTo(file2.getName()));
    }
    return files;
  }

  /**
   * Gets language from file name.
   *
   * @param fileName
   *          the file name.
   * @return the language (hu, de) or if default language "" (empty string).
   */
  private String getLanguage(final String fileName, final String[] languages) {
    for (String lang : languages) {
      if (fileName.contains(UNDERLINE + lang)) {
        return lang;
      }
    }
    return "";
  }

  private int getLastIndexOfFolderSeparator(final String fileAccess) {
    int lastIndexOf = fileAccess.lastIndexOf("/");
    if (lastIndexOf == -1) {
      lastIndexOf = fileAccess.lastIndexOf("\\");
    }
    return lastIndexOf;
  }

  private String getPathWithoutFileName(final String fileAccess,
      final int lastIndexOfFolderSeparator) {
    if (lastIndexOfFolderSeparator > -1) {
      return fileAccess.substring(0, lastIndexOfFolderSeparator);
    }
    return "";
  }

  /**
   * Remove spaces around =.
   * e.g. key.subkey      = value
   * @param line string to trim. should have equal sign.
   * @return trimmed string
   */
  private String trimLineAroundEqualSign(final String line) {

    if (line.contains(EQUALSIGN)) {

      String key = StringUtils.trim(StringUtils.substringBefore(line, EQUALSIGN));
      String value = StringUtils.trimToEmpty(StringUtils.substringAfter(line, EQUALSIGN));

      return key + EQUALSIGN + value;
    } else {
      return line;
    }
  }


  private int getPropertySeparatorIndex(final String unescapedLine) {
    int[] separators = new int[SEPARATOR_SIZE];
    int index = 0;
    separators[index++] = unescapedLine.indexOf('=');
    separators[index++] = unescapedLine.indexOf(' ');
    separators[index++] = unescapedLine.indexOf(':');
    separators[index++] = unescapedLine.indexOf('\t');
    separators[index++] = unescapedLine.indexOf('\f');
    Arrays.sort(separators);
    for (int i = 0; i < separators.length; i++) {
      if (separators[i] != -1) {
        return separators[i];
      }
    }
    throw new RuntimeException("Not find separator in the line. Unescaped line: [" + unescapedLine
        + "].");
  }

  @Override
  public void importFromXls(final String xlsFileName, final String workingDirectory) {

    validateImportParameters(xlsFileName, workingDirectory);

    WorkbookReader workbookReader = new WorkbookReader(xlsFileName);

    Map<String, Properties> langProperties = new HashMap<String, Properties>();
    langProperties.put("", new Properties());

    String[] languages = workbookReader.getLanguages();
    for (String lang : languages) {
      langProperties.put(lang, new Properties());
    }

    String prevPropertiesFile = null;
    ArrayList<String> propKeySequence = new ArrayList<String>();
    int lastRowNumber = workbookReader.getLastRowNumber();
    for (int i = 1; i <= lastRowNumber; i++) {
      WorkbookRowDTO nextRow = workbookReader.getNextRow();

      if (prevPropertiesFile == null) {
        prevPropertiesFile = nextRow.propertiesFile;
      }

      if (!prevPropertiesFile.equals(nextRow.propertiesFile)) {
        writePropertiesToFiles(langProperties, prevPropertiesFile, workingDirectory,
            propKeySequence);
        prevPropertiesFile = nextRow.propertiesFile;
      }

      langProperties.get("").setProperty(nextRow.propKey, nextRow.defaultLangValue);
      for (String lang : languages) {
        langProperties.get(lang).setProperty(nextRow.propKey, nextRow.langValues.get(lang));
      }
      propKeySequence.add(nextRow.propKey);
    }

    writePropertiesToFiles(langProperties, prevPropertiesFile, workingDirectory,
        propKeySequence);
  }

  private void insertOrUpdateWorkbookRow(final WorkbookWriter workbookWriter, final String lang,
      final String fileAccess, final String propKey, final String propValue) {
    Integer updatedRowNumber = findRowNumber(fileAccess, propKey);
    if (updatedRowNumber == null) {
      int rowNumber = workbookWriter.insertRow(fileAccess,
          propKey,
          lang,
          propValue);
      addPropKeyRowNumberToWorkbookKeyMap(fileAccess, propKey,
          rowNumber);
    } else {
      workbookWriter.updateRow(updatedRowNumber,
          lang,
          propValue);
    }
  }

  private void makeDirectories(final String workingDirectory, final String pathWithoutFileName) {
    File file = new File(workingDirectory, pathWithoutFileName);
    if (!file.exists() && !file.mkdirs()) {
      throw new RuntimeException("Cannot create directories.");
    }
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
  private void validateExportParameters(final String exportedFileName,
      final String workingDirectory, final String fileRegularExpression, final String[] languages) {

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
      throw new RuntimeException("The working directory is not directory.");
    }

    Pattern.compile(fileRegularExpression);
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
  private void validateImportParameters(final String importedFileName,
      final String workingDirectory) {

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
      throw new RuntimeException("The working directory is not directory.");
    }
  }

  private void writePropertiesToFiles(final Map<String, Properties> langProperties,
      final String fileAccess, final String workingDirectory,
      final ArrayList<String> propKeySequence) {

    langProperties.forEach((key, value) -> {

      File langFile = null;
      String pathWithoutFileName = null;
      String langFileName = fileAccess;

      if ("".equals(key)) {

        int lastIndexOfFolderSeparator = getLastIndexOfFolderSeparator(fileAccess);
        pathWithoutFileName = getPathWithoutFileName(langFileName,
            lastIndexOfFolderSeparator);
        makeDirectories(workingDirectory, pathWithoutFileName);

        langFile = new File(workingDirectory, langFileName);

      } else {

        int lastIndexOfFolderSeparator = getLastIndexOfFolderSeparator(fileAccess);
        pathWithoutFileName = getPathWithoutFileName(fileAccess,
            lastIndexOfFolderSeparator);
        makeDirectories(workingDirectory, pathWithoutFileName);

        langFileName = calculateLangFileName(fileAccess, key,
            lastIndexOfFolderSeparator);
        langFile = new File(workingDirectory, pathWithoutFileName + langFileName);
      }

      try (FileOutputStream out = new FileOutputStream(langFile);
          OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out,
              StandardCharsets.UTF_8);
          BufferedWriter bw = new BufferedWriter(outputStreamWriter);) {

        StringBuilder sb = new StringBuilder();

        propKeySequence.forEach((propKey) -> {
          String propValue = value.getProperty(propKey);
          sb.append(propKey);
          sb.append("=");
          sb.append(StringEscapeUtils.escapeJava(propValue));
          sb.append("\n");
        });

        bw.write(sb.toString());
      } catch (IOException e) {
        throw new RuntimeException(
            "Failed to save file [" + pathWithoutFileName + langFileName + "]", e);
      }

      value.clear();
    });

    propKeySequence.clear();
  }
}
