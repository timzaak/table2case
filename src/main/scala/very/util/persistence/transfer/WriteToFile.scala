package very.util.persistence.transfer

import java.io.FileWriter
import java.nio.file.Paths
import scala.util.Using

trait WriteToFile {
  def `package`: String
  def schema: String
  def name: String
  def writeToFile(srcPath: String, isForce: Boolean = false): Unit = {
    val path = Paths
      .get(srcPath, `package`.split("\\.").toList.appended(s"${name}.scala")*)
      .toFile

    if (path.exists() && !isForce) {
      // Write Log
    } else {
      Using(FileWriter(path))(_.write(schema))
    }
  }
}
