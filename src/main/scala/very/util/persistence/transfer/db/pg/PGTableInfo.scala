package very.util.persistence.transfer.db.pg

import very.util.persistence.transfer.db.wrapper.ResultSetIterator
import very.util.persistence.transfer.db.{ SQLColumn, SQLTable }

import java.sql.Connection
import scala.util.{ Try, Using }

object PGTableInfo {

  def getTableInfo(
    connection: Connection,
    schema: String = "public"
  ): Try[Seq[SQLTable]] = {
    val tableNameResult = Using(
      connection.prepareStatement(
        "SELECT tablename FROM pg_tables WHERE schemaname = ?"
      )
    ) { ps =>
      ps.setString(1, schema)
      ResultSetIterator(ps.executeQuery()).map(_.string("tablename")).toList
    }

    tableNameResult.map { tableNames =>
      tableNames.map { tableName =>
        Using(connection.prepareStatement(s"""SELECT
             |    f.attnum AS number,
             |    f.attname AS name,
             |    f.attnum,
             |    f.attnotnull AS notnull,
             |    pg_catalog.format_type(f.atttypid,f.atttypmod) AS type,
             |    CASE
             |        WHEN p.contype = 'p' THEN 't'
             |        ELSE 'f'
             |    END AS primarykey,
             |    CASE
             |        WHEN p.contype = 'u' THEN 't'
             |        ELSE 'f'
             |    END AS uniquekey,
             |    CASE
             |        WHEN p.contype = 'f' THEN g.relname
             |    END AS foreignkey,
             |    CASE
             |        WHEN p.contype = 'f' THEN p.confkey
             |    END AS foreignkey_fieldnum,
             |    CASE
             |        WHEN p.contype = 'f' THEN g.relname
             |    END AS foreignkey,
             |    CASE
             |        WHEN p.contype = 'f' THEN p.conkey
             |    END AS foreignkey_connnum,
             |    CASE
             |        WHEN f.atthasdef = 't' THEN pg_get_expr(d.adbin, d.adrelid)
             |    END AS default
             |FROM pg_attribute f
             |    JOIN pg_class c ON c.oid = f.attrelid
             |    JOIN pg_type t ON t.oid = f.atttypid
             |    LEFT JOIN pg_attrdef d ON d.adrelid = c.oid AND d.adnum = f.attnum
             |    LEFT JOIN pg_namespace n ON n.oid = c.relnamespace
             |    LEFT JOIN pg_constraint p ON p.conrelid = c.oid AND f.attnum = ANY (p.conkey)
             |    LEFT JOIN pg_class AS g ON p.confrelid = g.oid
             |WHERE c.relkind = 'r'::char
             |    AND n.nspname = ?  -- Replace with Schema name
             |    AND c.relname = ?  -- Replace with table name
             |    AND f.attnum > 0 ORDER BY number
             |""".stripMargin)) { ps =>
          ps.setString(1, schema)
          ps.setString(2, tableName)
          val columns = ResultSetIterator(ps.executeQuery()).map { v =>
            val columnName = v.string("name")
            val columnDefault = Option(v.string("default"))
            val isNull = !v.boolean("notnull")
            val dataType = v.string("type")
            val isAutoIncrement = columnDefault.exists(_.startsWith("nextval("))
            SQLColumn(columnName, dataType, !isNull, isAutoIncrement) -> v
              .boolean("primarykey")
          }.toList

          SQLTable(
            tableName,
            columns.map(_._1),
            columns.collect { case v if v._2 => v._1.name },
            schema = Some(schema)
          )
        }.get
      }
    }
  }

}
