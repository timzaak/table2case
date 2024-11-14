package very.util.persistence.transfer

import munit.FunSuite

class PGInfoSuite extends FunSuite {
  test("simple") {
    val model = SuiteHelper.getPGModel(SuiteHelper.fullPGTypeSQL)
    val info = PGTableInfo.getTableInfo(model._connection)
    println(info)
    
    
    val s = model._connection.prepareStatement(
      s"""SELECT  
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
         |    AND n.nspname = ?  
         |    AND c.relname = ?
         |    AND f.attnum > 0 ORDER BY number
         |;
         |""".stripMargin
    )
    s.setString(1 , "public")
    s.setString(2, "ta")
    val r = s.executeQuery()
    println(r)
  }

}
