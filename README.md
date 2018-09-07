 <-----/@@\----->                              
<-< <  \\//  > >->                             
  <-<-\ __ /->->                               
  Data /  \ Crow                               
      ^    ^ 

Created by Robert Jan van der Waals

Contact me at info@datacrow.net
Find me at http://www.datacrow.net
           http://sourceforge.net/projects/datacrow


----------------------------------------------------------------------------------------
Table of Content

 1.0 ------- Introduction
 2.0 ------- Requirements
 3.0 ------- About the database (HSQL)
 4.0 ------- Upgrading
 5.0 ------- Building the project
 6.0 ------- Starting Data Crow 
 7.0 ------- Credits
 8.0 ------- Licenses and 3rd party software


----------------------------------------------------------------------------------------
1.0 Introduction

Data Crow is an application to register Software, Images, Music Files, Audio CDs, 
Books and Movies. The registration is automated as far as possible. To achieve 
this, an internet connection is needed as Data Crow uses web services 
(like amazon.com, imdb.com and freedb.org) to retrieve information about a piece 
of software, an audio CD or a movie. Further more, files are parsed to retrieve 
useful information. 

Features:

* Internal help system (F1).
* Skinnable, nice looking and easy to use UI.
* You can customize almost everything; design your own item form, quick view, hide fields
  and module which you are not using and create your own reports (XSLT scripts).  
* Platform independent and completely portable (run from an USB stick).
* Loan administration. Keep track of your loans.
* Application Server (separate download).
* Create your own collection modules and/or modify existing collection modules.
* Advanced user configuration (access rights + module and field access).
* Rename your files based on the information of Data Crow.
* Highly customizable; add your own fields, alter existing modules or create a new
  one, rename any text within the application, set the fonts, ..
* Reporting (PDF, HTML, Text and XML).
* Registration of Software, Audio CDs and Music Files, Books, Movies and Images.
* Search for items using online services such as Amazon.com (http://www.amazon.com), 
  Imdb (http://www.imdb.com), Musicbrainz (http://musicbrainz.org) and many others.
* Extract information from files on your CD, DVD or Harddisk:
  image files (JPG, GIF, PNG, SVG), music files (technical info and tag content of 
  ASF, OGG, APE, FLAC and MP3 files) and movie file (DivX, Xvid, ASF, MKV, OGM, 
  RIFF, MOV, IFO, VOB and Mpeg video).
* Internal HSQL database + SQL query tool for expert users.

Data Crow does not write information to the registry or any other platform specific 
folder or structure. All the information is kept within the Data Crow installation 
folder. It does not obey platform specific rules and can run on any platform 
(Windows, Linux and others) having Java 1.6 (from Oracle) or higher installed. 


----------------------------------------------------------------------------------------
2.0 Requirements

Data Crow was tested on:
Windows XP, Windows 7 and Ubuntu (latest version at the moment of testing).
Systems used: Core 2 Duo 2.2ghz

Minimum requirement: 
An 1.0 Ghz system is perfectly capable of running Data Crow.
Data Crow needs, for large collections, at least 256 MB of free memory.
(a collection of 10,000 items or more is considered large).

Data Crow needs Java:
  * JRE 1.7 (or higher) from Oracle (http://java.com/)


----------------------------------------------------------------------------------------
3.0 About the database (HSQL)

Data Crow uses the HSQL database engine. It's powerful, fast and can run on
any system. Look at http://http://hsqldb.org/ for more information

By default a database with the name "dc" is used. You can use a different database by 
using the parameters. See chapter 6.0 for more information.


----------------------------------------------------------------------------------------
4.0 Upgrading

1) Create a backup of your data in the old version using the Backup & Restore utility
2) Install the new version on top of the existing version.


----------------------------------------------------------------------------------------
5.0 Building the project

Use Apache Ant (see http://jakarta.apache.org/) to build this project. Ant will you use
the build.xml file to compile the full Data Crow project.


----------------------------------------------------------------------------------------
6.0 Starting Data Crow

Data Crow can be started by typing "java -Xmx256m -jar datacrow.jar". 
On Windows platforms the datacrow32bit.exe or datacrow64bit.exe file can be used to start.

    -datacrow32bit.exe: can only be used in case the 32 bit version of Java has been 
     installed.

    -datacrow64bit.exe: can only be used in case the 64 bit version of Java has been 
     installed. It could be that you have a 64bit operating system while having 
     a 32bit version of Java installed. In this case the datacrow64bit.exe will
     fail to start.
     
If Data Crow fails to start try and run it "as administrator":
    -Right-click the EXE file or the shortcut (as created by the installer), select the
     properties tab and tick the option called "Run as administrator".

If you use multiple Data Crow installations you have several choices to make:

    -Use the Server version of Data Crow. Run this on one machine and connect to from 
     your other devices & machines. This is the best option.

    -Set up a user folder on a network share. Each of the Data Crow installations will
     point to this user folder. This is established by selecting the same user folder  
     on each of the Data Crow installations.

    -In case you run multiple Data Crow instances on one machine you can either have  
     them using the same user directory (sharing the same settings and data) or assign a 
     separate user folder to each of the installations (-userdir parameter).. 

    -In addition to the above you can share the user folder between the various 
     installations but have them using a different database per installation 
     (-db parameter).

The various parameters you can use are listed below:
    
    -client
     This has to be used in case you want to connect to a client.
    
    -db:<database name> 
     Forces Data Crow to use another database.
    
    -dir:<installation directory> 
     Use this parameter when Data Crow starts incorrectly and complains about missing 
     directories (non Windows platform only). 
     
    -userdir:<user directory> 
     Use this parameter to specify a specific user directory. Multiple instances
     of Data Crow can be started using this parameter. Additionally the selected user
     folder path will not be stored in the datacrow.properties file. You can also make
     the path relative to the Data Crow installation folder by supplying the path as
     './<folder name>'. Example: -userdir:./data
    
    -credentials:username/password
     Specify the login credentials to start Data Crow without displaying the login dialog.
     
    -nosplash
     Hides the splashscreen on startup.
     
    -debug
     For additional logging information.     
     
    -clearsettings
     Loads the default Data Crow settings. Disgards all user settings.
     

----------------------------------------------------------------------------------------
7.0 Credits

This application would not have succeeded (or even existed) without the help of:

* HSQL: This product includes Hypersonic SQL.
  Originally developed by Thomas Mueller and the Hypersonic SQL Group. 
  I want to thank Thomas Mueller for providing this application with an easy
  to use, powerful but small and platform independent database.
  http://hsqldb.sourceforge.net/
* http://www.freedb.org for allowing me to retrieve data from their web servers.
* http://musicbrainz.org for allowing me to retrieve Music Albums.
* Amazon.com for allowing me to retrieve data from their web servers.
  http://www.amazon.com
* Imdb.com for allowing me to extract data from their database.
  http://www.imdb.com
* JAudioTagger which I use to parse audio file information.
  http://www.jthink.net/jaudiotagger
* izPack for providing a smooth way to install Data Crow on multiple platforms.
  http://www.izforge.com/izpack/
* ISBNExtractor. Extracts ISBNs from PDF documents.
  http://isbnextractor.sourceforge.net.   


----------------------------------------------------------------------------------------
8.0 License

This program is free software: you can redistribute it and/or modify it under the terms 
of the GNU General Public License as published by the Free Software Foundation, 
either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
See the GNU General Public License for more details. 

You should have received a copy of the GNU General Public License along with this program. 
If not, see http://www.gnu.org/licenses.
