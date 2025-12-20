@echo off
echo Running Banking Application Test Suite...
echo.
java TestRunner.java
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Tests Failed!
    color 4F
) else (
    echo.
    echo Tests Passed!
    color 2F
)
pause
