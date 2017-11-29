import java.sql.Connection
import java.sql.DriverManager
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.memberProperties

fun <T : Any> get(c: KClass<T>, sql: String, conn: Connection): List<T> {

    var results = mutableListOf<T>()

    var instance = c.createInstance()

    val statement = conn.createStatement()
    val reader = statement.executeQuery(sql)

    val columns = (1..reader.metaData.columnCount).map { reader.metaData.getColumnName(it) }

    var isSet = false

    while (reader.next()) {
        c.memberProperties
                .filterIsInstance<KMutableProperty<*>>()
                .forEach {
                    if (columns.any { s -> s.equals(it.name, true) }) {
                        it.setter.call(instance, reader.getObject(it.name))
                        isSet = true
                    }
                }

        if (isSet) {
            results.add(instance)
            isSet = false
        }
    }
    return results
}


inline fun <reified T : Any> Connection.query(sql: String) = get(T::class, sql, this)
inline fun <reified T : Any> Connection.queryfirst(sql: String) = get(T::class, sql, this).first()


class Foo {
    var myname: String = "emptyName"
    var other: String = "emptyOther"
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
    var fred: String = "should not be found in column list"

}

fun main(args: Array<String>) {

    val conn = DriverManager.getConnection("jdbc:postgresql:amdb")

    val foo = conn.queryfirst<Foo>("select * from system")
    println(foo.lastportusageupdate)

    val bar = conn.query<Setting>("select * from setting")
    println(bar.first().Id)
    println(bar.first().Name)
    println(bar.first().Value)
    println(bar.first().DefaultValue)
    println(bar.first().Description)
    println(bar.first().Visible)
    println(bar.first().fred)

    var setting = conn.queryfirst<Setting>("select * from setting")
    println(setting.Id)
    println(setting.Name)
    println(setting.Value)
    println(setting.DefaultValue)
    println(setting.Description)
    println(setting.Visible)
    println(setting.fred)
}

