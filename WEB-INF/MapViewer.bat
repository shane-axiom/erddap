@echo off
rem usage: MapViewer <in>   
rem <in> must be the complete directory + name + extension of a
rem .hdf, .nc, or .grd gridded data file, where the x and y values represent
rem lon and lat. 
rem The file can be zipped, but the name of the .zip file must be the
rem name of the data file + ".zip".
rem 
rem
rem This script reads the data, plots it on a map, and pops up a JFrame
rem to display the image.
rem To make this Linux/Unix script executable, use "chmod +x MapViewer".
rem
rem 2006-03-14
rem CoastWatch/Bob Simons  bob.simons@noaa.gov
rem

set thisDir=%~dp0
java -cp %thisDir%/classes;%thisDir%/lib/netcdfAll-latest.jar;%thisDir%/lib/itext-1.3.1.jar;%thisDir%/lib/activation.jar;%thisDir%/lib/activation.jar;%thisDir%/lib/lucene-core.jar;%thisDir%/lib/mail.jar -Xms1000M -Xmx1000M gov.noaa.pfel.coastwatch.sgt.SgtMap %1 %2 %3 %4

rem finished 

