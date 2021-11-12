trait Humidity

case class HumidityValid(value: Int) extends Humidity

object HumidityInvalid extends Humidity

object Humidity {

  def from(data: String) = {data match {
    case "NaN" => HumidityInvalid
    case x => HumidityValid(x.trim.toInt)
  }
  }
}
