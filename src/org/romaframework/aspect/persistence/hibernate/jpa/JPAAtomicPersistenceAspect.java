package org.romaframework.aspect.persistence.hibernate.jpa;

import javax.persistence.EntityManager;

import org.romaframework.aspect.persistence.hibernate.HibernatePersistenceModule;

public class JPAAtomicPersistenceAspect extends JPABasePersistenceAspect {

	public JPAAtomicPersistenceAspect(HibernatePersistenceModule module) {
		super(module);
	}

	@Override
	protected void init() {

	}

	@Override
	public EntityManager getEntityManager() {
		return getModule().getEntityManagerFactory().createEntityManager();
	}

	@Override
	protected void beginOperation(EntityManager iManager) {
		iManager.getTransaction().begin();
	}

	@Override
	protected void endOperation(EntityManager iManager) {
		iManager.getTransaction().commit();
	}

	@Override
	protected void closeOperation(EntityManager iManager) {
		iManager.close();
	}

	public void commit() {
	}

	public void rollback() {
	}

	public void close() {
	}

}
