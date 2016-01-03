Users Guide
========================

What is PoeditSmarty
-------------------

Library for syncing gettext catalogs with Smarty sources. 
Library parses files and writes results as xgettext files, whose are processed by Poedit parser.
Soon I will make exe version without java (I dont have so much time)

If you notice oversights, please send me msg.

## Installation - ExE

### set file in path

* You need a NetFramework to run it.
* Download sources and unpack to any folder.
* Copy the file exe\PoeditSmarty.exe predetermined folder (And remember the path || Preferable to the path of the folder of poedit [Poedit\Gettext Tools\bin]).  For example: C:\Program Files\Poedit\Gettext Tools\bin

### Parser params:

* Create new parser in Poedit: File -> Preferences -> Parsers -> New
* Language: `Smarty`
* Parser command: `<PoeditSmarty.exe path>` -o %o -c %C -k %K -f %F  (if the file in -> "C:\Program Files\Poedit\Gettext Tools\bin" [poedit foder] you does not need an absolute path)
* Parser command For example: PoeditSmarty -o %o -c %C -k %K -f %F
* List of extensions: `*.tpl`
* An item in keywords list: `%k`
* An item in input files list: `%f`
* Source code charset: `%c`

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

```
{t}Text to be localized{/t}
```

```
{"Text to be localized"|_}
{_("Text to be localized")}
```

`_` Varies according to keyword

```
{"Text to be localized"|<keyword>}
{<keyword>("Text to be localized")}
```
