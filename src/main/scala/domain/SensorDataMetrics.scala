package domain

case class SensorDataMetrics(id: String, min: Int = Integer.MAX_VALUE, max: Int = Integer.MIN_VALUE, totalData: Int = 0, validDataCount: Int = 0, invalidDataCount: Int = 0) {
  def getValidCount = ""

  def getMinimum: String = if (min == Integer.MAX_VALUE) "NaN" else min.toString

  def getMaximum: String = if (max == Integer.MIN_VALUE) "NaN" else max.toString

  def average: String = if (validDataCount > 0) {
    (totalData / validDataCount).toString
  } else {
    "NaN"
  }

  def merge(newSample: SensorDataMetrics): SensorDataMetrics = {
    val min = Math.min(this.min, newSample.min)
    val max = Math.max(this.max, newSample.max)
    val total = totalData + newSample.totalData
    val validCount = validDataCount + newSample.validDataCount
    val invalidCount = invalidDataCount + newSample.invalidDataCount
    SensorDataMetrics(id, min, max, total, validCount, invalidCount)
  }

}
