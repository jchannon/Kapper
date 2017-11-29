# Kapper
A MicroORM for Kotlin

## Usage
```
val conn = DriverManager.getConnection("jdbc:postgresql:amdb")

val foo = conn.queryfirst<Foo>("select * from system")
```
