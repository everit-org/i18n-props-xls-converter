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
package org.everit.i18n.propsxlsconverter.internal.dto;

import java.util.Map;

/**
 * A workbook row.
 */
public class WorkbookRowDTO {

  public String defaultLangValue;

  public Map<String, String> langValues;

  public String propertiesFile;

  public String propKey;

  public WorkbookRowDTO defaultLangValue(final String defaultLangValue) {
    this.defaultLangValue = defaultLangValue;
    return this;
  }

  public WorkbookRowDTO langValues(final Map<String, String> langValues) {
    this.langValues = langValues;
    return this;
  }

  public WorkbookRowDTO propertiesFile(final String propertiesFile) {
    this.propertiesFile = propertiesFile;
    return this;
  }

  public WorkbookRowDTO propKey(final String propKey) {
    this.propKey = propKey;
    return this;
  }

}
