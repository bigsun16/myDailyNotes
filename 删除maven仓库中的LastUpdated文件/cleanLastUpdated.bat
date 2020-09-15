@echo off
rem create by BoBoTeacher(zhangbo1@itcast.cn)
rem crazy coder
  
rem 这里写你的仓库路径
set REPOSITORY_PATH=%MAVEN_HOME%\repository
rem 正在搜索...
for /f "delims=" %%i in ('dir /b /s "%REPOSITORY_PATH%\*lastUpdated*"') do (
    del /s /q %%i
)
rem 搜索完毕
pause