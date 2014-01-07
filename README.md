# gradle-plugin

Plugin to integrate [AppThwack](https://appthwack.com) with [Gradle](http://www.gradle.org/) and [Android Studio](http://developer.android.com/sdk/installing/studio.html).

Status
======

Initial 1.3 release.

Installation
============

### build.gradle

A typical build.gradle file for Android Studio will look like this:

    buildscript {
        repositories {
            mavenCentral()
            mavenLocal()
        }
        dependencies {
            classpath 'com.android.tools.build:gradle:0.5.4'

            //Jersey explicit requirement due to Groovy compiler + Java refection issues.
            classpath 'com.sun.jersey:jersey-core:1.8'
            classpath 'com.appthwack:gradle:1.3'
        }
    }

    apply plugin: 'android'
    apply plugin: 'appthwack'

    dependencies {
        //...
    }

    android {
        //...
    }

    appthwack {
        //See 'Configuration' section for more details.
    }

Usage
=====

### Android Studio

1.)  Click Run > Edit Configurations.

2.)  Click the green '+' in the top left corner and select 'Gradle'.

3.)  In the 'Name' field, enter the text: 'Run Tests on AppThwack'.

4.)  In the 'Gradle project' field, enter the fully-qualified path to the top level directory of your Android Studio project.

5.)  In the 'Tasks' field, enter the text: 'appthwackUpload'.

6.)  Hit 'OK'.

7.)  Select 'Run Tests on AppThwack' from the build configuration drop down.

8.)  Hit play and watch it go.

9.)  The task will print out some helpful information and a URL where the scheduled run can be viewed.


### CLI

1.)  cd path/to/my/android/studio/project

2.)  ./gradlew appthwackUpload

Configuration
=============

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
        devicePool='Top 10 devices'
        explorer()
    }

Extension Fields
=================

### API Key

Name: apiKey  
Description: API Key of your AppThwack account. See [profile](https://appthwack.com/user/profile) page for details.  
Usage: Required  
Example:

    appthwack {
        apiKey="DTOZZNWeCNWFWtuqqJEm14nnonVJMDXA9flmdvzg"
    }

### Project

Name: project  
Description: Name of the AppThwack project to use.  
Usage: Required  
Example:

    appthwack {
        //...
        project='demoproject'
    }

### Type

Name: type  
Description: Type of test to schedule.  
Usage: Optional, default: "junit"  
Example:

    appthwack {
        //...
        type='junit'
    }

### DevicePool

Name: devicePool  
Description: Name of the AppThwack device pool to use.  
Usage: Optional, default: "Top 10 devices"
Example:

    appthwack {
        //...
        devicePool='Top 25 devices'
    }

### Calabash

Name: calabash  
Description: Function to schedule Calabash tests. Note: This sets type="calabash".  
Usage: Optional  
Example:

    appthwack {
        //...
        calabash("/path/to/calabash/features.zip")
    }

### AppExplorer

Name: explorer  
Description: Function to schedule AppExplorer tests. Note: This sets type="appexplorer".  
Usage: Optional  
Example:

    appthwack {
        //...
        explorer()
    }

Dependencies
============

This project uses the [appthwack-java](https://github.com/appthwack/appthwack-java) client.

Issues
======

Have a question/comment/problem with the Gradle/Android Studio/AppThwack?
Open a Github [Issue](https://github.com/appthwack/gradle-plugin/issues) or file a ticket on [UserVoice](http://appthwack.uservoice.com/) and we'll get back to you as soon as we can.
