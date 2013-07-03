package org.romaframework.aspect.persistence.hibernate.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.romaframework.aspect.persistence.QueryByFilter;
import org.romaframework.aspect.persistence.QueryOperator;
import org.romaframework.aspect.persistence.hibernate.domain.Person;
import org.romaframework.core.Roma;
import org.romaframework.core.config.RomaApplicationContext;

public class SimplePersistenceTest {

	@BeforeClass
	public static void init(){
		RomaApplicationContext.getInstance().startup();
	}
	
	@Before
	public void before() {
		Roma.context().create();
	}

	@After
	public void after() {
		Roma.context().destroy();
	}

	@Test
	public void testCreateDelete() {
		Person p = new Person();
		p.setName("jhon");
		p.setSurname("doe");

		QueryByFilter filter = new QueryByFilter(Person.class);
		filter.addItem("name", QueryOperator.EQUALS, "jhon");

		Roma.context().persistence().createObject(p);
		Assert.assertEquals(1,Roma.context().persistence().query(filter).size());
		Roma.context().persistence().deleteObject(p);
		Assert.assertEquals(0,Roma.context().persistence().query(filter).size());
	}

}
