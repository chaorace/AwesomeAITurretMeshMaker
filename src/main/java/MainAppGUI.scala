
import java.net.URL

import scala.io.Source
import scala.swing._
import scala.swing.event.{SelectionChanged, ButtonClicked}

/**
 * Created by Chris on 6/9/2015.
 */

//Downloads nodelists and saves them in Awesomenauts formatted XML
object MainAppGUI extends SimpleSwingApplication {
  override def top: Frame = new MainFrame{
    contents = new BoxPanel(Orientation.Vertical){
      //GUI
      val mapOptions = new ComboBox[Map](Seq[Map]( //Combobox of possible map selections
        new Map("Ribbit IV", new URL("https://docs.google.com/spreadsheets/d/1ga9T6vYJXUEta_M8aI5fnPSJ13EfZyb4vJaSMF5wQig/export?format=csv&id=1ga9T6vYJXUEta_M8aI5fnPSJ13EfZyb4vJaSMF5wQig&gid=2105017707")),
        new Map("Sorona", new URL("https://docs.google.com/spreadsheets/d/1ga9T6vYJXUEta_M8aI5fnPSJ13EfZyb4vJaSMF5wQig/export?format=csv&id=1ga9T6vYJXUEta_M8aI5fnPSJ13EfZyb4vJaSMF5wQig&gid=1546795163")),
        new Map("Aiguillon", new URL("https://docs.google.com/spreadsheets/d/1ga9T6vYJXUEta_M8aI5fnPSJ13EfZyb4vJaSMF5wQig/export?format=csv&id=1ga9T6vYJXUEta_M8aI5fnPSJ13EfZyb4vJaSMF5wQig&gid=16438000")),
        new Map("AI Station 205", new URL("https://docs.google.com/spreadsheets/d/1ga9T6vYJXUEta_M8aI5fnPSJ13EfZyb4vJaSMF5wQig/export?format=csv&id=1ga9T6vYJXUEta_M8aI5fnPSJ13EfZyb4vJaSMF5wQig&gid=225475495")),
        new Map("AI Station 404", new URL("https://docs.google.com/spreadsheets/d/1ga9T6vYJXUEta_M8aI5fnPSJ13EfZyb4vJaSMF5wQig/export?format=csv&id=1ga9T6vYJXUEta_M8aI5fnPSJ13EfZyb4vJaSMF5wQig&gid=1717902971"))
      )) {
        selection.reactions += {
          //Disable boxes for nonexistant turrets on Sorona and 404
          case SelectionChanged(_) => {
            val mapName = selection.item.toString
            offsetsPanel.rtb.enabled = (mapName != "Sorona")
            offsetsPanel.btb.enabled = (mapName != "Sorona")
            offsetsPanel.rtf.enabled = (mapName != "AI Station 404")
            offsetsPanel.btf.enabled = (mapName != "AI Station 404")
          }
        }
      }

      val offsetsPanel = new BoxPanel(Orientation.Vertical) {
        val rtf = new TextField(4)
        val rtb = new TextField(4)
        val rbf = new TextField(4)
        val rbb = new TextField("0.00", 4) {
          enabled = false
        }
        val btf = new TextField(4)
        val btb = new TextField(4)
        val bbf = new TextField(4)
        val bbb = new TextField(4)

        contents += new BoxPanel(Orientation.Horizontal) {
          contents += new Label("RTB")
          contents += rtb
          contents += new Label("RTF")
          contents += rtf
          contents += new Label("BTF")
          contents += btf
          contents += new Label("BTB")
          contents += btb
        }
        contents += new BoxPanel(Orientation.Horizontal) {
          contents += new Label("RBB")
          contents += rbb
          contents += new Label("RBF")
          contents += rbf
          contents += new Label("BBF")
          contents += bbf
          contents += new Label("BBB")
          contents += bbb
        }

      }

      val saveButton = new Button{  //Opens a save dialog when clicked
        text = "Save"
        reactions += {
          //Downloads nodes and saves them to selected file after clicking save
          case ButtonClicked(_) => {

            //Processes url into an array of strings representing each node
            val downloadedText = Source.fromURL(mapOptions.selection.item.url).mkString
            val nodes = downloadedText.split("\n")
            //TODO: Input, Processing, and Saving
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

class Map(name: String, val url: URL){
  override def toString: String = name
}