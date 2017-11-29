# Kapper
A MicroORM for Kotlin

## Usage
```
val conn = DriverManager.getConnection("jdbc:postgresql:mydb")

val foo = conn.queryfirst<Foo>("select * from mytable")
```
