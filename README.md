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
$ java -jar org.everit.i18n.propsxlsconverter-{version}.jar -f export -xls propsxlsconverter.xls -wd /tmp/ -langs hu,de,us -r .*\.properties
```
A konvertáló export funkciójának működése.
* Ellenőrzésre kerülnek az argumentumok (összes szükséges argumentum meg van-e, munka könyvtár könytár-e, reguláris kifejezés valid-e)
* Megkeresésre kerülnek a reguláris kifejezés alapján a nyelvi fájlok.
* Az XLS alap információk elhelyezésre kerülnek az XLS munkafüzetben.
* A nyelvi fájlok feldolgozása és az XLS munkafüzet folyamatos bővítése.
* Az XLS fájl fizikai létrehozása.

Az elkészítésre XLS fájl oszlop leírása.
* A oszlop: a nyelvi fájl elérése a munkakönyvtárhoz képest
* B oszlop: a nyelvi fájlban lévő kulcs érték
* C oszlop: az alapértelmezett nyelvhez tartozó érték
* D - * oszlop: a megadott nyelvekhez tartozó érték

###Import
```
$ java -jar org.everit.i18n.propsxlsconverter-{version}.jar -f import -xls propsxlsconverter.xls -wd /tmp/
```
A konvertáló import funckiójának működése.

* Ellenőrzésre kerülnek az argumentumok (összes szükséges argumentum meg van-e, reguláris kifejezés valid-e)
* Beolvasásra kerül az XLS fájl.
* Az XLS fájl feldolgozásának megkezdése.
* A feldolgozás közben, amint egy nyelvi fájl teljesen beolvasásra került elkészítésre kerül a fájl.

Fontos, hogy a táblázat alapján az import során az alapértelmezett és a táblázatban szereplő nyelvekhez mindenképpen létrejönnek a nyelvi fájlok, függetlenül hogy az adott nyelven egyetlen egy szöveg sem tartozik!
