@echo off
cls
:start
title My Server
java -Xmx1512M -jar spigot-1.17.1.jar -nogui
set choice=
set /p choice="Do you want to restart? Press 'y' and enter for Yes: "
if not '%choice%'=='' set choice=%choice:~0,1%
if '%choice%'=='y' goto start