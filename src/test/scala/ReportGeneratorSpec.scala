import domain.{HumidityInvalid, HumidityValid, SensorDataMetrics, SensoryDataCollector}
import org.junit.{Assert, Test}
import report.ReportGenerator

class ReportGeneratorSpec {

  @Test
  def testReportFormatWithoutData() = {

    val generator = new ReportGenerator(0, List())
    val expected = """ sum of processed files: 0
                     | Num of processed measurements: 0
                     | Num of failed measurements: 0
                     | Sensors with highest avg humidity:
                     |
                     | sensor-id,min,avg,max
                     |""".stripMargin
    Assert.assertEquals(expected, generator.generateReport)
  }

  @Test
  def testReportFormatWithData() = {

    val metrics = SensorDataMetrics("s1", 20, 40, 90, 3, 1)
    val generator = new ReportGenerator(1, List(metrics) )
    val expected = """ sum of processed files: 1
                     | Num of processed measurements: 4
                     | Num of failed measurements: 1
                     | Sensors with highest avg humidity:
                     |
                     | sensor-id,min,avg,max
                     | s1, 20, 30, 40 
                     |""".stripMargin
    Assert.assertEquals(expected, generator.generateReport)
  }

}
