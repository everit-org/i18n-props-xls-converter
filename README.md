i18n-props-xls-converter
========================

## Introduction

Using this converter you can collect internatialization properties files (containing 'key=value' 
lines ) and writes their content into an XLS file (export) to support easier transalation of a 
project. The converter also creates the properties files from the XLS (import).

## Arguments
Short and long name | Description
------------------- | -----------
-f, --function | The function: 'import' or 'export'. (mandatory)
-xls, --xlsFileName | The excel file used by the import or export function. (mandatory)
-wd, --workingDirectory | The working directory used as a base directory for searching the properties files reqursively. (mandatory)
-r, --fileRegularExpression | Regular expression used to search the properties files recursively. (mandatory for export function)
-langs, --languages | Comma separated list of the languages to be processed. (mandatory for the export function)

## Usage

###Export
```
$ java -jar org.everit.i18n.propsxlsconverter-{version}.jar -f export -xls translations.xls -wd /tmp/ -langs hu,de,us -r .*\.properties
```
The format of the created XLS file:
* Column A: the properties file location according to the working directory
* Column B: the keys used in the properties files
* Column C: the values of the keys of the default language
* Column D - *: the values of the additional languages

###Import
```
$ java -jar org.everit.i18n.propsxlsconverter-{version}.jar -f import -xls translations.xls -wd /tmp/
```

*Important:* Using the import function all language files will be created even if there were no 
keys in a given language.
