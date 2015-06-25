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
package org.everit.i18n.propsxlsconverter.exception;

/**
 * Exception means has error when application import or export file(s).
 */
public class LanguageException extends RuntimeException {

  /**
   * The language error codes.
   */
  public enum LanguageErrorCode {
    IO_PROBLEM,
    NOT_OPENED_WORKBOOK,
    WORKING_DIRECTORY_NOT_DIRECTORY,
  }

  private static final long serialVersionUID = -3695703390783790044L;

  /**
   * The language error codes.
   */
  public LanguageErrorCode languageErrorCode;

  public LanguageException(final LanguageErrorCode languageErrorCode, final String msg) {
    super(msg);
    this.languageErrorCode = languageErrorCode;
  }

  public LanguageException(final LanguageErrorCode languageErrorCode, final String msg,
      final Throwable cause) {
    super(msg, cause);
    this.languageErrorCode = languageErrorCode;
  }
}
