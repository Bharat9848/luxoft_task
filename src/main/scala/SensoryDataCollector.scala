import scala.collection.mutable

class SensoryDataCollector {
  import scala.collection.mutable.Map

  val sensorMap: Map[String, SensorData] = new mutable.HashMap
  var totalFiles:Int=0

  def addSensorData(sensor:String, data:Humidity) ={
    val sensorData = sensorMap.getOrElse(sensor, SensorData(sensor))
    val newData = data match {
      case HumidityValid(value) => {
        val max = Math.max(value, sensorData.max)
        val min = Math.min(value, sensorData.min)
        val dataTotal = sensorData.totalData + value
        val validCount = sensorData.validDataCount + 1
        SensorData(sensor, min, max, dataTotal, validCount, sensorData.invalidDataCount)
      }
      case HumidityInvalid => sensorData.copy(invalidDataCount = sensorData.invalidDataCount+1)
    }
    sensorMap.put(sensor, newData)
  }

  def incrementFileProcessingCount = totalFiles+=1
}
