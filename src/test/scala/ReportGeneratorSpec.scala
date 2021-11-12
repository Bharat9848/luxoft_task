import org.junit.{Assert, Test}

class ReportGeneratorSpec {

  @Test
  def testReportFormatWithoutData() = {

    val collector = new SensoryDataCollector
    val generator = new ReportGenerator(collector)
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

    val collector = new SensoryDataCollector
    collector.incrementFileProcessingCount
    collector.addSensorData("s1", HumidityValid(20))
    collector.addSensorData("s1", HumidityValid(30))
    collector.addSensorData("s1", HumidityValid(40))
    collector.addSensorData("s1",HumidityInvalid)
    val generator = new ReportGenerator(collector)
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
