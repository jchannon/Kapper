import java.sql.DriverManager
import javax.sql.DataSource
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.reflect

class Kapper {
    protected fun <T : Any> get(c: KClass<T>): T {

        var instance = c.createInstance()

        val conn = DriverManager.getConnection("jdbc:postgresql:amdb")
        val statement = conn.createStatement()
        val reader = statement.executeQuery("select * from system")
        reader.next()


        c.memberProperties
                .filterIsInstance<KMutableProperty<*>>()
                .forEach {
                    println(it.name)
                    //while (reader.next()) {

                    for (i in 1..reader.metaData.columnCount) {
                        var colName = reader.metaData.getColumnName(i)
                        if (colName.equals(it.name, true)) {
                            it.setter.call(instance, reader.getString(colName))
                        }
                    }
                    //}

                }

        return instance
    }

    inline fun <reified T : Any> get() = get(T::class)
}

class Foo {
    var myname: String = "poo"
    var other: String = "jkl"
    var version: String = "empty"
}

fun main(args: Array<String>) {

    val kapper = Kapper()
    val foo = kapper.get<Foo>()

    println(foo.myname)
    println(foo.other)
    println(foo.version)

}

