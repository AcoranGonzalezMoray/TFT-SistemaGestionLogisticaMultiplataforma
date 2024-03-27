@echo off
SETLOCAL EnableDelayedExpansion

:set_defaults
set "sql_checked=[X]"
set "asp_checked=[X]"
set "angular_checked=[X]"

:menu
cls
echo --------------------------------------------------------
echo                     MENÚ DE PROYECTOS
echo --------------------------------------------------------
echo [1] Iniciar SQL Server !sql_checked!
echo [2] Iniciar proyecto ASP.NET API !asp_checked!
echo [3] Iniciar proyecto Angular !angular_checked!
echo [4] Salir
echo --------------------------------------------------------
set /p option=Ingrese el número de la opción deseada: 

if "%option%"=="1" goto start_sql_server
if "%option%"=="2" goto start_asp_net_api
if "%option%"=="3" goto start_angular
if "%option%"=="4" goto :end

:start_sql_server
echo Iniciando SQL Server...
start cmd /k net start MSSQLSERVER
set "sql_checked=[✓]"
goto menu

:start_asp_net_api
echo Iniciando proyecto ASP.NET API...
cd ./QRStockMateSL/QRStockMate/
start cmd /k dotnet run --launch-profile https
start "" "https://localhost:7220/swagger"
set "asp_checked=[✓]"
pause
goto menu

:start_angular
echo Iniciando proyecto Angular...
cd ../../QRStockMateWeb/src
start cmd /k ng serve -o
set "angular_checked=[✓]"
pause
goto menu

:end
taskkill /F /T /PID %PPID%
exit /b
