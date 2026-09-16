@echo off
setlocal
set "MAVEN_VERSION=3.9.9"
set "MAVEN_HOME=%~dp0.mvn\apache-maven-%MAVEN_VERSION%"
if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
  echo Descargando Maven %MAVEN_VERSION% localmente para este proyecto...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest 'https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip' -OutFile '%~dp0.mvn.zip'; Expand-Archive '%~dp0.mvn.zip' '%~dp0.mvn' -Force; Remove-Item '%~dp0.mvn.zip'"
)
call "%MAVEN_HOME%\bin\mvn.cmd" %*
