import java.awt.geom.Point2D
import java.io.PrintWriter


import scala.swing._
import scala.collection.mutable.Map
import scala.swing.event.ButtonClicked

/**
 * Created by Chris on 6/11/2015.
 */
class NodeCalibrationGUI(c: Config) extends Frame {
  val fc = new FileChooser()
  contents = new ScrollPane() {
    contents = new BoxPanel(Orientation.Vertical) {
      var nodeFields: Map[String, (TextField, TextField)] = Map()
      c.nodes.foreach(x => {
        contents += new BoxPanel(Orientation.Horizontal) {
          contents += new Label(x) {
            preferredSize = new Dimension(200, 20)
          }
          val t = new TextField
          val t2 = new TextField
          nodeFields += (x ->(t, t2))
          contents += t
          contents += t2
        }
      })
      contents += new Button("Generate!") {
        reactions += {
          case ButtonClicked(_) => {
            var m: Map[String, Point2D.Double] = Map()
            nodeFields.foreach(x => {
              m += (x._1 -> new Point2D.Double(x._2._1.text.toDouble, x._2._2.text.toDouble))
            })
            fc.showSaveDialog(null)
            val outputFile = fc.selectedFile
            outputFile.createNewFile()
            val writer = new PrintWriter(outputFile)
            AwesomeXmlFormatter.formatNodes(m, c).foreach(x => {
              writer.write(x)
              writer.flush() //I really don't like this hack, but Java's printing libraries are so unnecessarily arcane and dated *shrug*
            })
            writer.close()
            Dialog.showMessage(null, "Success!")
            this.visible = false
          }
        }
      }
    }
  }
  preferredSize = new Dimension(400, 800)
  pack()
  visible = true
}

case class Config(nodes: Array[String], formatType: FormatType.Value,
                  rtbOffset: Point2D.Double, rtfOffset: Point2D.Double, btfOffset: Point2D.Double, btbOffset: Point2D.Double,
                  rbfOffset: Point2D.Double, bbfOffset: Point2D.Double, bbbOffset: Point2D.Double)

object FormatType extends Enumeration {
  type FormatType = Value
  val Normal, Sorona, oldAI = Value
}