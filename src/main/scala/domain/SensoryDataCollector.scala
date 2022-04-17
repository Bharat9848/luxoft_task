package domain

import scala.collection.mutable

class SensoryDataCollector {
  import scala.collection.mutable.Map

  private val sensorMap: Map[String, SensorDataMetrics] = new mutable.HashMap
  private val totalLeaderFiles:mutable.HashSet[String] = new mutable.HashSet[String]()

  private def fromSample(sample: SensorHumiditySample) = {
    sample.data match {
      case HumidityInvalid => SensorDataMetrics(sample.id, invalidDataCount = 1)
      case HumidityValid(value) => SensorDataMetrics(sample.id, value, value, value, 1, 0)
    }
  }

  def addSensorData(groupData: GroupData) ={
    val leader = groupData.leader
    val sample = groupData.sample
    val newSensorMetrics = fromSample(sample)
    val sensorDataMetricsNew = sensorMap.get(sample.id).fold(newSensorMetrics)(old => old.merge(newSensorMetrics))
    totalLeaderFiles.add(leader)
    sensorMap.put(sample.id, sensorDataMetricsNew)
  }

  def getAllMeasurements = sensorMap

  def getTotalFileCount =  totalLeaderFiles.size
}
