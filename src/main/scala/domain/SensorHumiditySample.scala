package domain

case class SensorHumiditySample(id:String, data: HumidityMeasurement)
object SensorHumiditySample{
  def fromSampleString(sampleString: String): Option[SensorHumiditySample] = {
    val sensorToReading = sampleString.split(",")
    if (sensorToReading.length == 2) {
      Some(SensorHumiditySample(sensorToReading(0), HumidityMeasurement.from(sensorToReading(1))))
    } else {
      None
    }
  }
}

case class GroupData(leader: String, sample: SensorHumiditySample)
