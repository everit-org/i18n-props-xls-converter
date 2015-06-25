i18n-props-xls-converter
========================

## Introduction

Az konvertálót használhatjuk a különböző helyen lévő nyelvi fájlok (*.properties)
egy helyre történő össze gyűjtésére egy XLS fájlba a könyebb fordítás érdekében,
illetve a különböző nyelvi fájlok létrehozására egy adott XLS fájlból.

## Usage

A konvertáló export funkciójának használata
```
$ java -jar org.everit.i18n.propsxlsconverter-{version}.jar -f export -xls propsxlsconverter.xls -wd /tmp/ -langs hu,de,us -r .*\.properties
```
A konvertáló import funkciójának használata
```
$ java -jar org.everit.i18n.propsxlsconverter-{version}.jar -f import -xls propsxlsconverter.xls -wd /tmp/
```
## Arguments
Argumentum rövid, hosszú neve | Leírás
----------------------------- | -------
-f, --function | A konvertáló funkciójának meghatározása (export vagy import). Kötelező megadni.
-xls, --xlsFileName | Az export végén létrejövő XLS fájl neve, vagy az importálás során használandó XLS fájl neve. Kötelező megadni.
-wd, --workingDirectory | A munka könyvtár ahol a nyelvi fájlokat keressük. Kötelező megadni.
-langs, --languages | Az alapértelmezett nyelven felüli nyelvek meghatározása. Export funkció esetén kötelező megadni. Például -langs hu,de,us ; vagy csak -langs de ;
-r, --fileRegularExpression | A reguláris kifejezés amivel a munka könyvtárban meg keressük a nyelvi fájlokat. Export esetén kötelező megadni. Például a properties fájlokra: .*\.properties

## Működés

### Export

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

### Import

A konvertáló import funckiójának működése.

* Ellenőrzésre kerülnek az argumentumok (összes szükséges argumentum meg van-e, reguláris kifejezés valid-e)
* Beolvasásra kerül az XLS fájl.
* Az XLS fájl feldolgozásának megkezdése.
* A feldolgozás közben, amint egy nyelvi fájl teljesen beolvasásra került elkészítésre kerül a fájl.

Fontos, hogy a táblázat alapján az import során az alapértelmezett és a táblázatban szereplő nyelvekhez mindenképpen létrejönnek a nyelvi fájlok, függetlenül hogy az adott nyelven egyetlen egy szöveg sem tartozik!
