[![Build Status](https://travis-ci.org/arnaudroger/SimpleFlatMapper.svg?branch=master)](https://travis-ci.org/arnaudroger/SimpleFlatMapper)
[![Coverage Status](https://img.shields.io/coveralls/arnaudroger/SimpleFlatMapper.svg)](https://coveralls.io/r/arnaudroger/SimpleFlatMapper)

SimpleFlatMapper
========
Fast and Easy mapping from jdbc, csv to POJO.

SFM aims to provide a solution to map from a flat record - ResultSet, csv - to a java object with no configuration and low runtime cost. The object analysis is done at the initialisation of the mapper eliminating later reflection cost. 

The JdbcMapper query analysis is cached using a copy on write non locking technique. The cost of injection of data is reduce by generating asm for setters when possible and unrolling the mapping loop.

The SimpleFlatMapper JdbcMapper is a lightweight alternative to ibatis and hibernate.

Design
========
- work by default/no configuration
- low foot print
- use plain jdbc
- no external library needed, asm supported for max perf

What it does not do
-------
- no relationship mapping
- no query generation
- no insert/update
- no caching

Why?
-------

### Performance

Ibatis and hibernate have very expensive injection mechanism. On the hsqldb in mem for a medium size query it's a about 700% mark up for both. 
Sfm get's as fast as it can using asm generation. Event if you don't use asm is still a lot faster. 

### API intrusiveness

Ibatis provide the same kind of functionality put it forces you to use it's query mechanism and mask the jdbc api. 
Sfm just focus on the mapping from a ResultSet. You can manage the query the way you want. You can use JdbcTemplate, even use it in an Hibernate session.

Performance
========
See [src/test/benchmarks](/src/test/benchmarks) for more details.

Run 100000 queries, store the time it takes to read all the object in HdrHistogram. The table show the percentage above the pure jdbc benchmark 50% percentile. if it takes 100s for pure jdbc and 105s the targeted implementation it will show 5%.

SFM is for SimpleFlatMapper.

As you can see by the negative numbers are the one that look a bit sour there can be quite a bit of noise in the benchmark I'm still looking at sorting that out.

Mock Connection
-------

|Nb Rows|SFM Static|SFM Dynamic|SFM Dynamic NoASM|Sql2o|Hibernate|MyBatis|
|------:|------:|-------:|-------:|------:|----:|----:|
|1|9%|16%|50%|1006%|6207%|4067%|
|10|8%|16%|182%|839%|3897%|7262%|
|100|2%|1%|322%|736%|1457%|10725%|
|1000|24%|57%|350%|705%|941%|11475%|

In mem HsqlDb
-------

|Nb Rows|SFM Static|SFM Dynamic|SFM Dynamic NoASM|Sql2o|Hibernate|MyBatis|
|------:|------:|-------:|-------:|------:|----:|----:|
|1|2%|2%|5%|52%|184%|128%|
|10|3%|5%|11%|50%|242%|227%|
|100|7%|8%|24%|62%|403%|543%|
|1000|10%|9%|29%|65%|502%|721%|

Local Mysql
-------

|Nb Rows|SFM Static|SFM Dynamic|SFM Dynamic NoASM|Sql2o|Hibernate|MyBatis|
|------:|------:|-------:|-------:|------:|----:|----:|
|1|1%|2%|2%|10%|89%|126%|
|10|26%|27%|27%|131%|85%|126%|
|100|0%|1%|2%|11%|98%|84%|
|1000|1%|1%|6%|26%|132%|222%|

Samples
========

JdbcMapper
---------
```sql
create table MyTable {
	id bigint,
	email varchar(256),
	my_property int
}
```

```java
public class MyObject {
	private final long id;
	private final String email;
	private final int myProperty;
	
	public MyObject(long id, String email,  int myProperty) {
		this.id = id;
		this.email = email;
		this.myProperty = myProperty;
	}

	public long getId() { return id; }
	public String getEmail() { return email; }
	public int getProperty() { return myProperty; }
	
	public String toString() { ... }
}

public class MyDao {
    private final JdbcMapper<MyObject> mapper = 
    	JdbcMapperFactory.newInstance().newMapper(MyObject.class);

    public void writeAllObjectTo(Writer writer, Connection conn) throws SQLException {
        PreparedStatement ps = 
        	conn.prepareStatement("select id, email, my_property from MyTable");
        try {
            ResultSet rs = ps.executeQuery();
            mapper.forEach(rs, 
                new Handler<MyObject>() {
                     void handle(MyObject object) {
                         writer.append(object.toString()).append("\n");
                     }
                });
        } finally {
            ps.close();
        }
    }
}
```

JdbcTemplate
-----

See [JdbcTemplateMapperFactoryTest](/src/test/java/org/sfm/jdbc/spring/JdbcTemplateMapperFactoryTest.java) for more examples.

```java
class MyDao {
	private final JdbcTemplateMapper<DbObject> mapper = 
		new JdbcTemplateMapperFactory().newMapper(DbObject.class);
		
	public void doSomething() {		
		List<DbObject> results = template.query(DbHelper.TEST_DB_OBJECT_QUERY, mapper);
	}
	
	public void doSomethingElse() {		
		 template
		 	.query(DbHelper.TEST_DB_OBJECT_QUERY, 
		 		mapper.newResultSetExtractor(new ListHandler<DbObject>()))
		 	.getList();
	}
}
```

OsgiSupport
------
Why? because sometimes you don't have a choice.

```java
class MyService {

	@Reference
	JdbcMapperService jdbcMapperService;
	
	JdbcMapper mapper;
	
	....
	
	@Activate
	public void activate() {
		mapper = jdbcMapperService.newFactory().newMapper(DbObject.class);
	}
}
```

Mapping
========

the Dynamic mapper will assume a column name from the database will be matching ignoring case and underscore to the property name.
It is on the todo list to be able to specify manual mapping and a custom property name matcher.

ie:
```
- my_property => myProperty
- myproperty => myProperty
```

Value Injection
=======

The JdbcMapper supports
- constructor injection using asm to get the parameter name
- setter injection
- field injection
- complex object injection

It looks for injection on that order and if asm is present will generate optimised asm version.

Maven dependency
======

```xml
		<dependency>
			<groupId>com.github.arnaudroger</groupId>
			<artifactId>simpleFlatMapper</artifactId>
			<version>0.8</version>
		</dependency>
```
