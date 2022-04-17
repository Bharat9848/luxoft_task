package report

import domain.SensorDataMetrics

class ReportGenerator(totalFiles: Int, sensorMetricsList: Iterable[SensorDataMetrics]) {
  def generateReport:String = {

    val measurement = sensorMetricsList.map(sdata => sdata.invalidDataCount + sdata.validDataCount).sum

    val failedMeasurement = sensorMetricsList.map(sdata => sdata.invalidDataCount).sum

    val sensorData = sensorMetricsList.map(metrics => {
      val sensorName = metrics.id
      s" $sensorName, ${metrics.getMinimum}, ${metrics.average}, ${metrics.getMaximum} \n"
    }).fold("")(_ + _)

    val headerData =
      s""" sum of processed files: $totalFiles
         | Num of processed measurements: $measurement
         | Num of failed measurements: $failedMeasurement
         | Sensors with highest avg humidity:
         |
         | sensor-id,min,avg,max
         |""".stripMargin
    headerData + sensorData
  }
}
