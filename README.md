## Introduction

LogicSim is a simulation tool for digital circuits.

Simple uses include switches, logic gates (such as AND, OR, XOR, NOT) and LEDs.
For more advanced circuits, modules can be created and reused to build up complexity.

## History
It was created by Andreas Tetzl between 1995 and 2009 at https://www.tetzl.de/, written
firstly for Amiga and then using Java applets. This version is still in use (called 2.4),
but uses a mechanism for saving the files which is no longer supported. So files written
using version 2.4 can unfortunately not be read by subsequent versions.

In 2020 LogicSim was uploaded to GitHub by Peter Gabriel (codepiet) and substantially rewritten,
removing the applets, improving the graphics and switching to an XML-based file format.

In 2025, it was picked up again by Benkralex and developed further, bringing us to the current 3.x state.

## Starting LogicSim
If you just want to start LogicSim3:
1. download the current binary distribution (ZIP) from the github-releases-folder.
2. unzip the file
3. double-click LogicSim.jar
4. If this fails, use the bat-file on Windows or start the jar via terminal or console:
   (go to the folder first)
     javaw.exe -jar LogicSim.jar

## Develop and Contribute
1. checkout the project
2. setup your IDE so that "src" will be recognized as a source-folder
3. start App.java in logicsim-package
4. build your distro via ant-file build.xml
5. if you fix something, raise an Issue or a Pull Request

## Translations
The easiest way to contribute to the translations is by using the Translatinator:
https://activityworkshop.net/translate/project?id=logicsim

With this tool, anyone can help to correct or enhance the existing translations,
add new translations in any of the supported languages, or request that a new
language be added.

## Create the jar file / the distro
Create the jar and complete distribution file via ant-build.
In the ant file the manifest will be added which is necessary for any jar.

## Help / Manual
As well as this Readme, there is a short user guide in Html format, available from the "Help"
menu within the program. (For the sources, see the 'docs' folder).
This is rather brief and could benefit from being updated and from having better pictures,
but it is available in German, English, French and Italian. Not all of these versions are
consistent with each other though, especially regarding screenshots. If you're using LogicSim in
another language, then the English help will be shown.

For earlier versions of the program, there used to be a 'manual' folder containing a Handbuch Pdf
in German, built from `tex` sources. Unfortunately this has fallen out of date and is no longer
provided. If something like this is desired (with a clear reader focus separating developer
concerns from user concerns) then help would be very welcome. But such a manual / handbook could be
in Html or Markdown or something else instead of tex/pdf, and should probably be available in more
than one language?

## Program complexity
It has been mentioned a few times that LogicSim could become a useful educational tool, but
unfortunately the initial complexity could be quite overwhelming. That was one reason why the
`gates` directory was used to dynamically load a smaller selection of components, perhaps
targeted towards a specific teaching goal.

Instead of this, it would be nice if the "mode" selection could be expanded with more options.
Currently only "normal" and "expert" are available, but one could imagine having many more
gradations inbetween. An absolute beginner would not need to have anything to do with modules
or Flip-Flops but would benefit from a simplified list just showing basic gates with switches
and LEDs. A subsequent lesson might then introduce clocks and counters, gradually expanding
the complexity. CPU stuff and half-adders could then come later.

It would be good to get some feedback from teachers or students who have used LogicSim in
such a setting, to see what simplifications would make sense.

## Contributing
If you find problems or you have suggestions for improvements, please just raise a new Issue.

If you can help with fixing, expanding or translating any of the documentation,
that would be very welcome. Again, either raise an Issue if you're not sure how to help,
or create a Pull Request directly. That also applies to the translations within the program,
although probably it's simpler to just use the Translatinator (see above).
