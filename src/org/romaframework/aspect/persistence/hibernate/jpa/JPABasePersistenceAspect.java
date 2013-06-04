package org.romaframework.aspect.persistence.hibernate.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;

import org.romaframework.aspect.persistence.PersistenceAspect;
import org.romaframework.aspect.persistence.PersistenceAspectAbstract;
import org.romaframework.aspect.persistence.PersistenceException;
import org.romaframework.aspect.persistence.Query;
import org.romaframework.aspect.persistence.QueryByExample;
import org.romaframework.aspect.persistence.QueryByFilter;
import org.romaframework.aspect.persistence.QueryByText;
import org.romaframework.aspect.persistence.hibernate.HibernatePersistenceModule;
import org.romaframework.core.Roma;
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
		return iObject.getClass().toString()+module.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(iObject).toString();
	}

	public <T> T refreshObject(T iObject, String iMode) throws PersistenceException {
		return refreshObject(iObject, iMode, PersistenceAspect.STRATEGY_DETACHING);
	}

	public <T> T refreshObject(T iObject, String iMode, byte iStrategy) throws PersistenceException {
		return iObject;
	}

	public <T> T loadObjectByOID(String iOID, String iMode) throws PersistenceException {
		return loadObjectByOID(iOID, iMode, PersistenceAspect.STRATEGY_DETACHING);
	}

	@SuppressWarnings("unchecked")
	public <T> T loadObjectByOID(String iOID, String iMode, byte iStrategy) throws PersistenceException {
		return (T)getEntityManager().find(getClassFromOid(iOID), getIdFromOid(iOID));
	}

	public <T> T loadObjectByOID(Class<T> clazz, Object id) throws PersistenceException {
		return loadObjectByOID(clazz, id, null);
	}

	public <T> T loadObjectByOID(Class<T> clazz, Object id, String iMode) throws PersistenceException {
		return loadObjectByOID(clazz, id, iMode, PersistenceAspect.STRATEGY_DETACHING);
	}

	public <T> T loadObjectByOID(Class<T> clazz, Object id, String iMode, byte iStrategy) throws PersistenceException {
		return getEntityManager().find(clazz, id);
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
		return updateObjects(iObjects,STRATEGY_DETACHING);
	}

	public Object[] updateObjects(Object[] iObjects, byte iStrategy) throws PersistenceException {
		for(int i =0;i<iObjects.length;i++){
			iObjects[i]=updateObject(iObjects[i],iStrategy);
		}
		return iObjects;
	}

	public void deleteObject(Object iObject) throws PersistenceException {
		getEntityManager().remove(iObject);
	}

	public void deleteObjects(Object[] iObjects) throws PersistenceException {
		for (Object object : iObjects) {
			deleteObject(object);
		}
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
		//((Session)getEntityManager().getDelegate()).is
		//TODO:
		return false;
	}

	public boolean isObjectPersistent(Object iObject) throws PersistenceException {
		return getEntityManager().contains(iObject);
	}

	public boolean isClassPersistent(Class<?> iClass) {
		for(ManagedType<?> type : getEntityManager().getMetamodel().getManagedTypes()){
			if(type.getJavaType().equals(iClass))return true;
		}
		return false;
	}

	public boolean isFieldPersistent(Class<?> iClass, String iFieldName) {
		for(ManagedType<?> type : getEntityManager().getMetamodel().getManagedTypes()){
			if(type.getJavaType().equals(iClass)){
				for(Attribute<?, ?> att:  type.getAttributes()){
					if(att.getName().equals(iFieldName))
						return true;
				}
				return false;
			}
		}
		return false;
	}

	public boolean isFieldPersistent(SchemaField iField) {
		return isFieldPersistent((Class<?>)iField.getType().getSchemaClass().getLanguageType(), iField.getName());
	}

	public void setObjectDirty(Object iObject, String iFieldName) {
		// TODO Auto-generated method stub

	}

	public byte getStrategy() {
		return STRATEGY_DETACHING;
	}

	public byte getTxMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setTxMode(byte txMode) {
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
		return getEntityManager();
	}

	public HibernatePersistenceModule getModule() {
		return module;
	}

	public void setModule(HibernatePersistenceModule module) {
		this.module = module;
	}
	
	private Class<?> getClassFromOid(String oid){
		String className = oid.substring(0,oid.indexOf('@'));
		return (Class<?>)Roma.schema().getSchemaClass(className).getLanguageType();
	}
	
	private Object getIdFromOid(String oid){
		return Long.valueOf(oid.substring(0,oid.indexOf('@')+1));
	}
	
}
