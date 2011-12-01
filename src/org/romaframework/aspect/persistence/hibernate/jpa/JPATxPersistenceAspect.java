package org.romaframework.aspect.persistence.hibernate.jpa;

import javax.persistence.EntityManager;

import org.romaframework.aspect.persistence.hibernate.HibernatePersistenceModule;

public class JPATxPersistenceAspect extends JPABasePersistenceAspect {

	private EntityManager	manager;

	public JPATxPersistenceAspect(HibernatePersistenceModule module) {
		super(module);
	}

	@Override
	protected void init() {
		manager = getModule().getEntityManagerFactory().createEntityManager();
		manager.getTransaction().begin();
	}

	@Override
	protected void beginOperation(EntityManager iManager) {

	}

	@Override
	protected void endOperation(EntityManager iManager) {

	}

	@Override
	protected void closeOperation(EntityManager iManager) {
	}

	public void commit() {
		manager.getTransaction().commit();
		manager.close();
	}

	public void rollback() {
		manager.getTransaction().rollback();
		manager.close();
	}

	public void close() {
		if (!manager.isOpen())
			return;

		if (manager.getTransaction().isActive())
			rollback();
	}

	@Override
	public EntityManager getEntityManager() {
		return manager;
	}

}
