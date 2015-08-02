package com.minorityhobbies.util.ee;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

/**
 * Java Persistence API (JPA) data-access-object. Provides standard CRUD (create, read, update, delete) operations
 * for any JPA entity. Subclasses can simply delegate to these methods for simple cases. For more complex operations,
 * a combination of these methods may be used. For very specific cases, the subclass can perform an operation directly
 * on the {@link EntityManager}.
 *
 * In a Java EE container, the entity manager will be automatically injected into this class so there is no need
 * to do so in a subclass.
 */
public class AbstractJPARepository {
	@PersistenceContext
	EntityManager entityManager;

	/**
	 * Creates a new AbstractJPARepository instance.
	 */
	public AbstractJPARepository() {
		super();
	}

	/**
	 * Generally only required for testing outside of a Java EE environment.
	 *
	 * @param entityManager	The entity manager for this class to use.
	 */
	public AbstractJPARepository(EntityManager entityManager) {
		super();
		this.entityManager = entityManager;
	}

	protected final void persist(Object... entities) {
		try {
			for (Object entity : entities) {
				entityManager.persist(entity);
			}
		} catch (RuntimeException e) {
			throw e;
		}
	}
	
	protected final void merge(Object... entities) {
		try {
			for (Object entity : entities) {
				entityManager.merge(entity);
			}
		} catch (RuntimeException e) {
			throw e;
		}
	}

	protected final <T> int deleteById(long id, Class<T> type) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaDelete<T> delete = cb.createCriteriaDelete(type);
		Root<T> root = delete.getRoot();
		delete = delete.where(cb.equal(root.get("id"), id));
		
		Query q = entityManager.createQuery(delete);
		return q.executeUpdate();
	}
	
	protected final <T> T getUnique(String jpaQuery, Class<T> type,
			Object... params) {
		TypedQuery<T> query = getEntityManager().createQuery(jpaQuery, type);
		for (int i = 1; i <= params.length; i++) {
			query.setParameter(i, params[i-1]);
		}
		return query.getSingleResult();
	}

	protected final <T> T getUniqueById(Class<T> type, Object id) {
		return entityManager.find(type, id);
	}
			
	protected final <T> T getNullableUnique(String jpaQuery, Class<T> type,
			Object... params) {
		try {
			return getUnique(jpaQuery, type, params);
		} catch (NoResultException e) {
			return null;
		}
	}
	
	protected final <T> List<T> get(String jpaQuery, Class<T> type,
			Object... params) {
		TypedQuery<T> query = getEntityManager().createQuery(jpaQuery, type);
		for (int i = 1; i <= params.length; i++) {
			query.setParameter(i, params[i-1]);
		}
		return query.getResultList();
	}

	protected final <T> List<T> getWithLimit(String jpaQuery, Class<T> type,
			int limit, Object... params) {
		TypedQuery<T> query = getEntityManager().createQuery(jpaQuery, type);
		for (int i = 1; i <= params.length; i++) {
			query.setParameter(i, params[i-1]);
		}
		return query.setMaxResults(limit).getResultList();
	}

	protected final EntityManager getEntityManager() {
		return entityManager;
	}
}
