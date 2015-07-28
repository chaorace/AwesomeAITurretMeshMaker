import java.io.PrintWriter


import scala.io.Source
import scala.swing._
import scala.collection.mutable.Map
import scala.swing.event.ButtonClicked
import scala.pickling.Defaults._
import scala.pickling.json._

/**
 * Created by Chris on 6/11/2015.
 */
class NodeCalibrationGUI(c: Config) extends Frame {
  peer.setAlwaysOnTop(true)
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
      contents += new BoxPanel(Orientation.Horizontal) {
        contents +=
          new Button("Generate!") {
            reactions += {
              case ButtonClicked(_) => {
                var m: Map[String, SPoint2D] = Map()
                nodeFields.foreach(x => {
                  m += (x._1 -> new SPoint2D(x._2._1.text.toDouble, x._2._2.text.toDouble))
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
        contents +=
          new Button("Save Config") {
            reactions += {
              case ButtonClicked(_) => {
                var m: Map[String, SPoint2D] = Map()
                nodeFields.foreach(x => {
                  m += (x._1 -> new SPoint2D(x._2._1.text.toDouble, x._2._2.text.toDouble))
                })
                val pkl = m.pickle
                fc.showSaveDialog(null)
                val outputFile = fc.selectedFile
                val writer = new PrintWriter(outputFile)
                writer.write(pkl.value)
                writer.close()
                Dialog.showMessage(null, "Success!")
              }
            }
          }
        contents +=
          new Button("Load Config") {
            reactions += {
              case ButtonClicked(_) => {
                fc.showOpenDialog(null)
                val inputFile = fc.selectedFile
                val m = Source.fromFile(inputFile).mkString.unpickle[Map[String, SPoint2D]]
                m.foreach(x => {
                  nodeFields.foreach(y => {
                    if (y._1 == x._1) {
                      y._2._1.text = x._2.x.toString
                      y._2._2.text = x._2.y.toString
                    }
                  })
                })
              }
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
                  rtbOffset: SPoint2D, rtfOffset: SPoint2D, btfOffset: SPoint2D, btbOffset: SPoint2D,
                  rbfOffset: SPoint2D, bbfOffset: SPoint2D, bbbOffset: SPoint2D)

object FormatType extends Enumeration {
  type FormatType = Value
  val Normal, Sorona, oldAI = Value
}