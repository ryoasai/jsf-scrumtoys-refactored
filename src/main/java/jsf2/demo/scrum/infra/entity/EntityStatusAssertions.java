/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.demo.scrum.infra.entity;

/**
 *
 * @author Ryo
 */
public class EntityStatusAssertions {
 
    private EntityStatusAssertions() {}

    public static void assertThatEntityIsNotNull(PersistentEntity<?> entity) {
        if (entity == null) throw new IllegalStateException("Entity is null.");
    }
        
    public static void assertThatEntityIsPersistable(PersistentEntity<?> entity) {
        if (entity == null || !entity.isNew()) throw new IllegalStateException("Entity " + entity + "cannot be made persistent.");
    }

    public static void assertThatEntityHasId(PersistentEntity<?> entity) {
        if (entity == null || entity.isNew()) throw new IllegalStateException("Entity " + entity + "does not have id.");
    }    
}
