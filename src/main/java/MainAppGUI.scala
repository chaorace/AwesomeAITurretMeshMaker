
import java.awt.geom.Point2D
import java.net.URL

import scala.io.Source
import scala.swing._
import scala.swing.event.{SelectionChanged, ButtonClicked}

/**
 * Created by Chris on 6/9/2015.
 */

//Downloads nodelists and saves them in Awesomenauts formatted XML
object MainAppGUI extends SimpleSwingApplication {
  override def top: Frame = new MainFrame {
    peer.setAlwaysOnTop(true)
    contents = new BoxPanel(Orientation.Vertical) {
      //GUI
      val mapOptions = new ComboBox[GameMap](Seq[GameMap](//Combobox of possible map selections
        new GameMap("Ribbit IV", new URL("https://docs.google.com/spreadsheets/d/1ga9T6vYJXUEta_M8aI5fnPSJ13EfZyb4vJaSMF5wQig/export?format=csv&id=1ga9T6vYJXUEta_M8aI5fnPSJ13EfZyb4vJaSMF5wQig&gid=2105017707")),
        new GameMap("Sorona", new URL("https://docs.google.com/spreadsheets/d/1ga9T6vYJXUEta_M8aI5fnPSJ13EfZyb4vJaSMF5wQig/export?format=csv&id=1ga9T6vYJXUEta_M8aI5fnPSJ13EfZyb4vJaSMF5wQig&gid=1546795163")),
        new GameMap("Aiguillon", new URL("https://docs.google.com/spreadsheets/d/1ga9T6vYJXUEta_M8aI5fnPSJ13EfZyb4vJaSMF5wQig/export?format=csv&id=1ga9T6vYJXUEta_M8aI5fnPSJ13EfZyb4vJaSMF5wQig&gid=16438000")),
        new GameMap("AI Station 205", new URL("https://docs.google.com/spreadsheets/d/1ga9T6vYJXUEta_M8aI5fnPSJ13EfZyb4vJaSMF5wQig/export?format=csv&id=1ga9T6vYJXUEta_M8aI5fnPSJ13EfZyb4vJaSMF5wQig&gid=225475495")),
        new GameMap("AI Station 404", new URL("https://docs.google.com/spreadsheets/d/1ga9T6vYJXUEta_M8aI5fnPSJ13EfZyb4vJaSMF5wQig/export?format=csv&id=1ga9T6vYJXUEta_M8aI5fnPSJ13EfZyb4vJaSMF5wQig&gid=1717902971"))
      )) {
        selection.reactions += {
          //Disable boxes for nonexistant turrets on Sorona and 404
          case SelectionChanged(_) => {
            val mapName = selection.item.toString
            offsetsPanel.rtbx.enabled = mapName != "Sorona"
            offsetsPanel.rtby.enabled = mapName != "Sorona"
            offsetsPanel.btbx.enabled = mapName != "Sorona"
            offsetsPanel.btby.enabled = mapName != "Sorona"
            offsetsPanel.rtfx.enabled = mapName != "AI Station 404"
            offsetsPanel.rtfy.enabled = mapName != "AI Station 404"
            offsetsPanel.btfx.enabled = mapName != "AI Station 404"
            offsetsPanel.btfy.enabled = mapName != "AI Station 404"
          }
        }
      }

      val offsetsPanel = new BoxPanel(Orientation.Vertical) {
        val rtfx = new TextField(4)
        val rtfy = new TextField(4)
        val rtbx = new TextField(4)
        val rtby = new TextField(4)
        val rbfx = new TextField(4)
        val rbfy = new TextField(4)
        val btfx = new TextField(4)
        val btfy = new TextField(4)
        val btbx = new TextField(4)
        val btby = new TextField(4)
        val bbfx = new TextField(4)
        val bbfy = new TextField(4)
        val bbbx = new TextField(4)
        val bbby = new TextField(4)

        contents += new BoxPanel(Orientation.Horizontal) {
          contents += new Label("RTB")
          contents += rtbx
          contents += rtby
          contents += new Label("RTF")
          contents += rtfx
          contents += rtfy
          contents += new Label("BTF")
          contents += btfx
          contents += btfy
          contents += new Label("BTB")
          contents += btbx
          contents += btby
        }
        contents += new BoxPanel(Orientation.Horizontal) {
          //Dummy boxes for the reference turret
          contents += new Label("RBB")
          contents += new TextField("0.00", 4) {
            enabled = false
          }
          contents += new TextField("0.00", 4) {
            enabled = false
          }
          contents += new Label("RBF")
          contents += rbfx
          contents += rbfy
          contents += new Label("BBF")
          contents += bbfx
          contents += bbfy
          contents += new Label("BBB")
          contents += bbbx
          contents += bbby
        }

      }

      val saveButton = new Button {
        //Opens a save dialog when clicked
        text = "Save"
        reactions += {
          //Downloads nodes and saves them to selected file after clicking save
          case ButtonClicked(_) => {

            //Processes url into an array of strings representing each node
            val downloadedText = Source.fromURL(mapOptions.selection.item.url).mkString
            //            val downloadedText = "REGENHOME\nUPPERLANESECOND_0\nFINALSTAND_0"
            val nodes = downloadedText.split("\n")
            //Sends to node calibration Gui in a config object

            //Decide which type of config to use depending on selected map
            val mapName = mapOptions.selection.item.toString
            //Select xml format type based on map
            mapName match {
              case "Sorona" => {
                try {
                  new NodeCalibrationGUI(Config(nodes, FormatType.Sorona,
                    new Point2D.Double(0.00, 0.00),
                    new Point2D.Double(offsetsPanel.rtfx.text.toDouble, offsetsPanel.rtfy.text.toDouble),
                    new Point2D.Double(offsetsPanel.btfx.text.toDouble, offsetsPanel.btfy.text.toDouble),
                    new Point2D.Double(0.00, 0.00),
                    new Point2D.Double(offsetsPanel.rbfx.text.toDouble, offsetsPanel.rbfy.text.toDouble),
                    new Point2D.Double(offsetsPanel.bbfx.text.toDouble, offsetsPanel.bbfy.text.toDouble),
                    new Point2D.Double(offsetsPanel.bbbx.text.toDouble, offsetsPanel.bbby.text.toDouble)))
                } catch {
                  case _: Exception => Dialog.showMessage(null, "Bad number input")
                }
              }
              case "AI Station 404" => {
                try {
                  new NodeCalibrationGUI(Config(nodes, FormatType.oldAI,
                    new Point2D.Double(offsetsPanel.rtbx.text.toDouble, offsetsPanel.rtby.text.toDouble),
                    new Point2D.Double(0.00, 0.00),
                    new Point2D.Double(0.00, 0.00),
                    new Point2D.Double(offsetsPanel.btbx.text.toDouble, offsetsPanel.btby.text.toDouble),
                    new Point2D.Double(offsetsPanel.rbfx.text.toDouble, offsetsPanel.rbfy.text.toDouble),
                    new Point2D.Double(offsetsPanel.bbfx.text.toDouble, offsetsPanel.bbfy.text.toDouble),
                    new Point2D.Double(offsetsPanel.bbbx.text.toDouble, offsetsPanel.bbby.text.toDouble)))
                } catch {
                  case _: Exception => Dialog.showMessage(null, "Bad number input")
                }
              }
              case _ => {
                try {
                  new NodeCalibrationGUI(Config(nodes, FormatType.Normal,
                    new Point2D.Double(offsetsPanel.rtbx.text.toDouble, offsetsPanel.rtby.text.toDouble),
                    new Point2D.Double(offsetsPanel.rtfx.text.toDouble, offsetsPanel.rtfy.text.toDouble),
                    new Point2D.Double(offsetsPanel.btfx.text.toDouble, offsetsPanel.btfy.text.toDouble),
                    new Point2D.Double(offsetsPanel.btbx.text.toDouble, offsetsPanel.btby.text.toDouble),
                    new Point2D.Double(offsetsPanel.rbfx.text.toDouble, offsetsPanel.rbfy.text.toDouble),
                    new Point2D.Double(offsetsPanel.bbfx.text.toDouble, offsetsPanel.bbfy.text.toDouble),
                    new Point2D.Double(offsetsPanel.bbbx.text.toDouble, offsetsPanel.bbby.text.toDouble)))
                } catch {
                  case _: Exception => Dialog.showMessage(null, "Bad number input")
                }
              }
            }
          }
        }
      }

      contents += new Label("Pick a map to download")
      contents += mapOptions
      contents += new Label("Calibrate relative turret offsets")
      contents += offsetsPanel
      contents += saveButton
    }

  }
}

class GameMap(name: String, val url: URL) {
  override def toString: String = name
}