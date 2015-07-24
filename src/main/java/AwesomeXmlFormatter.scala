import java.awt.geom.Point2D
import scala.collection.mutable.Map
import scala.xml._


/**
 * Created by Chris on 6/9/2015.
 */
object AwesomeXmlFormatter {
  def formatNodes(nodesOffsets: Map[String, Point2D.Double], c: Config): String = {
    var firstCycle = true


    //build output


    //TODO:Generation logic

    def generateTurretSet(currOffset: Point2D.Double, t: Turret.Value, mult: Double): String = {
      """<condition id="isTurretInArea">""" + "\n" +
        """<string id="teams" values="teams" multiselect="true">OWN_TEAM;;ENEMY_TEAM;;</string>""" + "\n" +
        """<string id="health comparison" values="valuecompare">greater</string>""" + "\n" +
        """<float id="health">0.00</float>""" + "\n" +
        s"""<float id=\"xOffset\">${(currOffset.getX + Turret.getOffset(c, t).getX) * mult}</float>""" + "\n" +
        s"""<float id=\"yOffset\">${currOffset.getY + Turret.getOffset(c, t).getY}</float>""" + "\n" +
        """<float id="width">0.50</float>""" + "\n" +
        """<float id="height">0.75</float>""" + "\n" +
        """<string id="check line of sight" values="yesno">no</string>""" + "\n" +
        s"""<string id=\"Comment\">Is ${t} up?</string>""" + "\n" +
        """<normal>""" + "\n" +
        """<action id="setBool">""" + "\n" +
        s"""<string id=\"id\">Toolkit_MapInfo_Event_${t}</string>""" + "\n" +
        """<string id="value" values="flagtoggle">yes</string>""" + "\n" +
        s"""<string id=\"Comment\">${t} is up</string>""" + "\n" +
        """</action>""" + "\n" +
        """</normal>""" + "\n" +
        """<else>""" + "\n" +
        """<action id="setBool">""" + "\n" +
        s"""<string id=\"id\">Toolkit_MapInfo_Event_${t}</string>""" + "\n" +
        """<string id="value" values="flagtoggle">no</string>""" + "\n" +
        s"""<string id=\"Comment\">${t} is down</string>""" + "\n" +
        """</action>""" + "\n" +
        """</else>""" + "\n" +
        """</condition>""" + "\n"
    }


    def xmlLoop(remainingNodes: Map[String, Point2D.Double]): String = {


      //Recursion escape condition and special formatting
      if (remainingNodes.isEmpty) {
        return ""
      }

      var s0 = ""

      var s1 = ""


      c.formatType match {
        case FormatType.Normal => {
          Turret.values.foreach(x => generateTurretSet(remainingNodes.head._2, x, 1.0).foreach(x => s0 += x))
          Turret.values.foreach(x => generateTurretSet(remainingNodes.head._2, x, -1.0).foreach(x => s1 += x))
        }
        case FormatType.oldAI => {
          Turret.getOldAITurrets.foreach(x => generateTurretSet(remainingNodes.head._2, x, 1.0).foreach(x => s0 += x))
          Turret.getOldAITurrets.foreach(x => generateTurretSet(remainingNodes.head._2, x, -1.0).foreach(x => s1 += x))
        }
        case FormatType.Sorona => {
          Turret.getSoronaTurrets.foreach(x => generateTurretSet(remainingNodes.head._2, x, 1.0).foreach(x => s0 += x))
          Turret.getSoronaTurrets.foreach(x => generateTurretSet(remainingNodes.head._2, x, -1.0).foreach(x => s1 += x))
        }
      }


      var out = "%s"

      if (remainingNodes.nonEmpty & !firstCycle) {
        out = """<else>""" + "\n" + "%s" + """</else>""" + "\n"
      }
      if (firstCycle) firstCycle = false

      val in =
        """<andblock>""" + "\n" +
          """<string id="Comment">Am I at the checkpoint?</string>""" + "\n" +
          """<normal>""" + "\n" +
          """<condition id="getBoolEquals">""" + "\n" +
          """<string id="id">Toolkit_Detection_Event_DirectionFacing</string>""" + "\n" +
          """<string id="value" values="yesno">no</string>""" + "\n" +
          """<string id="Comment">Am I facing right?</string>""" + "\n" +
          """<string id="Minimized">yes</string>""" + "\n" +
          """<normal>""" + "\n" +
          s0 +
          """</normal>""" + "\n" +
          """<else>""" + "\n" +
          s1 +
          """</else>""" + "\n" +
          """</condition>""" + "\n" +
          """</normal>""" + "\n" +
          xmlLoop(remainingNodes.tail) +
          """<or>""" + "\n" +
          """<condition id="checkCounter">""" + "\n" +
          """<string id="id">Toolkit_WaypointPlus_Event_LastCheckpoint</string>""" + "\n" +
          s"""<string id="value">${remainingNodes.head._1}</string>""" + "\n" +
          """<string id="compare method" values="valuecompare">equal</string>""" + "\n" +
          s"""<string id="Comment">Was I at ${remainingNodes.head._1}?</string>""" + "\n" +
          """</condition>""" + "\n" +
          """<condition id="getBoolEquals">""" + "\n" +
          """<string id="id">Toolkit_WaypointPlus_Event_AtTargetWaypoint</string>""" + "\n" +
          """<string id="value" values="yesno">yes</string>""" + "\n" +
          """<string id="Comment">Am I at my previous checkpoint?</string>""" + "\n" +
          """</condition>""" + "\n" +
          """</or>""" + "\n" +
          """</andblock>""" + "\n"

      out.format(in)


    }

    //EOF boilerplate
    val output =
      "<?xml version=\"1.0\" ?>" + "\n" +
        "<enemy>" + "\n" +
        "<behaviour>" + "\n" +
        "<root x=\"110\" y=\"40\">" + "\n" +
        "<normal>" + "\n" + {
        xmlLoop(nodesOffsets)
      } +
        "</normal>" + "\n" +
        "</root>" + "\n" +
        "</behaviour>" + "\n" +
        "</enemy>"
    //return output
    output

  }
}

object Turret extends Enumeration {
  type Turret = Value
  val Turret10, Turret11, Turret12, Turret13, Turret20, Turret21, Turret22, Turret23 = Value

  val noOffset = new Point2D.Double(0.00, 0.00)

  def getOffset(c: Config, t: Turret.Value): Point2D.Double = t match {
    case Turret10 => c.rtbOffset
    case Turret11 => noOffset
    case Turret12 => c.rtfOffset
    case Turret13 => c.rbfOffset
    case Turret20 => c.btbOffset
    case Turret21 => c.bbbOffset
    case Turret22 => c.btfOffset
    case Turret23 => c.bbfOffset
  }

  def getSoronaTurrets() = List[Turret.Value](
    Turret11, Turret12, Turret13, Turret21, Turret22, Turret23
  )

  def getOldAITurrets() = List[Turret.Value](
    Turret10, Turret11, Turret13, Turret20, Turret21, Turret23
  )
}

