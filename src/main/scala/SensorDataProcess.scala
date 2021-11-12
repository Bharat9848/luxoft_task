import java.io.File
import java.nio.file.{Path, Paths}

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{FileIO, Flow, Framing, Keep, Sink, Source}
import akka.util.ByteString

import scala.io.StdIn.readLine

object SensorDataProcess extends App {
  implicit val system = ActorSystem("sensor-data-processing")
  println("Enter sensor data directory")
  val dataDirectory = readLine()
  val directory = Paths.get(dataDirectory).toFile
  val sensoryDataCollector = new SensoryDataCollector

  def fileToFileData(file:Path, sensoryDataCollector: SensoryDataCollector):Source[List[String], _] = {
    sensoryDataCollector.incrementFileProcessingCount
    FileIO.fromPath(file)
    .via(Framing.delimiter(ByteString("\n"), 256, true)
      .map(_.utf8String))
    .map(List(_))
    .drop(1)
  }

  def directoryToFileData(directory: File, collector: SensoryDataCollector):Source[String, NotUsed] = {
    val fileIterator = directory.listFiles(pathname => pathname.getName.endsWith(".csv")).toList.iterator

    Source.fromIterator[File](() => fileIterator)
      .flatMapConcat(file => fileToFileData(file.toPath, collector)).mapConcat(x => x)
  }

  def rawDataToHumidityData :Flow[String, (String, Humidity),_] = Flow[String].map(line => {
    val entry = line.split(",")
    (entry(0), Humidity.from(entry(1)))
  })

  def viaHumidityDataCollector(sensoryDataCollector: SensoryDataCollector) = Sink.fold[SensoryDataCollector, (String, Humidity)](sensoryDataCollector)(
    (collector, data) => {
      collector.addSensorData(data._1, data._2)
      collector
    })

  def dataCollectorGraph(collector: SensoryDataCollector, directory1: File) = directoryToFileData(directory1, collector)
    .via(rawDataToHumidityData)
    .toMat(viaHumidityDataCollector(collector))(Keep.right)

  val dataCollector = dataCollectorGraph(sensoryDataCollector, directory).run()

  dataCollector.foreach(collector => {
    val generator = new ReportGenerator(collector)
    println(generator.generateReport)
    system.terminate()
  })(system.getDispatcher)

}
