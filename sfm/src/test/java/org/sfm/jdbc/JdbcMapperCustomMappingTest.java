package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.map.GetterFactory;
import org.sfm.map.MappingContext;
import org.sfm.map.MappingException;
import org.sfm.map.FieldMapper;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.column.GetterProperty;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.reflect.Getter;
import org.sfm.test.jdbc.DbHelper;
import org.sfm.test.jdbc.TestRowHandler;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcMapperCustomMappingTest {

	@Test
	public void testColumnAlias() throws Exception {
		JdbcMapperFactory mapperFactory = JdbcMapperFactoryHelper.asm();
		mapperFactory.addAlias("not_id_column", "id");
		
		final JdbcMapper<DbObject> mapper = mapperFactory.newMapper(DbObject.class);
		
		DbHelper.testQuery(new TestRowHandler<PreparedStatement>() {

			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet r = t.executeQuery();
				r.next();
				DbHelper.assertDbObjectMapping(mapper.map(r));
			}

		}, DbHelper.TEST_DB_OBJECT_QUERY.replace("id,", "id as not_id_column,"));
	}
	
	@Test
	public void testColumnAliasStatic() throws Exception {
		JdbcMapperFactory mapperFactory = JdbcMapperFactoryHelper.asm();
		mapperFactory.addAlias("not_id_column", "id");
		
		final JdbcMapper<DbObject> mapper = JdbcMapperDbObjectTest.addColumn(mapperFactory.newBuilder(DbObject.class)).mapper();
		
		DbHelper.testQuery(new TestRowHandler<PreparedStatement>() {

			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet r = t.executeQuery();
				r.next();
				DbHelper.assertDbObjectMapping(mapper.map(r));
			}
			
		}, DbHelper.TEST_DB_OBJECT_QUERY.replace("id,", "id as not_id_column,"));
	}
	
	@Test
	public void testCustomMappingStatic() throws SQLException, Exception  {
		JdbcMapperFactory mapperFactory = JdbcMapperFactoryHelper.asm();
		mapperFactory.addCustomFieldMapper("id", new FieldMapper<ResultSet, DbObject>() {
			@Override
			public void mapTo(ResultSet source, DbObject target, MappingContext<? super ResultSet> mappingContext)
					throws MappingException {
				target.setId(1);
			}
		});
		
		
		final JdbcMapper<DbObject> mapper = JdbcMapperDbObjectTest.addColumn(mapperFactory.newBuilder(DbObject.class)).mapper();
		
		DbHelper.testQuery(new TestRowHandler<PreparedStatement>() {

			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet r = t.executeQuery();
				r.next();
				DbHelper.assertDbObjectMapping(mapper.map(r));
			}
			
		}, DbHelper.TEST_DB_OBJECT_QUERY.replace("id,", "33 as id,"));
	}
	
	@Test
	public void testCustomMapping() throws SQLException, Exception  {
		JdbcMapperFactory mapperFactory = JdbcMapperFactoryHelper.asm();
		mapperFactory.addCustomFieldMapper("id", new FieldMapper<ResultSet, DbObject>() {
			@Override
			public void mapTo(ResultSet source, DbObject target, MappingContext<? super ResultSet> mappingContext)
					throws MappingException {
				target.setId(1);
			}
		});
		
		
		final JdbcMapper<DbObject> mapper = mapperFactory.newMapper(DbObject.class);
		
		DbHelper.testQuery(new TestRowHandler<PreparedStatement>() {

			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet r = t.executeQuery();
				r.next();
				DbHelper.assertDbObjectMapping(mapper.map(r));
			}
			
		}, DbHelper.TEST_DB_OBJECT_QUERY.replace("id,", "33 as id,"));
	}

	@Test
	public void testCustomReaderOnSetter() throws SQLException, Exception {
		JdbcMapperFactory mapperFactory = JdbcMapperFactoryHelper.asm().addCustomGetter("id", new Getter<ResultSet, Long>() {
					@Override
					public Long get(ResultSet target) throws Exception {
						return 1l;
					}
				});


		final JdbcMapper<DbObject> mapper = mapperFactory.newMapper(DbObject.class);

		DbHelper.testQuery(new TestRowHandler<PreparedStatement>() {

			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet r = t.executeQuery();
				r.next();
				DbHelper.assertDbObjectMapping(mapper.map(r));
			}

		}, DbHelper.TEST_DB_OBJECT_QUERY.replace("id,", "33 as id,"));
	}
	@SuppressWarnings("unchecked")
	@Test
	public void testCustomReaderOnSetterStatic() throws Exception  {
		final JdbcMapper<DbObject> mapper =
				JdbcMapperFactoryHelper.asm().newBuilder(DbObject.class)
						.addMapping("id",
								new GetterProperty(new Getter<ResultSet, Long>() {
									@Override
									public Long get(ResultSet target) throws Exception {
										return 1l;
									}
								}))
						.addMapping("name") //email, creation_time, type_ordinal, type_name
						.addMapping("email")
						.addMapping("creation_time")
						.addMapping("type_ordinal")
						.addMapping("type_name")
						.mapper();


		DbHelper.testQuery(new TestRowHandler<PreparedStatement>() {

			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet r = t.executeQuery();
				r.next();
				DbHelper.assertDbObjectMapping(mapper.map(r));
			}

		}, DbHelper.TEST_DB_OBJECT_QUERY.replace("id,", "33 as id,"));
	}

    @Test
    public void testCustomGetterFactory() throws SQLException, Exception  {
        final JdbcMapper<DbObject> mapper =
                JdbcMapperFactoryHelper.asm()
                        .newBuilder(DbObject.class)
                        .addMapping("id",
                                FieldMapperColumnDefinition.customGetterFactory(new GetterFactory<ResultSet, JdbcColumnKey>() {
                                    @SuppressWarnings("unchecked")
                                    @Override
                                    public <P> Getter<ResultSet, P> newGetter(Type target, JdbcColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                                        return (Getter<ResultSet, P>) new Getter<ResultSet, Long>() {
                                            @Override
                                            public Long get(ResultSet target) throws Exception {
                                                return 1l;
                                            }
                                        };
                                    }
                                }))
                        .addMapping("name") //email, creation_time, type_ordinal, type_name
                        .addMapping("email")
                        .addMapping("creation_time")
                        .addMapping("type_ordinal")
                        .addMapping("type_name")
                        .mapper();


        DbHelper.testQuery(new TestRowHandler<PreparedStatement>() {

            @Override
            public void handle(PreparedStatement t) throws Exception {
                ResultSet r = t.executeQuery();
                r.next();
                DbHelper.assertDbObjectMapping(mapper.map(r));
            }

        }, DbHelper.TEST_DB_OBJECT_QUERY.replace("id,", "33 as id,"));
    }

	@Test
	public void testCustomReaderOnConstructor() throws SQLException, Exception  {
		JdbcMapperFactory mapperFactory = JdbcMapperFactoryHelper.asm().addCustomGetter("id",new Getter<ResultSet, Long>() {
					@Override
					public Long get(ResultSet target) throws Exception {
						return 1l;
					}
				});


		final JdbcMapper<DbFinalObject> mapper = mapperFactory.newMapper(DbFinalObject.class);

		DbHelper.testQuery(new TestRowHandler<PreparedStatement>() {

			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet r = t.executeQuery();
				r.next();
				DbHelper.assertDbObjectMapping(mapper.map(r));
			}

		}, DbHelper.TEST_DB_OBJECT_QUERY.replace("id,", "33 as id,"));
	}
}