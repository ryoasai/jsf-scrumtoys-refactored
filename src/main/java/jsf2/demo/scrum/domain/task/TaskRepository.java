/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.demo.scrum.domain.task;

import jsf2.demo.scrum.domain.story.*;
import javax.ejb.Stateless;
import javax.persistence.Query;
import jsf2.demo.scrum.infra.repository.JpaRepository;

/**
 *
 * @author Ryo
 */
@Stateless
public class TaskRepository extends JpaRepository<Long, Task> {

    public TaskRepository() {
        super(Task.class);
    }
        
    public long countOtherTasksWithName(Story story, Task task, String newName) {

        Query query = em.createNamedQuery((task.isNew()) ? "task.new.countByNameAndStory" : "task.countByNameAndStory");
        query.setParameter("name", newName);
        query.setParameter("story", story);
        if (!task.isNew()) {
            query.setParameter("currentTask", (!task.isNew()) ? task : null);
        }

        return (Long) query.getSingleResult();
    }    
    

}

