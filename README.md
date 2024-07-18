# sturgeon-gui

Graphical User Interface for the [Sturgeon application](https://github.com/princessmaximacenter/sturgeon)

### How to install
Requirements:
- Java 11
- Maven
- Sturgeon (if you use this outside the production environment 
you might want to doublecheck src/main/resources/Config.yml if everything is aligning with your situation)

Build jar file:
```
# cd to sturgeon-gui folder and run the following:
mvn clean compile install
# Jar file can be found in the target folder and you can run it by:
java -jar target/sturgeon-gui-1.0.0.jar
```

### Contact
Maintained by the Translation Bioinformatics group
Developer: Alex Janse