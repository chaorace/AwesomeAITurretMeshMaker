/**
 * Created by Chris on 6/9/2015.
 */
object AwesomeXmlFormatter {
  def formatNodes(nodes: Array[String], rule: String) = {
    //build output
    var output = "<?xml version=\"1.0\" ?>\n<enemy>\n    <behaviour>\n        <root x=\"110\" y=\"40\">\n            <string id=\"Comment\">MAPNAME Map nodes</string>\n            <normal>\n                <sequence>\n                    <string id=\"Is blocking\" values=\"yesno\">no</string>\n                    <normal>"
    nodes.foreach(x => {
      output += rule.replaceAll("NODENAME", x) + "\n"
    })
    output += "</normal>\n                </sequence>\n            </normal>\n        </root>\n    </behaviour>\n</enemy>"
    //return output
    output
  }
}
