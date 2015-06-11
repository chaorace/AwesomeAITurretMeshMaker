import java.awt.geom.Point2D

import scala.swing._
import scala.collection.mutable.Map

/**
 * Created by Chris on 6/11/2015.
 */
class NodeCalibrationGUI(c: Config) extends Frame {
  contents = new ScrollPane() {
    contents = new BoxPanel(Orientation.Vertical) {
      var nodeFields: Map[String, (TextField, TextField)] = Map()
      c.nodes.foreach(x => {
        contents += new BoxPanel(Orientation.Horizontal) {
          contents += new Label(x) {
            preferredSize = new Dimension(200, 20)
          }
          val t = new TextField(4)
          val t2 = new TextField(4)
          nodeFields += (x ->(t, t2))
          contents += t
          contents += t2
        }
      })
      contents += new Button("Generate!")
    }
  }
  preferredSize = new Dimension(400, 800)
  pack()
  visible = true
}

case class Config(nodes: Array[String],
                  rtbOffset: Point2D.Double, rtfOffset: Point2D.Double, btfOffset: Point2D.Double, btbOffset: Point2D.Double,
                  rbfOffset: Point2D.Double, bbfOffset: Point2D.Double, bbbOffset: Point2D.Double)