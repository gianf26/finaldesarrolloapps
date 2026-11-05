@echo off
setlocal
set "APP_HOME=%~dp0"
set "WRAPPER=%APP_HOME%gradle\wrapper\gradle-wrapper.jar"
if not exist "%WRAPPER%" (
  echo Descargando Gradle Wrapper oficial 8.11.1...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $file='%WRAPPER%'; Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-8.11.1-wrapper.jar' -OutFile $file; $sha=(Get-FileHash -Algorithm SHA256 $file).Hash.ToLower(); if ($sha -ne '2db75c40782f5e8ba1fc278a5574bab070adccb2d21ca5a6e5ed840888448046') { Remove-Item $file; throw 'Checksum del wrapper invalido' }"
  if errorlevel 1 exit /b 1
)
java -classpath "%WRAPPER%" org.gradle.wrapper.GradleWrapperMain %*
exit /b %ERRORLEVEL%
