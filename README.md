# Kapper
A MicroORM for Kotlin inspired by [Dapper](https://github.com/StackExchange/Dapper) on .NET

## Usage
```
val conn = DriverManager.getConnection("jdbc:postgresql:mydb")

val foo = conn.queryfirst<Foo>("select * from mytable")
```
