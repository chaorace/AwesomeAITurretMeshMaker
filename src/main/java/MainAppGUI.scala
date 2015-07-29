
import java.io.PrintWriter
import java.net.URL

import scala.io.Source
import scala.swing._
import scala.swing.event.{SelectionChanged, ButtonClicked}
import pickling.Defaults._
import pickling.json._

/**
 * Created by Chris on 6/9/2015.
 */

//Downloads nodelists and saves them in Awesomenauts formatted XML
object MainAppGUI extends SimpleSwingApplication {
  var customURL: Option[String] = None
  val fc = new FileChooser

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
        text = "Generate!"
        reactions += {
          //Downloads nodes and saves them to selected file after clicking save
          case ButtonClicked(_) => {

            //Processes url into an array of strings representing each node
            val downloadedText = Source.fromURL(if (customURL.nonEmpty) new URL(customURL.get) else mapOptions.selection.item.url).mkString
            val nodes = downloadedText.split("\n")
            //Sends to node calibration Gui in a config object

            //Decide which type of config to use depending on selected map
            val mapName = mapOptions.selection.item.toString
            //Select xml format type based on map
            mapName match {
              case "Sorona" => {
                try {
                  new NodeCalibrationGUI(Config(nodes, FormatType.Sorona,
                    new SPoint2D(0.00, 0.00),
                    new SPoint2D(offsetsPanel.rtfx.text.toDouble, offsetsPanel.rtfy.text.toDouble),
                    new SPoint2D(offsetsPanel.btfx.text.toDouble, offsetsPanel.btfy.text.toDouble),
                    new SPoint2D(0.00, 0.00),
                    new SPoint2D(offsetsPanel.rbfx.text.toDouble, offsetsPanel.rbfy.text.toDouble),
                    new SPoint2D(offsetsPanel.bbfx.text.toDouble, offsetsPanel.bbfy.text.toDouble),
                    new SPoint2D(offsetsPanel.bbbx.text.toDouble, offsetsPanel.bbby.text.toDouble)))
                } catch {
                  case _: Exception => Dialog.showMessage(null, "Bad number input")
                }
              }
              case "AI Station 404" => {
                try {
                  new NodeCalibrationGUI(Config(nodes, FormatType.oldAI,
                    new SPoint2D(offsetsPanel.rtbx.text.toDouble, offsetsPanel.rtby.text.toDouble),
                    new SPoint2D(0.00, 0.00),
                    new SPoint2D(0.00, 0.00),
                    new SPoint2D(offsetsPanel.btbx.text.toDouble, offsetsPanel.btby.text.toDouble),
                    new SPoint2D(offsetsPanel.rbfx.text.toDouble, offsetsPanel.rbfy.text.toDouble),
                    new SPoint2D(offsetsPanel.bbfx.text.toDouble, offsetsPanel.bbfy.text.toDouble),
                    new SPoint2D(offsetsPanel.bbbx.text.toDouble, offsetsPanel.bbby.text.toDouble)))
                } catch {
                  case _: Exception => Dialog.showMessage(null, "Bad number input")
                }
              }
              case _ => {
                try {
                  new NodeCalibrationGUI(Config(nodes, FormatType.Normal,
                    new SPoint2D(offsetsPanel.rtbx.text.toDouble, offsetsPanel.rtby.text.toDouble),
                    new SPoint2D(offsetsPanel.rtfx.text.toDouble, offsetsPanel.rtfy.text.toDouble),
                    new SPoint2D(offsetsPanel.btfx.text.toDouble, offsetsPanel.btfy.text.toDouble),
                    new SPoint2D(offsetsPanel.btbx.text.toDouble, offsetsPanel.btby.text.toDouble),
                    new SPoint2D(offsetsPanel.rbfx.text.toDouble, offsetsPanel.rbfy.text.toDouble),
                    new SPoint2D(offsetsPanel.bbfx.text.toDouble, offsetsPanel.bbfy.text.toDouble),
                    new SPoint2D(offsetsPanel.bbbx.text.toDouble, offsetsPanel.bbby.text.toDouble)))
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
      contents += new BoxPanel(Orientation.Horizontal) {
        contents += saveButton
        contents += new Button("Load Custom Map URL") {
          reactions += {
            case ButtonClicked(_) => {
              customURL = Dialog.showInput(contents.head, "Please input custom map url", initial = mapOptions.selection.item.url.toString)
            }
          }
        }
        contents += new Button("Clear Custom Map URL") {
          reactions += {
            case ButtonClicked(_) => {
              customURL = None
            }
          }
        }
        contents += new Button("Save Configuration") {
          reactions += {
            case ButtonClicked(_) => {
              val mapName = mapOptions.selection.item.toString
              val c = new MainUIConfig(mapName, customURL,
                new SPoint2D(offsetsPanel.rtbx.text.toDouble, offsetsPanel.rtby.text.toDouble),
                new SPoint2D(offsetsPanel.rtfx.text.toDouble, offsetsPanel.rtfy.text.toDouble),
                new SPoint2D(offsetsPanel.btfx.text.toDouble, offsetsPanel.btfy.text.toDouble),
                new SPoint2D(offsetsPanel.btbx.text.toDouble, offsetsPanel.btby.text.toDouble),
                new SPoint2D(offsetsPanel.rbfx.text.toDouble, offsetsPanel.rbfy.text.toDouble),
                new SPoint2D(offsetsPanel.bbfx.text.toDouble, offsetsPanel.bbfy.text.toDouble),
                new SPoint2D(offsetsPanel.bbbx.text.toDouble, offsetsPanel.bbby.text.toDouble))
              val pkl = c.pickle
              fc.showSaveDialog(null)
              val outputFile = fc.selectedFile
              val writer = new PrintWriter(outputFile)
              writer.write(pkl.value)
              writer.close()
              Dialog.showMessage(null, "Success!")
            }
          }
        }
        contents += new Button("Load Configuration") {
          reactions += {
            case ButtonClicked(_) => {
              fc.showOpenDialog(null)
              val inputFile = fc.selectedFile
              val c: MainUIConfig = Source.fromFile(inputFile).mkString.unpickle[MainUIConfig]
              c.mapName match {
                case "Ribbit IV" => mapOptions.selection.index = 0
                case "Sorona" => mapOptions.selection.index = 1
                case "Aiguillon" => mapOptions.selection.index = 2
                case "AI Station 205" => mapOptions.selection.index = 3
                case "AI Station 404" => mapOptions.selection.index = 4
              }
              offsetsPanel.rtbx.text = c.rtbOffset.x.toString
              offsetsPanel.rtby.text = c.rtbOffset.y.toString
              offsetsPanel.rtfx.text = c.rtfOffset.x.toString
              offsetsPanel.rtfy.text = c.rtfOffset.y.toString
              offsetsPanel.rbfx.text = c.rbfOffset.x.toString
              offsetsPanel.rbfy.text = c.rbfOffset.y.toString
              offsetsPanel.btfx.text = c.btfOffset.x.toString
              offsetsPanel.btfy.text = c.btfOffset.y.toString
              offsetsPanel.bbfx.text = c.bbfOffset.x.toString
              offsetsPanel.bbfy.text = c.bbfOffset.y.toString
              offsetsPanel.btbx.text = c.btbOffset.x.toString
              offsetsPanel.btby.text = c.btbOffset.y.toString
              offsetsPanel.bbbx.text = c.bbbOffset.x.toString
              offsetsPanel.bbby.text = c.bbbOffset.y.toString
              customURL = c.customURL
            }
          }
        }
      }
    }
  }
}

class GameMap(name: String, val url: URL) {
  override def toString: String = name
}

case class MainUIConfig(mapName: String, customURL: Option[String],
                        rtbOffset: SPoint2D, rtfOffset: SPoint2D, btfOffset: SPoint2D, btbOffset: SPoint2D,
                        rbfOffset: SPoint2D, bbfOffset: SPoint2D, bbbOffset: SPoint2D)