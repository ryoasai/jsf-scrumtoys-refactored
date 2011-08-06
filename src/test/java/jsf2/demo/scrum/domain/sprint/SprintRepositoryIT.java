/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.demo.scrum.domain.sprint;

import jsf2.demo.scrum.domain.project.*;
import org.junit.Before;
import com.googlecode.jeeunit.Transactional;
import com.googlecode.jeeunit.JeeunitRunner;
import java.util.List;
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
public class SprintRepositoryIT {

    @PersistenceContext
    EntityManager em;
    
    @Inject
    SprintRepository target;

    Project project; 

    Sprint sprint1;
    Sprint sprint2;
    
    @Before
    public void setUp() {
        project = new Project("test1", Dates.create(2011, 8, 6));
        
        sprint1 = new Sprint("sprint1");
        sprint2 = new Sprint("sprint2");
  
        project.addSprint(sprint1);
        project.addSprint(sprint2);
        
        em.persist(project);
    }

    @Test
    public void crud() throws Exception {
        Sprint sprint = new Sprint("new sprint");
        
        project.addSprint(sprint);
        
        em.flush();

        sprint.setName("renamed");
        sprint.setEndDate(Dates.create(2012, 8, 6));
        sprint.setGoals("goal");
        sprint.setDailyMeetingTime(Dates.create(2012, 8, 6, 1, 2, 3));
        em.flush();
        
        project.removeSprint(sprint);
        em.flush();
    }
        
    @Test
    public void countOtherSprintsWithName() throws Exception {
        assertThat(target.countOtherSprintsWithName(project, sprint1, "sprint2"), is(1L));
        assertThat(target.countOtherSprintsWithName(project, sprint1, "test*"), is(0L));
    }
}
