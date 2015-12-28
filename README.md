Users Guide
========================

What is PoeditSmarty
-------------------

Library for syncing gettext catalogs with Smarty sources. 
Library parses files and writes results as xgettext files, whose are processed by Poedit parser.
Soon I will exe version without java (I have so much time)

If you notice oversights, please send me msg.

## Installation

### set file in path

* You need a java run it.
* Download sources and unpack to any folder.
* Copy the file PoeditSmarty.jar predetermined folder (And remember the path).  For example: C:\Program Files\Poedit\Gettext Tools\bin
* get java path (And remember the path). For example: C:\Program Files\Java\jdk1.8.0_65\bin\java.exe

### Parser params:

* Create new parser in Poedit: File -> Preferences -> Parsers -> New
* Language: `Smarty`
* Parser command: `<java.exe path>` -jar `<PoeditSmarty.jar path>` -o %o -c %C -k %K -f %F  
* Parser command For example: `<C:\Program Files\Java\jdk1.8.0_65\bin\java>` -jar `C:\Program Files\Poedit\GettextTools\bin\PoeditSmarty.jar` -o %o -c %C -k %K -f %F 
* List of extensions: `*.tpl`
* An item in keywords list: `%k`
* An item in input files list: `%f`
* Source code charset: `%c`

### global info
if java set as `System variable` does not need an absolute path to java.
Parser command For example: java -jar `C:\Program Files\Poedit\GettextTools\bin\PoeditSmarty.jar` -o %o -c %C -k %K -f %F 
It also has support in cmd.

### Support cmd parameters

* -c , --code            <Args>  <Required> : charset flag.
* -d , --debug                              : enable debug.
* -f , --file            <Args>  <Required> : list of input files.
* -h , --help                               : Display command line parameters for use.
* -k , --key             <Args>  <Required> : list of keywords.
* -o , --out             <Args>  <Required> : expands to the name of output file.

### Known issues
* I have not found, if found then reported me

## Supported formats

block.t plugin:
```
{t}Text to be localized{/t}
```
native:
```
{"Text to be localized"|_}
{_("Text to be localized")}
```
