import akka.actor.ActorSystem
import akka.stream.scaladsl.{FileIO, Flow, Framing, Keep, Sink, Source}
import akka.util.ByteString
import domain.{GroupData, HumidityMeasurement, SensorHumiditySample, SensoryDataCollector}
import report.ReportGenerator

import java.io.File
import java.nio.file.{Files, OpenOption, Path, Paths, StandardOpenOption}
import scala.io.StdIn.readLine

object SensorDataProcess extends App {
  implicit val system = ActorSystem("sensor-data-processing")
  println("Enter sensor data directory")
  val dataDirectory = readLine()
  val directory = Paths.get(dataDirectory).toFile

  def directoryToFileData(directory: File) = {
    val fileIterator = directory.listFiles(pathname => pathname.getName.endsWith(".csv")).toList.iterator
    def fileToFileData(file:Path):Source[(String,String), _] = {
      FileIO.fromPath(file)
        .via(Framing.delimiter(ByteString("\n"), 256, true)
          .map(_.utf8String))
        .zip(Source.repeat[String](file.toFile.getName))
        .drop(1)
    }

    Source.fromIterator[File](() => fileIterator)
      .flatMapConcat(file => fileToFileData(file.toPath))
  }

  def rawDataToHumidityData :Flow[(String, String),GroupData,_] =
    Flow[(String, String)]
      .map(sampleToFileName => {
        val (sample, fileName) = sampleToFileName
        (fileName, SensorHumiditySample.fromSampleString(sample))
      })
      .filter(_._2.isDefined)
      .map(fileNameToSample => GroupData(fileNameToSample._1, fileNameToSample._2.get))

  def viaHumidityDataCollector = Sink.fold[SensoryDataCollector, GroupData](new SensoryDataCollector)(
    (collector, data) => {
      collector.addSensorData(data)
      collector
    })

  def dataCollectorGraph(dir: File) = directoryToFileData(dir)
    .via(rawDataToHumidityData)
    .toMat(viaHumidityDataCollector)(Keep.right)

  val dataCollector = dataCollectorGraph(directory).run()

  dataCollector.foreach(collector => {
    val sensorDataMetrics = collector.getAllMeasurements.values.toList.sortBy(metrics =>{
      if(metrics.totalData == 0){
        Integer.MAX_VALUE;
      }else {
        metrics.totalData/metrics.validDataCount
      }
    })
    val generator = new ReportGenerator(collector.getTotalFileCount, sensorDataMetrics)
    println(generator.generateReport)
    system.terminate()
  })(system.getDispatcher)

}
