package org.romaframework.aspect.persistence.hibernate.jpa;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.EntityManagerImpl;
import org.romaframework.aspect.persistence.QueryByExample;
import org.romaframework.aspect.persistence.QueryByFilter;
import org.romaframework.aspect.persistence.QueryByFilterItem;
import org.romaframework.aspect.persistence.QueryByFilterItemPredicate;
import org.romaframework.aspect.persistence.QueryByFilterOrder;
import org.romaframework.aspect.persistence.QueryByText;
import org.romaframework.aspect.persistence.QueryOperator;
import org.romaframework.core.schema.SchemaHelper;

public class JPAPersistenceHelper {

	protected static Log	log	= LogFactory.getLog(JPAPersistenceHelper.class);

	public static long countByText(EntityManager manager, QueryByText queryInput) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static long countByExample(EntityManager manager, QueryByExample queryInput) {
		return countByExampleInternal(manager, queryInput);
	}

	public static long countByFilter(EntityManager manager, QueryByFilter queryInput) {
		return countByFilterInternal(manager, queryInput);
	}

	public static <T> List<T> queryByText(EntityManager manager, QueryByText queryInput) {
		return queryByTextInternal(manager, queryInput);
	}

	public static <T> List<T> queryByExample(EntityManager manager, QueryByExample queryInput) {
		return queryByExampleInternal(manager, queryInput);
	}

	public static <T> List<T> queryByFilter(EntityManager manager, QueryByFilter queryInput) {
		return queryByFilterInternal(manager, queryInput);
	}

	private static long countByExampleInternal(EntityManager manager, QueryByExample queryInput) {
		if (manager instanceof EntityManagerImpl) {
			Session session = ((EntityManagerImpl) manager).getSession();
			Criteria criteria = session.createCriteria(queryInput.getCandidateClass());
			criteria.setProjection(Projections.rowCount());
			return (Long) criteria.list().get(0);
		}
		return 0;
	}

	private static long countByFilterInternal(EntityManager manager, QueryByFilter queryInput) {
		if (manager instanceof EntityManagerImpl) {
			Session session = ((EntityManagerImpl) manager).getSession();
			Criteria criteria = session.createCriteria(queryInput.getCandidateClass());
			for (QueryByFilterItem item : queryInput.getItems()) {
				criteria.add(createCriterion(item));
			}
			criteria.setProjection(Projections.rowCount());
			return (Long) criteria.list().get(0);
		}
		return 0;
	}

	private static <T> List<T> queryByExampleInternal(EntityManager manager, QueryByExample queryInput) {
		if (manager instanceof EntityManagerImpl) {
			QueryByFilter queryFilter = buildQueryByFilter(queryInput);
			return queryByFilter(manager, queryFilter);
		}
		return new ArrayList<T>();
	}

	@SuppressWarnings("unchecked")
	private static <T> List<T> queryByTextInternal(EntityManager manager, QueryByText queryInput) {
		if (manager instanceof EntityManagerImpl) {
			Session session = ((EntityManagerImpl) manager).getSession();
			Criteria criteria = session.createCriteria(queryInput.getCandidateClass());
			return criteria.list();
		}
		return new ArrayList<T>();
	}

	@SuppressWarnings("unchecked")
	private static <T> List<T> queryByFilterInternal(EntityManager manager, QueryByFilter queryInput) {
		if (manager instanceof EntityManagerImpl) {
			Session session = ((EntityManagerImpl) manager).getSession();
			Criteria criteria = session.createCriteria(queryInput.getCandidateClass());
			for (QueryByFilterItem item : queryInput.getItems()) {
				criteria.add(createCriterion(item));
			}
			return criteria.list();
		}
		return new ArrayList<T>();
	}

	private static Criterion createCriterion(QueryByFilterItem filter) {
		QueryByFilterItemPredicate item = (QueryByFilterItemPredicate) filter;
		if (QueryByFilter.FIELD_LIKE.equals(item.getFieldOperator())) {
			return Restrictions.like(item.getFieldName(), item.getFieldValue()+"%");
		}
		return Restrictions.eq(item.getFieldName(), item.getFieldValue());
	}

	protected static QueryByFilter buildQueryByFilter(QueryByExample iQuery) {
		if (log.isDebugEnabled())
			log.debug("[JDOPersistenceAspect.queryByExample] Class: " + iQuery.getCandidateClass() + " filter object: " + iQuery);

		// TODO Use SchemaClassReflection to use method/field getters and setters

		// EXTRACT QUERY BASED ON FILER OBJECT
		QueryByFilter filter = new QueryByFilter(iQuery.getCandidateClass());
		filter.setRangeFrom(iQuery.getRangeFrom(), iQuery.getRangeTo());
		filter.setSubClasses(iQuery.isSubClasses());
		filter.setMode(iQuery.getMode());
		filter.setStrategy(iQuery.getStrategy());

		if (iQuery.getFilter() != null) {
			Field[] fields = SchemaHelper.getFields(iQuery.getCandidateClass());
			Object fieldValue;
			QueryOperator operator = null;
			for (int i = 0; i < fields.length; ++i) {
				try {
					if (Modifier.isStatic(fields[i].getModifiers()) || Modifier.isTransient(fields[i].getModifiers()))
						// JUMP STATIC AND TRANSIENT FIELDS
						continue;

					if (fields[i].getName().startsWith("jdo"))
						// IGNORE ALL JDO FIELDS
						continue;

					if (!fields[i].isAccessible())
						fields[i].setAccessible(true);

					fieldValue = fields[i].get(iQuery.getFilter());
					if (fieldValue != null) {
						if (fieldValue instanceof Collection<?> || fieldValue instanceof Map<?, ?>)
							continue;
						if (fieldValue instanceof String && ((String) fieldValue).length() == 0)
							// EMPTY STRING, IGNORE FOR FILTERING
							continue;

						if (fields[i].getType().equals(String.class))
							operator = QueryByFilter.FIELD_LIKE;
						else
							operator = QueryByFilter.FIELD_EQUALS;

						// INSERT INTO QUERY PREDICATE
						filter.addItem(fields[i].getName(), operator, fieldValue);
					}
				} catch (Exception e) {
					log.error("[JDOPersistenceAspect.queryByExample]", e);
				}
			}
		}

		QueryByFilter addFilter = iQuery.getAdditionalFilter();
		if (addFilter != null) {
			filter.setSubClasses(addFilter.isSubClasses());

			// COPY ALL ITEMS TO THE MAIN FILTER
			for (Iterator<QueryByFilterItem> it = addFilter.getItemsIterator(); it.hasNext();) {
				filter.addItem(it.next());
			}

			// COPY ALL ORDER CLAUSES TO THE MAIN FILTER
			for (Iterator<QueryByFilterOrder> it = addFilter.getOrdersIterator(); it.hasNext();) {
				filter.addOrder(it.next());
			}
		}
		return filter;
	}

}
