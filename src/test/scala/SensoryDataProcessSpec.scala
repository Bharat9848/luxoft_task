import akka.actor.ActorSystem
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.testkit.TestProbe
import domain.{GroupData, HumidityInvalid, HumidityMeasurement, HumidityValid, SensorDataMetrics, SensorHumiditySample, SensoryDataCollector}
import org.junit.Assert.assertEquals
import org.junit.Test

import java.nio.file.Paths
import java.util.concurrent.TimeUnit
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
    val expectedSet = Set(("s2,80", "leader1.csv"), ("s3,NaN", "leader1.csv"), ("s2,78", "leader1.csv"),("s1,98", "leader1.csv"), ("s1,10", "leader2.csv"), ("s2,88", "leader2.csv"), ("s1,NaN", "leader2.csv"))
    val fileRows = SensorDataProcess.directoryToFileData(directory)

    val collectorSink = Sink.fold[Set[(String,String)], (String, String)](Set())((list, tuple) => list + tuple)
    val graph = fileRows.toMat(collectorSink)(Keep.right)
    val filenameFuture = graph.run()
    val actualSet = extractValue(filenameFuture)
    assertEquals(expectedSet, actualSet)
  }

  @Test
  def testFromRawLineToHumidityData = {
    val file1Info = List(("s2,80", "leader1.csv"),("s3,NaN","leader1.csv"),("s2,78","leader1.csv"),("s1,98", "leader1.csv"))
    val file2Info = List(("s1,10","leader2.csv"), ("s2,88","leader2.csv"), ("s1,NaN","leader2.csv"))
    val dataFlowUnderTest = SensorDataProcess.rawDataToHumidityData.toMat(Sink.fold[Set[GroupData], GroupData](Set())((list,groupData) => list + groupData))(Keep.right)
    val humidityDataFuture = Source(file1Info ++ file2Info).runWith(dataFlowUnderTest)
    val actualTuple = extractValue(humidityDataFuture)
    val expectedTuple =  Set(GroupData("leader1.csv", SensorHumiditySample("s2", HumidityValid(80))), GroupData("leader1.csv", SensorHumiditySample("s3", HumidityInvalid)), GroupData("leader1.csv", SensorHumiditySample("s2", HumidityValid(78))),GroupData("leader1.csv", SensorHumiditySample("s1", HumidityValid(98))), GroupData("leader2.csv", SensorHumiditySample("s1", HumidityValid(10))), GroupData("leader2.csv", SensorHumiditySample("s2", HumidityValid(88))), GroupData("leader2.csv", SensorHumiditySample("s1", HumidityInvalid)))
    assertEquals(expectedTuple, actualTuple)
  }

  @Test
  def testFromRawLineToHumidityInvalidData = {
    val rawCsvData = ("s1,NaN", "Leader1.csv")
    val expectedTuple = GroupData("Leader1.csv", SensorHumiditySample("s1", HumidityInvalid))
    val dataFlowUnderTest = SensorDataProcess.rawDataToHumidityData.toMat(Sink.head)(Keep.right)

    val humidityDataFuture = Source.single(rawCsvData).runWith(dataFlowUnderTest)
    val actualTuple = extractValue(humidityDataFuture)
    assertEquals(expectedTuple, actualTuple)
  }

  @Test
  def testHumidityDataToCollector = {
    val input = Source(Set(GroupData("leader1.csv", SensorHumiditySample("s2", HumidityValid(80))), GroupData("leader1.csv", SensorHumiditySample("s3", HumidityInvalid)), GroupData("leader1.csv", SensorHumiditySample("s2", HumidityValid(78))),GroupData("leader1.csv", SensorHumiditySample("s1", HumidityValid(98))), GroupData("leader2.csv", SensorHumiditySample("s1", HumidityValid(10))), GroupData("leader2.csv", SensorHumiditySample("s2", HumidityValid(88))), GroupData("leader2.csv", SensorHumiditySample("s1", HumidityInvalid))))
    val collectorFuture = input.runWith(SensorDataProcess.viaHumidityDataCollector)
    val collector = extractValue(collectorFuture)
    assertEquals(Map("s1"-> SensorDataMetrics("s1", 10, 98, 108, 2, 1), "s2" -> SensorDataMetrics("s2", 78, 88, 246, 3, 0), "s3" -> SensorDataMetrics("s3", Integer.MAX_VALUE, Integer.MIN_VALUE, 0, 0, 1)), collector.getAllMeasurements)
  }


}
