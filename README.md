# gradle-plugin

Plugin to integrate [AppThwack](https://appthwack.com) with [Gradle](http://www.gradle.org/) and [Android Studio](http://developer.android.com/sdk/installing/studio.html).


Status
======

Currently under active development.


Installation
============

### build.gradle

A typical build.gradle file for Android Studio will look like this:
**Note: The AppThwack specific sections are in bold.**

    buildscript {
        repositories {
            mavenCentral()
            mavenLocal()
        }
        dependencies {
            classpath 'com.android.tools.build:gradle:0.5.4'

            //Jersey explicit requirement due to Groovy compiler + Java refection issues.
            **classpath 'com.sun.jersey:jersey-core:1.8'**
            **classpath 'com.appthwack:gradle:1.0'**
        }
    }

    apply plugin: 'android'
    **apply plugin: 'appthwack'**

    dependencies {
        //...
    }

    android {
        //...
    }

    **appthwack** {
        //See 'Usage' section for more details.
    }


Usage
=====

### Schedule JUnit/Robotium tests.

    appthwack {
        apiKey='...'
        type='junit'
        project='My Project Name'
    }

### Schedule Calabash tests.

    appthwack {
        apiKey='...'
        project='My Project Name'
        devicePool='B&N Nooks'
        calabash('/src/AndroidApp/tests/calabash/features.zip')
    }

### Schedule AppExplorer tests.

    appthwack {
        apiKey='...'
        project='Demo Project'
        devicePool='Top 10'
        explorer()
    }


Extension Fields
=================

## Required
***

### API Key

Name: apiKey
Description: API Key of your AppThwack account. See [profile](https://appthwack.com/user/profile) page for details.
Example:
    apiKey="DTOZZNWeCNWFWtuqqJEm14nnonVJMDXA9flmdvzg"

### Project

Name: project
Description: Name of the AppThwack project to use.
Example:
    project="demoproject"

### Type

Name: type
Description: Type of test to schedule.
Example:
    type="junit"

## Optional
***

### DevicePool

Name: devicePool
Description: Name of the AppThwack device pool to use.
Example:
    devicePool="Top 25"

### Calabash

Name: calabash
Description: Function to schedule Calabash tests. Note: This sets type="calabash".
Example:
    calabash("/path/to/calabash/features.zip")

### AppExplorer

Name: explorer
Description: Function to schedule AppExplorer tests. Note: This sets type="appexplorer".
Example:
    explorer()


Dependencies
============

This project uses the [appthwack-java](https://github.com/appthwack/appthwack-java) client.
