# LogicSim3

## Introduction
LogicSim is a simulation tool for digital circuits.
It has been programmed until 2009 by Andreas Tetzl until version 2.4 and this version is hosted on http://www.tetzl.de/java_logic_simulator_de.html.
In 2020 LogicSim has been reprogrammed by Peter Gabriel (pngabriel@gmail.com) to enhance the program and get rid of old techniques (e.g. applets) (this is hosted on https://github.com/codepiet/LogicSim3).
In 2025, I (Benkralex) started to enhance the program, because we use it at my school.

Unfortunately old files written with LogicSim2 cannot be used with LogicSim3.

## Starting LogicSim
If you just want to start LogicSim3:
1. **Download** the LogicSim-[Version].zip from [the GitHub-Releases](https://github.com/Benkralex/LogicSim3/releases/latest).
2. **Unzip** the file
3. Make the file executable (Linux or Mac):
```Linux or Mac:
chmod +x LogicSim.jar
```
4. **Double-click** LogicSim.jar
5. If this fails, use the bat-file on Windows or the .sh-file on Linux 
6. Or start the jar via terminal or console:
   (open the folder in the terminal)

Windows:
```bash
javaw.exe -jar LogicSim.jar
```
Linux or Mac:
```shell
java -jar LogicSim.jar
```

## Develop and Contribute
1. Checkout the project
2. Set up your IDE so that "src" will be recognized as source-folder
3. Start App.java in logicsim-package
4. Build your distro via ant-file build.xml
5. If you fix something, feel free to create a pull-request

## Create the jar file / the distro
```shell
ant clean-build #for .jar file (in build/jar/)
ant dist #for .zip file (in release/)
```

## Manual
There is only very sparse documentation about this project - and only in German!
Please experiment with the functions. The manual is in the manual-subfolder in the project 
and will be revised soon.

## Issues
For issue tracking, there is a tracking system on GitHub. It would be nice if you report a bug.