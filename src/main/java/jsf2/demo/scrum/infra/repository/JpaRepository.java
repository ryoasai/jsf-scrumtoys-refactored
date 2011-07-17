/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.demo.scrum.infra.repository;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jsf2.demo.scrum.infra.entity.PersistentEntity;

/**
 *
 * @author Ryo
 */
public abstract class JpaRepository<K extends Serializable, E extends PersistentEntity<K>> implements Repository<K, E> {

    private Class<E> entityClass;
   
    @PersistenceContext
    protected EntityManager em;

    public JpaRepository(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public E findById(K id) {
        return em.find(entityClass, id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<E> findByNamedQuery(String queryName) {
        return (List<E>) em.createNamedQuery(queryName).getResultList();
    }

    @Override
    public E save(E entity) {
        if (entity.isNew()) {
            em.persist(entity);
            return entity;
        } else if (!em.contains(entity)) {
            return em.merge(entity);
        } else {
            return entity;
        }
    }

    @Override
    public void remove(E entity) {
        if (em.contains(entity)) {
            em.remove(entity);
        } else {
            em.remove(em.merge(entity));
        }
    }
}
