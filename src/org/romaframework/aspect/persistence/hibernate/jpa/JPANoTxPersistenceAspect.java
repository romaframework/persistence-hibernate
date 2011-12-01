package org.romaframework.aspect.persistence.hibernate.jpa;

import javax.persistence.EntityManager;

import org.romaframework.aspect.persistence.hibernate.HibernatePersistenceModule;

public class JPANoTxPersistenceAspect extends JPABasePersistenceAspect {

	private EntityManager	manager;

	public JPANoTxPersistenceAspect(HibernatePersistenceModule module) {
		super(module);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void init() {
		manager = getModule().getEntityManagerFactory().createEntityManager();
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
		// TODO Auto-generated method stub

	}

	public void commit() {
		close();
	}

	public void rollback() {
		close();
	}

	public void close() {
		manager.close();
	}

	@Override
	public EntityManager getEntityManager() {
		return manager;
	}

}
