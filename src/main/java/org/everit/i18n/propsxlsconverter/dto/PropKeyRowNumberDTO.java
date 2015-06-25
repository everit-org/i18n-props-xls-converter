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
package org.everit.i18n.propsxlsconverter.dto;

/**
 * The class to property key and row number binding.
 */
public class PropKeyRowNumberDTO {

  public String propKey;

  public int rowNumber;

  /**
   * Add property key.
   */
  public PropKeyRowNumberDTO propKey(final String propKey) {
    this.propKey = propKey;
    return this;
  }

  /**
   * Add row number.
   */
  public PropKeyRowNumberDTO rowNumber(final int rowNumber) {
    this.rowNumber = rowNumber;
    return this;
  }
}
