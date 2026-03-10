![](../src/logicsim/images/about.jpg?raw=true)

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

## Create the jar file / the distro
Create the jar and complete distribution file via ant-build.
In the ant file the manifest will be added which is necessary for any jar.

## Manual
As well as this Readme, there are also two other kinds of documentation:
 * Within the program, there's a short user guide in Html format (see the 'docs' folder).
This is rather brief and could benefit from better pictures,
but it is available in German, English, French and Italian.
 * Outside the program, there's a 'manual' folder containing a Handbuch Pdf in German.
This seems to be rather outdated, and hasn't been updated for years. Maybe this should be
in Html or Markdown or something else instead, and maybe in more than one language?

There's some overlap between these two, and maybe we don't need both?

## Contributing
If you find problems or you have suggestions for improvements, please just raise a new Issue.

If you can help with fixing, expanding or translating any of the documentation,
that would be very welcome. Again, either raise an Issue if you're not sure how to help,
or create a Pull Request directly. That also applies to the translations within the program.