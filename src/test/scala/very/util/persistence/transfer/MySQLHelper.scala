package very.util.persistence.transfer

import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName
import very.util.persistence.transfer.db.Model

object MySQLHelper {

  def simpleMySQL: String =
    """CREATE TABLE IF NOT EXISTS users (
       |id INT PRIMARY KEY AUTO_INCREMENT,
       |username VARCHAR(255) NOT NULL,
       |info TEXT
       |)
       """.stripMargin

  def fullMySQLTypeSQL: String =
    """CREATE TABLE IF NOT EXISTS ta(
        |id INT AUTO_INCREMENT PRIMARY KEY,
        |t_tiny_int TINYINT, -- MySQL equivalent for int2 (smallint)
        |t_long BIGINT,
        |t_bson JSON, -- MySQL uses JSON type
        |t_date DATE,
        |t_timestamp_tz TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- MySQL equivalent
        |t_text TEXT,
        |t_money DECIMAL(10,2), -- MySQL equivalent
        |t_array_int JSON, -- Workaround for array types using JSON
        |t_array_text JSON -- Workaround for array types using JSON
        |)
        """.stripMargin

  private lazy val mysqlContainer = {
    val container = new MySQLContainer(DockerImageName.parse("mysql:8.0.33"))
    container.start()
    container
  }

  def getMySQLModel(sql: String = simpleMySQL): Model = {
    Class.forName("com.mysql.cj.jdbc.Driver")
    val model = Model(
      mysqlContainer.getJdbcUrl(),
      mysqlContainer.getUsername(),
      mysqlContainer.getPassword(),
    )
    val stmt = model._connection.createStatement()
    try {
      stmt.execute(sql)
    } finally {
      stmt.close()
    }
    model
  }
}
