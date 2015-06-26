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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * The configuration of the {@link PropsXlsConverterMain}.
 */
public class PropsXlsConverterConfig {

  private static final String ARG_FILE_REGULAR_EXPRESSION = "fileRegularExpression";

  private static final String ARG_FUNCTION = "function";

  private static final String ARG_LANGUAGES = "languages";

  private static final String ARG_WORKING_DIRECTORY = "workingDirectory";

  private static final String ARG_XLS_FILE_NAME = "xlsFileName";

  private static final String FUNCTION_EXPORT = "export";

  private static final String FUNCTION_IMPORT = "import";

  private static final Options OPTIONS = new Options();

  static {
    OPTIONS.addOption("f", ARG_FUNCTION, true,
        "The function: 'import' or 'export'."
            + "\nimport: Processes the given excel file and creates the properties files from it."
            + "\nexport: exports the properties files to a human readable and editable excel file."
            + "\nMandatory.");

    OPTIONS.addOption("xls", ARG_XLS_FILE_NAME, true,
        "The excel file used for the import or export function."
            + "\nFor example: translation.xls"
            + "\nMandatory.");

    OPTIONS.addOption("wd", ARG_WORKING_DIRECTORY, true,
        "The working directory used as a base directory for searching the properties files "
            + "reqursively."
            + "\nFor example: /home/foo or C:\\Users\\foo"
            + "\nMandatory.");

    OPTIONS.addOption("r", ARG_FILE_REGULAR_EXPRESSION, true,
        "Regular expression used to search the properties files recursively."
            + "\nFor example: .*\\.properties$"
            + "\nMandatory for export function.");

    OPTIONS.addOption("langs", ARG_LANGUAGES, true,
        "Comma separated list of the languages to be processed."
            + "\nFor example: hu,de"
            + "\nMandatory for the export function.");
  }

  private final CommandLine commandLine;

  /**
   * Constructor.
   */
  public PropsXlsConverterConfig(final String[] args) {

    CommandLineParser commandLineParser = new DefaultParser();
    try {
      commandLine = commandLineParser.parse(OPTIONS, args, true);
    } catch (ParseException e) {
      printHelp();
      throw new RuntimeException(e);
    }
  }

  private String getCommandLineValue(final String key) {

    String result = commandLine.getOptionValue(key);

    if (result == null) {
      printHelp();
      throw new IllegalArgumentException("Missing mandatory argument: " + key);
    }

    return result;
  }

  public String getFileRegularExpression() {
    return getCommandLineValue(ARG_FILE_REGULAR_EXPRESSION);
  }

  public String getFunction() {
    return getCommandLineValue(ARG_FUNCTION);
  }

  public String[] getLanguages() {
    return getCommandLineValue(ARG_LANGUAGES).split(",");
  }

  public String getWorkingDirectory() {
    return getCommandLineValue(ARG_WORKING_DIRECTORY);
  }

  public String getXlsFileName() {
    return getCommandLineValue(ARG_XLS_FILE_NAME);
  }

  public boolean isExportFunction() {
    return FUNCTION_EXPORT.equals(getFunction());
  }

  public boolean isImportFunction() {
    return FUNCTION_IMPORT.equals(getFunction());
  }

  /**
   * Prints the usage of the configuration.
   */
  public void printHelp() {
    HelpFormatter helperFormatter = new HelpFormatter();
    helperFormatter.printHelp(
        "java -jar *.jar -f export -xls example.xls -wd /tmp/ -langs hu,de -r .*\\.properties "
            + "\n java -jar *.jar -f import -xls example.xls -wd /tmp/",
        OPTIONS);
  }

}
