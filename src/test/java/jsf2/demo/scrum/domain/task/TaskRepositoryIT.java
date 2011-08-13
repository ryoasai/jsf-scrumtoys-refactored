/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.demo.scrum.domain.task;

import jsf2.demo.scrum.domain.story.*;
import jsf2.demo.scrum.domain.sprint.*;
import jsf2.demo.scrum.domain.project.*;
import org.junit.Before;
import com.googlecode.jeeunit.Transactional;
import com.googlecode.jeeunit.JeeunitRunner;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jsf2.demo.scrum.infra.util.Dates;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Ryo
 */
@RunWith(JeeunitRunner.class)
@Transactional
public class TaskRepositoryIT {

    @PersistenceContext
    EntityManager em;
    
    @Inject
    TaskRepository target;

    Project project; 
    Sprint sprint;
    Story story;

    Task task1;
    Task task2;

    @Before
    public void setUp() {
        project = new Project("project", Dates.create(2011, 8, 6));
        sprint = new Sprint("sprint");
        story = new Story("story");

        project.addSprint(sprint);
        
        story = new Story("story1");
        sprint.addStory(story);
        
        task1 = new Task("task1");
        task2 = new Task("task2");

        story.addTask(task1);
        story.addTask(task2);
        
        em.persist(project);
    }

    @Test
    public void crud() throws Exception {
        Task task = new Task("new task");
        
        story.addTask(task);
        
        em.flush();

        task.setName("renamed");
        task.setEndDate(Dates.create(2012, 8, 6));
        task.setStatus(TaskStatus.DONE);
        em.flush();
        
        story.removeTask(task);
        em.flush();
    }
        
    @Test
    public void countOtherTasksWithName() throws Exception {
        assertThat(target.countOtherTasksWithName(story, task1, "task2"), is(1L));
        assertThat(target.countOtherTasksWithName(story, task1, "test*"), is(0L));
    }
}
