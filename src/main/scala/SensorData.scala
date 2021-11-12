
case class SensorData(id:String, min:Int = Integer.MAX_VALUE, max:Int = Integer.MIN_VALUE, totalData:Int = 0, validDataCount: Int = 0, invalidDataCount:Int = 0){
  def getValidCount=""
  def getMinimum: String =if (min == Integer.MAX_VALUE) "NaN" else min.toString
  def getMaximum: String = if (max == Integer.MIN_VALUE) "NaN" else max.toString
  def average: String = if (validDataCount > 0) {
    (totalData / validDataCount).toString
  } else {
    "NaN"
  }
}

case class HumidityData(id:String, data: Humidity)


