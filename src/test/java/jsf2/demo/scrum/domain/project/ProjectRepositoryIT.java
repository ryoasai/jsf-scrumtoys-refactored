/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.demo.scrum.domain.project;

import org.junit.Before;
import com.googlecode.jeeunit.Transactional;
import com.googlecode.jeeunit.JeeunitRunner;
import java.util.List;
import java.util.logging.Logger;
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
public class ProjectRepositoryIT {

    @PersistenceContext
    EntityManager em;
    
    @Inject
    ProjectRepository target;

    Project project1; 
    Project project2;
    Project project3;

    @Before
    public void setUp() {
        project1 = new Project("test1", Dates.create(2011, 8, 6));
        project2 = new Project("test2", Dates.create(2011, 8, 7));
        project3 = new Project("test3", Dates.create(2011, 8, 8));
        
        em.persist(project1);
        em.persist(project2);
        em.persist(project3);
    }

    @Test
    public void crud() throws Exception {
        Project project = new Project("new project", Dates.create(2011, 8, 6));
        target.persist(project);
        
        em.flush();

        project.setName("renamed");
        project.setEndDate(Dates.create(2012, 8, 6));
        
        em.flush();
        
        target.remove(project);
        em.flush();
    }
        
    @Test
    public void getAll() throws Exception {
        List<Project> projectList = target.findByNamedQuery("project.getAll");
        
        assertThat(projectList.size(), is(3));
    }
    
    @Test
    public void countOtherProjectsWithName() throws Exception {
        assertThat(target.countOtherProjectsWithName(project1, "test2"), is(1L));
        assertThat(target.countOtherProjectsWithName(project1, "test*"), is(0L));
    }
}
