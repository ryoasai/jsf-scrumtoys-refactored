/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.demo.scrum.domain.story;

import jsf2.demo.scrum.domain.sprint.*;
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
public class StoryRepositoryIT {

    @PersistenceContext
    EntityManager em;
    
    @Inject
    StoryRepository target;

    Project project; 
    Sprint sprint;

    Story story1;
    Story story2;

    @Before
    public void setUp() {
        project = new Project("project", Dates.create(2011, 8, 6));
        sprint = new Sprint("sprint");
        project.addSprint(sprint);
        
        story1 = new Story("story1");
        story2 = new Story("story2");

        sprint.addStory(story1);
        sprint.addStory(story2);
        
        em.persist(project);
    }

    @Test
    public void crud() throws Exception {
        Story story = new Story("new story");
        
        sprint.addStory(story);
        
        em.flush();

        story.setName("renamed");
        story.setEndDate(Dates.create(2012, 8, 6));
        story.setPriority(3);
        story.setAcceptance("acceptance");
        em.flush();
        
        sprint.removeStory(story);
        em.flush();
    }
        
    @Test
    public void countOtherStoriesWithName() throws Exception {
        assertThat(target.countOtherStoriesWithName(sprint, story1, "story2"), is(1L));
        assertThat(target.countOtherStoriesWithName(sprint, story1, "test*"), is(0L));
    }
}
