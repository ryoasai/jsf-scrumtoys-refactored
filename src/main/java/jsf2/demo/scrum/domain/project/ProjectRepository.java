/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.demo.scrum.domain.project;

import javax.ejb.Stateless;
import javax.persistence.Query;
import jsf2.demo.scrum.infra.repository.JpaRepository;

/**
 *
 * @author Ryo
 */
@Stateless
public class ProjectRepository extends JpaRepository<Long, Project> {

    public ProjectRepository() {
        super(Project.class);
    }    
    
    public long countOtherProjectsWithName(Project project, String newName) {
        
        Query query = em.createNamedQuery(project.isNew() ? "project.new.countByName" : "project.countByName");
        
        query.setParameter("name", newName);
        if (!project.isNew()) {
            query.setParameter("currentProject", project);
        }

        return (Long) query.getSingleResult();
    }
}
