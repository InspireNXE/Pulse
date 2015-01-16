Pulse [![Build Status](https://travis-ci.org/InspireNXE/Pulse.png?branch=master)](https://travis-ci.org/InspireNXE/Pulse) [![Coverage Status](https://coveralls.io/repos/InspireNXE/Pulse/badge.png?branch=master)](https://coveralls.io/r/InspireNXE/Pulse?branch=master)  
=============
Pulse is an open-source Minecraft server written in Java that implements Sponge API. It is licensed under the [MIT License].

* [Source]
* [Issues]
* Chat: #pulse on irc.esper.net

## Team
[![Zidane](https://secure.gravatar.com/avatar/3b8d6171c3f15daf35328a4f04c83de9?s=48)](https://github.com/Zidane "Zidane, Lead Developer")
[![Grinch](https://secure.gravatar.com/avatar/19d97d07c8797464aa8b7e2e0481da78?s=48)](https://github.com/Grinch "Grinch, Developer")

## Prerequisites
* [Java] `>=` 8
* [Gradle] `>=` 2.2

## Cloning
If you are using Git, use this command to clone the project: `git clone git@github.com:InspireNXE/Pulse.git`

## Setup
__For [Eclipse]__  
1. Make sure you have the Gradle plugin installed (Help > Eclipse Marketplace > Gradle Integration Plugin)  
2. Import Pulse as a Gradle project (File > Import)  
3. Select the root folder for Pulse and click 'Build Model'  
4. Check Pulse when it finishes building and click 'Finish'  

__For [IntelliJ]__  
1. Make sure you have the Gradle plugin enabled (File > Settings > Plugins).  
2. Click File > Import Module and select the 'build.gradle' file for Pulse.  

## Building
__Note:__ If you do not have [Gradle] installed you can use the gradlew files included with the project in place of 'gradle' in the following command(s). If you are using Git Bash, Unix or OS X then use './gradlew'. If you are using Windows then use 'gradlew.bat'.

In order to build Pulse you simply need to run the `gradle` command. You can find the compiled JAR file in `~/build/shaded`.

## Running
After building, you can go to `~/build/shaded` and run the command `java -jar pulse-1.0.0-SNAPSHOT.jar`.

## Contributing
Are you a talented programmer looking to contribute some code? We'd love the help!  
* Open a pull request with your changes, following our [guidelines and coding standards](CONTRIBUTING.md).  
* Please follow the above guidelines for your pull request(s) to be accepted.  
* For help setting up the project, keep reading!  

## FAQ
__How does the version system work for Pulse?__
>Our version outputs similarly to `1.0.0-SNAPSHOT.b123`. This can be dissected into `{VERSION}.b{BUILD_NUMBER}`.

__A dependency was added, but my IDE is missing it! How do I add it?__
>If a new dependency was added you can simply restart your IDE and it should pull in new dependencies.

__Help! Things are not working!__
>Some issues can be resolved by deleting the '.gradle' folder in your user directory and running through the setup steps again. Otherwise if you are having trouble with something that the README does not cover, feel free to join our IRC channel and ask for assistance.

[MIT License]: http://www.tldrlegal.com/license/mit-license/
[Source]: https://github.com/InspireNXE/Pulse/
[Issues]: https://github.com/InspireNXE/Pulse/issues
[Java]: http://java.oracle.com
[Gradle]: http://www.gradle.org/
[Eclipse]: http://www.eclipse.org/
[IntelliJ]: http://www.jetbrains.com/idea/