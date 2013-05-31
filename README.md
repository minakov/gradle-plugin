Gradle plugin to deploys apks to AppThwack's online test farms.

A typical project build.gradle will look like this:

    buildscript {
        repositories {
            mavenCentral()
        }
        dependencies {
            classpath 'com.android.tools.build:gradle:0.4.2'
            classpath 'com.appthwack:gradle:1.0'
        }
    }
    
    apply plugin: 'android'
    apply plugin: 'appthwack'
    
    android {
        //...
    }
    
    appthwack {
    }


