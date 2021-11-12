## Program Structure
Program is using Akka-Stream scala library to solve the problem. To solve the problem it breaks the problem statement in following stages
1. Filter csv files from input directory: This stage create File source stream after filtering only csv file from input directory.
2. Read file raw data: This stage takes input from above file stream and convert file data bytes to raw data string.
3. Parse raw data: This stage parses the raw data from above stage and parses raw string to ```(sensor, Humidity)``` tuple.
4. Collect and group humidity data: This stage takes humidity data input from above stage. And it furthers group and collect the data in ```SensoryDataCollector```.
5. Print report: This is the final stage. It processes the collector from above stage and prints the output 

## How to run program

Program is using sbt built tool. Sbt build tool is required to run the program. After sbt setup, program can be run using following command after opening program's base directory in a command prompt.
```
sbt
runMain SensorDataProcess 
``` 
#### Program behavior
After running the above commands, program will prompt for data directory as required in problem statement. After taking input data directory program will output the data report result as per the problem statement 's output format and then exit.
#### Run program against test sample 
To run the program against sample data files, use the following commands
```
sbt
runMain SensorDataProcess
Enter sensor data directory
./src/test/resources/leaderData
...
<print output>
...
```

##Project structure and classes overview 
Directory structure is according to sbt project convention. Following list the source files and small overview about them 

 - src/main/scala
1. **Humidity.scala**  -  Case classes to represent Humidity data
2. **ReportGenerator.scala** - Generates report
3. **SensorDataProcess.scala** - Main program takes input and triggers the Akka stream pipeline
4. **SensorData.scala** - case class representing sensor state
5. **SensoryDataCollector.scala** - Data structure wrapper class to collect sensor data
- src/test/integration 
1. **IntegrationSpec.scala** - It test the program against a big input file.
- src/test/resources/leaderData  
1. **leader1.csv** - Test data
2. **leader2.csv** - Test data
3. **leader3.txt** - Test data to ignore
- src/test/scala                 
1. **ReportGeneratorSpec.scala** - Unit test file for ReportGenerator
2. **SensoryDataCollectorSpec.scala** - Unit test file for SensoryDataCollector
3. **SensoryDataProcessSpec.scala** - Unit test file for SensoryDataProcess 

