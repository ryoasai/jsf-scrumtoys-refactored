/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.demo.scrum.infra.repository;

import java.io.Serializable;
import java.util.List;
import jsf2.demo.scrum.infra.entity.PersistentEntity;

/**
 *
 * @author Ryo
 */
public interface Repository<K extends Serializable, E extends PersistentEntity<K>> {

    E findById(K id);

    List<E> findByNamedQuery(String queryName);

    E persist(E entity);

    void remove(E entity);
    
    void remove(K id);

}
