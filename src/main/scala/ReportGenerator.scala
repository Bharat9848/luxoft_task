class ReportGenerator(collector: SensoryDataCollector) {


  def generateReport:String = {
    val statsData = collector.sensorMap

    val measurement = statsData.values.map(sdata => sdata.invalidDataCount + sdata.validDataCount).sum

    val failedMeasurement = statsData.values.map(sdata => sdata.invalidDataCount).sum

    val sensorData = statsData.toList.map(sensorVals => {
      val data = sensorVals._2
      val sensorName = sensorVals._1
      s" $sensorName, ${data.getMinimum}, ${data.average}, ${data.getMaximum} \n"
    }).fold("")(_ + _)

    val headerData =
      s""" sum of processed files: ${collector.totalFiles}
         | Num of processed measurements: $measurement
         | Num of failed measurements: $failedMeasurement
         | Sensors with highest avg humidity:
         |
         | sensor-id,min,avg,max
         |""".stripMargin
    headerData + sensorData
  }
}
