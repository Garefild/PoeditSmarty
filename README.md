Users Guide
========================

What is PoeditSmarty
-------------------

Library for syncing GetText catalogs with Smarty sources. 
Library parses files and writes results as xgettext files, whose are processed by Poedit parser.
Soon I will make C++ version without java.

If you notice oversights, please send me msg.
version 3.0.0

![alt tag](https://raw.githubusercontent.com/Garefild/PoeditSmarty/master/Images/Screenshot_1.png)
![alt tag](https://raw.githubusercontent.com/Garefild/PoeditSmarty/master/Images/Screenshot_2.png)
![alt tag](https://raw.githubusercontent.com/Garefild/PoeditSmarty/master/Images/Screenshot_3.png)

## Installation - Java

### set file in path

* You need java to run it.
* Download sources and unpack to any folder.
* Copy the file PoeditSmarty.jar predetermined folder (And remember the path).  For example: C:\Program Files\Poedit\Gettext Tools\bin
* get java path (And remember the path). For example: C:\Program Files\Java\jdk\bin\java.exe

### Parser params:

* Create new parser in Poedit: File -> Preferences -> Parsers -> New
* Language: `Smarty`
* Parser command: `<java.exe path>` -jar `<PoeditSmarty.jar path>` -o %o -c %C -k %K -f %F  
* Parser command For example: "C:\Program Files\Java\jdk\bin\java" -jar "C:\Program Files\Poedit\GettextTools\bin\PoeditSmarty.jar" -o %o -c %C -k %K -f %F 
* List of extensions: `*.tpl`
* An item in keywords list: `%k`
* An item in input files list: `%f`
* Source code charset: `%c`

### global info
* if java set as `System variable` does not need an absolute path to java. 
* Parser command For example: java -jar "C:\Program Files\Poedit\GettextTools\bin\PoeditSmarty.jar" -o %o -c %C -k %K -f %F 
* It also has support in cmd.

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
{t params } 
    multi line with params 
    Line 1
    Line 2 ...
{/t}
```

```
{ "Text to be localized"|_ }
{ _("Text to be localized") }
```

```
@niklausburren tnx :)

{_("Text to be (localized)")}
```

```
@justanpo tnx :)

{ ngettext("product", "products", $products_count) }
```

`_` Varies according to keyword

```
{ "Text to be localized"|<keyword> }
{ <keyword>("Text to be localized") }
```

```
@IacopoOrtis tnx :)

{t nick=$userName day=$dayOfTheWeek}Hello %1, today is %2.{/t}
```
