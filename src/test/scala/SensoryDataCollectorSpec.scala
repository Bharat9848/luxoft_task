import org.junit.{Assert, Test}

class SensoryDataCollectorSpec {

  @Test
  def testIncrementWithoutAnyFileProcessing= {
    val collector = new SensoryDataCollector
    Assert.assertEquals(0, collector.totalFiles)
  }
  @Test
  def testIncrementWithFileProcessing= {
    val collector = new SensoryDataCollector
    collector.incrementFileProcessingCount
    collector.incrementFileProcessingCount
    Assert.assertEquals(2, collector.totalFiles)
  }

  @Test
  def testAddSensoryDataWithoutAnyProcessing = {
    val collector = new SensoryDataCollector
    Assert.assertEquals(Map(), collector.sensorMap)
  }

  @Test
  def testAddSensoryDataWithValidDataProcessing = {
    val collector = new SensoryDataCollector
    collector.addSensorData("s1", HumidityValid(30))
    Assert.assertEquals(Map("s1" -> SensorData("s1", 30, 30, 30, 1, 0)), collector.sensorMap)
  }

  @Test
  def testAddSensoryDataWithInvalidDataProcessing = {
    val collector = new SensoryDataCollector
    collector.addSensorData("s1", HumidityInvalid)
    Assert.assertEquals(Map("s1" -> SensorData("s1", Integer.MAX_VALUE, Integer.MIN_VALUE, 0, 0, 1)), collector.sensorMap)
  }
}
