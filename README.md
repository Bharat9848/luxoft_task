## Program Structure
This program uses Akka-Stream scala library to solve the problem. It breaks the problem in following stages
1. Filter csv files from input directory: This stage create File source stream after filtering only csv files from input directory. Then it converts file data bytes to raw data strings per line along with leader information.
2. Parse raw data: This stage parses the raw data from above stage and parses raw string to ```(leader, (sensor, domain.HumidityMeasurement))``` tuple.
3. Collect and group humidity data: This stage takes humidity data input from above stage. And it furthers group and collect the data in ```domain.SensoryDataCollector```. 
4. We create a linear graph with three stages defined in above three steps. We run the graph which return collector.
5. Print report: This is the final stage. It processes the collector data and sort it and then prints the output. 

## How to run program

Program is using sbt built tool. It is required to run the program. After setting up sbt, program can be run using following commands from program's base directory in a command prompt.
```
sbt
runMain SensorDataProcess 
``` 
#### Program behavior
After running the above commands, program will prompt for data directory as required in the problem statement.Then Program will process the input data directory and output the data report result as per the problem statement's output format and exit.
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

## Project structure and classes overview 
Directory structure is according to sbt project convention. Following list the source files and small overview about them 

 - src/main/scala
1. **domain.HumidityMeasurement.scala**  -  Case classes to represent domain.Humidity data
2. **report.ReportGenerator.scala** - Generates report
3. **SensorDataProcess.scala** - Main program takes input and triggers the Akka stream pipeline
4. **domain.SensorDataMetrics.scala** - case class to collect metrics for a sensor.
5. **domain.SensorHumiditySample.scala** - case class to represent single sensor humidity sample.
6. **domain.SensoryDataCollector.scala** - Keep all the sensors data
- src/test/integration
1. **IntegrationSpec.scala** - It tests the program against a big input file.
- src/test/resources/leaderData  
1. **leader1.csv** - Test data
2. **leader2.csv** - Test data
3. **leader3.txt** - Test data to ignore
- src/test/scala                 
1. **ReportGeneratorSpec.scala** - Unit test file for report.ReportGenerator
2. **SensoryDataCollectorSpec.scala** - Unit test file for domain.SensoryDataCollector
3. **SensoryDataProcessSpec.scala** - Unit test file for SensoryDataProcess 

