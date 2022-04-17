package domain

sealed trait HumidityMeasurement

case class HumidityValid(value: Int) extends HumidityMeasurement

object HumidityInvalid extends HumidityMeasurement

object HumidityMeasurement {

  def from(data: String) = {
    data match {
      case "NaN" => HumidityInvalid
      case x => HumidityValid(x.trim.toInt)
    }
  }
}


