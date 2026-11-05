import urllib.request
import zipfile
import os

os.makedirs('gradle/wrapper', exist_ok=True)
print('Downloading Gradle wrapper...')
url = 'https://services.gradle.org/distributions/gradle-8.4-bin.zip'
urllib.request.urlretrieve(url, 'gradle.zip')

print('Extracting wrapper jar...')
z = zipfile.ZipFile('gradle.zip')
jar_name = [f for f in z.namelist() if 'gradle-wrapper-' in f][0]
open('gradle/wrapper/gradle-wrapper.jar', 'wb').write(z.read(jar_name))
z.close()
os.remove('gradle.zip')

print('Writing properties file...')
props = """distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.4-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists"""
open('gradle/wrapper/gradle-wrapper.properties', 'w').write(props)

print('Writing gradlew.bat...')
bat_content = """@echo off
set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_HOME=%DIRNAME%
set CLASSPATH=%APP_HOME%\\gradle\\wrapper\\gradle-wrapper.jar
java -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
"""
open('gradlew.bat', 'w').write(bat_content)

print('Gradle wrapper generated successfully!')