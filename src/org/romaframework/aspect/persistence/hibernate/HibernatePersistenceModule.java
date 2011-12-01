package org.romaframework.aspect.persistence.hibernate;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.romaframework.aspect.persistence.PersistenceContextInjector;
import org.romaframework.core.module.SelfRegistrantConfigurableModule;

public class HibernatePersistenceModule extends SelfRegistrantConfigurableModule<String> {

	protected EntityManagerFactory	entityManagerFactory;
	protected static final PersistenceContextInjector	injector	= new PersistenceContextInjector();
	
	@Override
	public void startup() throws RuntimeException {
		init();
	}

	@Override
	public void shutdown() throws RuntimeException {
		super.shutdown();
	}

	public EntityManagerFactory getEntityManagerFactory() {
		init();
		return entityManagerFactory;
	}

	private void init() {
		if (entityManagerFactory == null)
			synchronized (this) {
				if (entityManagerFactory == null) {
					entityManagerFactory = Persistence.createEntityManagerFactory("default", configuration);
				}
			}

	}
	
}
