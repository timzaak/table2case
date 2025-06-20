package very.util.persistence.transfer

import org.testcontainers.containers.PostgreSQLContainer
import very.util.persistence.transfer.db.Model

object PostgresHelper {

  def simplePGSQL: String =
    """create table if not exists users(
       |id serial primary key,
       |username text not null,
       |info text
       |)
       """.stripMargin

  def fullPGTypeSQL: String =
    """create table if not exists ta(
        |id serial primary key,
        |t_tiny_int int2,
        |t_long bigint,
        |t_bson jsonb, // Postgres specific
        |t_date DATE ,
        |t_timestamp_tz timestamptz default now(), // Postgres specific
        |t_text text,
        |t_money numeric(10,2),
        |t_array_int integer[], // Postgres specific
        |t_array_text text[] // Postgres specific
        |)
        """.stripMargin

  lazy val postgres = {
    val pg = new PostgreSQLContainer("postgres:15")
    pg.start()
    pg
  }

  def getPGModel(sql: String = simplePGSQL): Model = {
    val model = Model(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
    val stmt = model._connection.createStatement()
    stmt.execute(sql)
    stmt.close()
    model
  }
}
