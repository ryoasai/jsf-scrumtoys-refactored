/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.demo.scrum.web.controller.scrum;

import jsf2.demo.scrum.domain.story.Story;
import jsf2.demo.scrum.domain.story.StoryRepository;
import mockit.Cascading;
import jsf2.demo.scrum.domain.sprint.Sprint;
import javax.faces.validator.ValidatorException;
import mockit.Deencapsulation;
import java.util.List;
import java.util.Arrays;
import mockit.Expectations;
import jsf2.demo.scrum.application.scrum_management.ScrumManager;
import mockit.Mocked;
import jsf2.demo.scrum.domain.project.Project;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Ryo
 */
public class StoryActionTest {

    StoryAction target = new StoryAction();
    
    @Cascading
    ScrumManager scrumManager;

    @Mocked
    StoryRepository storyRepository;
    
    
    Project project = new Project();
    Sprint sprint = new Sprint();
    Story story = new Story();

    @Before
    public void setUp() {
        target.scrumManager = scrumManager;
        target.storyRepository = storyRepository;
    }

    @Test
    public void getStories() {
        new Expectations() {{
            scrumManager.getCurrentSprint(); result = sprint;
            scrumManager.getCurrentSprint().getStories(); result = Arrays.asList(story);
        }};

        List<Story> stories = target.getStories();
        assertThat(stories.size(), is(1));
        assertThat(stories.contains(story), is(true));
    }

    @Test
    public void getSprints_noCurrentSprint() {
        new Expectations() {{
            scrumManager.getCurrentSprint(); result = null;
        }};

        List<Story> stories = target.getStories();
        assertThat(stories.size(), is(0));
    }
    
   @Test
    public void showTasks() {
        new Expectations() {{
            scrumManager.setCurrentStory(story);
        }};

        String view = target.showTasks(story);
        assertThat(view, is("/task/show?faces-redirect=true"));
    }

    @Test
    public void checkUniqueStoryName_valid() {
        new Expectations(target) {{
            scrumManager.getCurrentSprint(); result = sprint;
            scrumManager.getCurrentStory(); result = story;

            storyRepository.countOtherStoriesWithName(sprint, story, "test"); result = 0L;
        }};
        
        target.checkUniqueStoryName(null, null, "test");
    }
        
    @Test(expected=ValidatorException.class)
    public void checkUniqueStoryName_invalid() {
        new Expectations(target) {{
            scrumManager.getCurrentSprint(); result = sprint;
            scrumManager.getCurrentStory(); result = story;

            storyRepository.countOtherStoriesWithName(sprint, story, "test"); result = 1L;
            Deencapsulation.invoke(target, "getMessageForKey", "story.form.label.name.unique"); result = "test value";
        }};
        
        target.checkUniqueStoryName(null, null, "test");
    }     
}
