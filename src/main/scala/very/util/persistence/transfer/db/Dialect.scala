package very.util.persistence.transfer.db

enum Dialect {
  case Sqlite, Postgres, MySql, H2
  /*
  def driver: String = this match {
    case Sqlite   => "org.sqlite.JDBC"
    case Postgres => "org.postgresql.Driver"
    case MySql    => "com.mysql.cj.jdbc.Driver"
    case H2       => "org.h2.Driver"
  }
   */
}
