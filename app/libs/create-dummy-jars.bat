@echo off
echo Creating dummy JAR files for LSPosed dependencies...

echo Manifest-Version: 1.0 > manifest.txt
echo Created-By: Cascade > manifest.txt

jar cvfm dummy-api.jar manifest.txt
jar cvfm dummy-service.jar manifest.txt

echo Dummy JAR files created successfully!
del manifest.txt