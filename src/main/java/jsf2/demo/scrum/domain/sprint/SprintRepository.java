/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.demo.scrum.domain.sprint;

import javax.ejb.Stateless;
import javax.persistence.Query;
import jsf2.demo.scrum.domain.project.Project;
import jsf2.demo.scrum.infra.repository.JpaRepository;

/**
 *
 * @author Ryo
 */
@Stateless
public class SprintRepository extends JpaRepository<Long, Sprint> {

    public SprintRepository() {
        super(Sprint.class);
    }

    public long countOtherSprintsWithName(Project project, Sprint sprint, String newName) {

        Query query = em.createNamedQuery((sprint.isNew()) ? "sprint.new.countByNameAndProject" : "sprint.countByNameAndProject");
        query.setParameter("name", newName);
        query.setParameter("project", project);
        if (!sprint.isNew()) {
            query.setParameter("currentSprint", sprint);
        }

        return (Long) query.getSingleResult();
    }
}
