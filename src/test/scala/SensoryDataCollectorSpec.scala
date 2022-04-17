import domain.{GroupData, HumidityInvalid, HumidityValid, SensorDataMetrics, SensorHumiditySample, SensoryDataCollector}
import org.junit.{Assert, Test}

class SensoryDataCollectorSpec {

  @Test
  def testIncrementWithoutAnyFileProcessing= {
    val collector = new SensoryDataCollector
    Assert.assertEquals(0, collector.getTotalFileCount)
  }
  @Test
  def testIncrementWithFileProcessing= {
    val collector = new SensoryDataCollector
    collector.addSensorData(GroupData("Leader1", SensorHumiditySample("s1", HumidityInvalid)))
    collector.addSensorData(GroupData("Leader2", SensorHumiditySample("s1", HumidityInvalid)))
    Assert.assertEquals(2, collector.getTotalFileCount)
  }

  @Test
  def testAddSensoryDataWithoutAnyProcessing = {
    val collector = new SensoryDataCollector
    Assert.assertEquals(Map(), collector.getAllMeasurements)
  }

  @Test
  def testAddSensoryDataWithValidDataProcessing = {
    val collector = new SensoryDataCollector
    collector.addSensorData(GroupData("leader1", SensorHumiditySample("s1", HumidityValid(30))))
    Assert.assertEquals(Map("s1" -> SensorDataMetrics("s1", 30, 30, 30, 1, 0)), collector.getAllMeasurements)
  }

  @Test
  def testAddSensoryDataWithInvalidDataProcessing = {
    val collector = new SensoryDataCollector
    collector.addSensorData(GroupData("leader1", SensorHumiditySample("s1", HumidityInvalid)))
    Assert.assertEquals(Map("s1" -> SensorDataMetrics("s1", Integer.MAX_VALUE, Integer.MIN_VALUE, 0, 0, 1)), collector.getAllMeasurements)
  }
}
