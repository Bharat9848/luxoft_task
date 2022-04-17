import akka.actor.ActorSystem
import akka.stream.scaladsl.{FileIO, Source}
import akka.testkit.TestKit
import akka.util.ByteString
import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.Test

import java.io.File
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Random

class IntegrationSpec extends TestKit(ActorSystem("IntegrationSpec")){

    @Test
    def testBigFile = {
      val filePath: String = createTestFile
      val csvDataFile = new File(filePath)
      assertTrue(csvDataFile.exists())
      val random = new Random(68)
      val dataWroteFuture = Source(1 to 10000000)
        .map(_ => ByteString("s1," + random.nextInt(100) + "\n"))
        .runWith(FileIO.toPath(Paths.get(filePath)))

      val ioResult = Await.result(dataWroteFuture, Duration(5, TimeUnit.MINUTES))
      assertTrue(ioResult.status.isSuccess)

      val collectorFut  = SensorDataProcess.dataCollectorGraph(new File(".")).run

      val collector = Await.result(collectorFut, Duration(1, TimeUnit.MINUTES))
      assertEquals(1, collector.getTotalFileCount)
      assertTrue(csvDataFile.delete())
    }

  private def createTestFile = {
    val filePath = "./Leader21.csv"
    val bigCsvFile = new File(filePath)
    bigCsvFile.createNewFile()
    filePath
  }
}
