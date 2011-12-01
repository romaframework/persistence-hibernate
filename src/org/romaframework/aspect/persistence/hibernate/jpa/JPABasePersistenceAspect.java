package org.romaframework.aspect.persistence.hibernate.jpa;

import java.util.List;

import javax.persistence.EntityManager;

import org.romaframework.aspect.persistence.PersistenceAspect;
import org.romaframework.aspect.persistence.PersistenceAspectAbstract;
import org.romaframework.aspect.persistence.PersistenceException;
import org.romaframework.aspect.persistence.Query;
import org.romaframework.aspect.persistence.QueryByExample;
import org.romaframework.aspect.persistence.QueryByFilter;
import org.romaframework.aspect.persistence.QueryByText;
import org.romaframework.aspect.persistence.hibernate.HibernatePersistenceModule;
import org.romaframework.core.schema.SchemaField;

public abstract class JPABasePersistenceAspect extends PersistenceAspectAbstract {

	private HibernatePersistenceModule	module;

	public JPABasePersistenceAspect(HibernatePersistenceModule module) {
		this.module = module;
		init();
	}

	protected abstract void init();

	protected abstract void beginOperation(EntityManager iManager);

	protected abstract void endOperation(EntityManager iManager);

	protected abstract void closeOperation(EntityManager iManager);

	public abstract EntityManager getEntityManager();

	public String getOID(Object iObject) throws PersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T loadObject(T iObject, String iMode) throws PersistenceException {
		return loadObject(iObject, iMode, PersistenceAspect.STRATEGY_DETACHING);
	}

	public <T> T loadObject(T iObject, String iMode, byte iStrategy) throws PersistenceException {
		return iObject;
	}

	public <T> T loadObjectByOID(String iOID, String iMode) throws PersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T loadObjectByOID(String iOID, String iMode, byte iStrategy) throws PersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T createObject(T iObject) throws PersistenceException {
		return createObject(iObject, STRATEGY_DETACHING);
	}

	public <T> T createObject(T iObject, byte iStrategy) throws PersistenceException {
		getEntityManager().persist(iObject);
		getEntityManager().refresh(iObject);
		return iObject;

	}

	public <T> T updateObject(T iObject) throws PersistenceException {
		return updateObject(iObject, STRATEGY_DETACHING);
	}

	public <T> T updateObject(T iObject, byte iStrategy) throws PersistenceException {
		return getEntityManager().merge(iObject);
	}

	public Object[] updateObjects(Object[] iObjects) throws PersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object[] updateObjects(Object[] iObjects, byte iStrategy) throws PersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteObject(Object iObject) throws PersistenceException {
		// TODO Auto-generated method stub

	}

	public void deleteObjects(Object[] iObjects) throws PersistenceException {
		// TODO Auto-generated method stub

	}

	public <T> List<T> query(Query iQuery) throws PersistenceException {
		EntityManager manager = null;
		List<T> result = null;
		try {
			manager = getEntityManager();

			beginOperation(manager);

			if (iQuery instanceof QueryByExample) {
				QueryByExample queryInput = (QueryByExample) iQuery;
				result = JPAPersistenceHelper.queryByExample(manager, queryInput);
			} else if (iQuery instanceof QueryByFilter) {
				QueryByFilter queryInput = (QueryByFilter) iQuery;
				result = JPAPersistenceHelper.queryByFilter(manager, queryInput);
			} else if (iQuery instanceof QueryByText) {
				QueryByText queryInput = (QueryByText) iQuery;
				result = JPAPersistenceHelper.queryByText(manager, queryInput);
			}

		} catch (Throwable e) {
			throw new PersistenceException("$PersistenceAspect.query.error", e);
		} finally {
			endOperation(manager);
		}
		return result;
	}

	public <T> T queryOne(Query iQuery) throws PersistenceException {
		List<T> result = query(iQuery);
		if (result != null && result.size() > 0)
			return result.get(0);
		return null;
	}

	public long queryCount(Query iQuery) throws PersistenceException {
		EntityManager manager = null;
		long result = 0;
		try {
			manager = getEntityManager();

			beginOperation(manager);

			if (iQuery instanceof QueryByExample) {
				QueryByExample queryInput = (QueryByExample) iQuery;
				result = JPAPersistenceHelper.countByExample(manager, queryInput);
			} else if (iQuery instanceof QueryByFilter) {
				QueryByFilter queryInput = (QueryByFilter) iQuery;
				result = JPAPersistenceHelper.countByFilter(manager, queryInput);
			} else if (iQuery instanceof QueryByText) {
				QueryByText queryInput = (QueryByText) iQuery;
				result = JPAPersistenceHelper.countByText(manager, queryInput);
			}
		} catch (Throwable e) {
			throw new PersistenceException("$PersistenceAspect.query.error", e);
		} finally {
			endOperation(manager);
		}
		return result;
	}

	public boolean isObjectLocallyModified(Object iObject) throws PersistenceException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isObjectPersistent(Object iObject) throws PersistenceException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isClassPersistent(Class<?> iClass) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isFieldPersistent(Class<?> iClass, String iFieldName) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isFieldPersistent(SchemaField iField) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setObjectDirty(Object iObject, String iFieldName) {
		// TODO Auto-generated method stub

	}

	public byte getStrategy() {
		// TODO Auto-generated method stub
		return 0;
	}

	public byte getTxMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setTxMode(byte txMode) {
		// TODO Auto-generated method stub

	}

	public boolean isActive() {
		return getEntityManager().getTransaction().isActive();
	}

	public void commit() {
		getEntityManager().getTransaction().commit();
	}

	public void rollback() {
		getEntityManager().getTransaction().rollback();
	}

	public void close() {
		getEntityManager().close();
	}

	public Object getUnderlyingComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	public HibernatePersistenceModule getModule() {
		return module;
	}

	public void setModule(HibernatePersistenceModule module) {
		this.module = module;
	}
}
