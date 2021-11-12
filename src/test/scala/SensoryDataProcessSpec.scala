import java.nio.file.Paths
import java.util.concurrent.TimeUnit

import akka.Done
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.testkit.TestProbe
import org.junit.Assert.assertEquals
import org.junit.{Assert, Test}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class SensoryDataProcessSpec {

  implicit val system = ActorSystem("testSystem")
  val probe = TestProbe()

  private def extractValue[T](collectorFuture: Future[T]) = {
    Await.result(collectorFuture, Duration.apply(500, TimeUnit.MILLISECONDS))
  }
  @Test
  def testReadCsvData = {
    val directory = Paths.get("./src/test/resources/leaderData").normalize().toAbsolutePath.toFile
    val expectedSet = Set("s2,88", "s1,NaN", "s2,78", "s1,10", "s1,98", "s2,80", "s3,NaN")

    val fileRows = SensorDataProcess.directoryToFileData(directory, new SensoryDataCollector)

    val value = Sink.fold[Set[String], String](Set())(_ + _)
    val graph = fileRows.toMat(value)(Keep.right)
    val filenameFuture = graph.run()
    val actualSet = extractValue(filenameFuture)
    assertEquals(expectedSet, actualSet)
  }

  @Test
  def testFromRawLineToHumidityData = {
    val rawCsvData = "s1,50"
    val expectedTuple = (("s1", HumidityValid(50)))
    val dataFlowUnderTest = SensorDataProcess.rawDataToHumidityData.toMat(Sink.head)(Keep.right)

    val humidityDataFuture = Source.single(rawCsvData).runWith(dataFlowUnderTest)
    val actualTuple = extractValue(humidityDataFuture)
    assertEquals(expectedTuple, actualTuple)
  }

  @Test
  def testFromRawLineToHumidityInvalidData = {
    val rawCsvData = "s1,NaN"
    val expectedTuple = (("s1", HumidityInvalid))
    val dataFlowUnderTest = SensorDataProcess.rawDataToHumidityData.toMat(Sink.head)(Keep.right)

    val humidityDataFuture = Source.single(rawCsvData).runWith(dataFlowUnderTest)
    val actualTuple = extractValue(humidityDataFuture)
    assertEquals(expectedTuple, actualTuple)
  }

  @Test
  def testHumidityDataToCollector = {
    val input = Source[(String, Humidity)](("s1", HumidityValid(20)) ::("s1", HumidityValid(30)):: ("s1", HumidityValid(40))::("s1",HumidityInvalid):: Nil)
    val collectorFuture = input.runWith(SensorDataProcess.viaHumidityDataCollector(new SensoryDataCollector))
    val collector = extractValue(collectorFuture)
    assertEquals(Map("s1"-> SensorData("s1", 20, 40, 90, 3, 1)), collector.sensorMap)
  }


}
