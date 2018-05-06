# Top stepik courses

This application allows you to view most popular courses on Stepik.

## Build
To build, execute `build` and `fatJar` Gradle tasks.
The JAR will be located at /build/libs/stepikTop.jar.

## Usage
`java -jar /path/to/topStepik.jar`

Command line parameters:
1. `-help` : Display help
2. `<number>` : Display top <number> popular courses

Example:
`java -jar /path/to/topStepik.jar 5` - display top 5 courses