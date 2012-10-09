@echo off
cd ..
runtime\bin\python\python_mcp runtime\recompile.py %*
runtime\bin\python\python_mcp runtime\reobfuscate.py %*
cd tool
..\runtime\bin\python\python_mcp package.py %*
pause
