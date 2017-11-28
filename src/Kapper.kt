import java.sql.Connection
import java.sql.DriverManager
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.memberProperties

//class Kapper {
fun <T : Any> get(c: KClass<T>, sql: String, conn: Connection): List<T> {

    //val g = hashMapOf("int8" to "Int")

    var results = mutableListOf<T>()

    var instance = c.createInstance()

    val statement = conn.createStatement()
    val reader = statement.executeQuery(sql)
    
    while (reader.next()) {
        c.memberProperties
                .filterIsInstance<KMutableProperty<*>>()
                .forEach {
                    for (i in 1..reader.metaData.columnCount) {
                        var colName = reader.metaData.getColumnName(i)
                        // println(reader.metaData.getColumnTypeName(i))
                        if (colName.equals(it.name, true)) {
                            //println("found " + it.name + " & " + colName)

                            when (reader.metaData.getColumnTypeName(i)) {
                                "varchar" -> it.setter.call(instance, reader.getString(colName))
                                "int8" -> it.setter.call(instance, reader.getInt(colName))
                                "int4" -> it.setter.call(instance, reader.getInt(colName))
                                "serial" -> it.setter.call(instance, reader.getInt(colName))
                                "timestamp" -> it.setter.call(instance, reader.getDate(colName))
                                "bool" -> it.setter.call(instance, reader.getBoolean(colName))
                            }

                            break
                        }
                    }
                }
    }

    results.add(instance)

    return results
}

//inline fun <reified T : Any> get(sql: String) = get(T::class, sql)

inline fun <reified T : Any> Connection.query(sql: String) = get(T::class, sql, this)
inline fun <reified T : Any> Connection.queryfirst(sql: String) = get(T::class, sql, this).first()

//}

class Foo {
    var myname: String = "poo"
    var other: String = "jkl"
    var version: String = "empty"
    var id: Int = 0
    var lastportusageupdate: Date = Date()
}

class Setting {
    var Id: Int = 0
    var Name: String = ""
    var Value: String = ""
    var DefaultValue: String = ""
    var Description: String = ""
    var Visible: Boolean = false

}

fun main(args: Array<String>) {

    val conn = DriverManager.getConnection("jdbc:postgresql:amdb")

    val bar = conn.query<Setting>("select * from setting")
    println(bar.first().Id)
    println(bar.first().Name)
    println(bar.first().Value)
    println(bar.first().DefaultValue)
    println(bar.first().Description)
    println(bar.first().Visible)

    var setting = conn.queryfirst<Setting>("select * from setting")
    println(setting.Id)
    println(setting.Name)
    println(setting.Value)
    println(setting.DefaultValue)
    println(setting.Description)
    println(setting.Visible)
}

