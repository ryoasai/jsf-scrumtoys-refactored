/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.demo.scrum.web.controller.scrum;

import jsf2.demo.scrum.domain.task.Task;
import jsf2.demo.scrum.domain.task.TaskRepository;
import jsf2.demo.scrum.domain.story.Story;
import jsf2.demo.scrum.domain.story.StoryRepository;
import jsf2.demo.scrum.domain.sprint.SprintRepository;
import mockit.Cascading;
import jsf2.demo.scrum.domain.sprint.Sprint;
import javax.faces.validator.ValidatorException;
import mockit.Deencapsulation;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Arrays;
import jsf2.demo.scrum.domain.project.ProjectRepository;
import mockit.Expectations;
import jsf2.demo.scrum.application.scrum_management.ScrumManager;
import mockit.Mocked;
import jsf2.demo.scrum.domain.project.Project;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Ryo
 */
public class TaskActionTest {

    TaskAction target = new TaskAction();
    
    @Cascading
    ScrumManager scrumManager;

    @Mocked
    TaskRepository taskRepository;
    

    Sprint sprint = new Sprint();
    Story story = new Story();
    Task task = new Task();

    @Before
    public void setUp() {
        target.scrumManager = scrumManager;
        target.taskRepository = taskRepository;
    }

    @Test
    public void getTasks() {
        new Expectations() {{
            scrumManager.getCurrentStory(); result = story;
            scrumManager.getCurrentStory().getTasks(); result = Arrays.asList(task);
        }};

        List<Task> tasks = target.getTasks();
        assertThat(tasks.size(), is(1));
        assertThat(tasks.contains(task), is(true));
    }

    @Test
    public void getTasks_noCurrentStory() {
        new Expectations() {{
            scrumManager.getCurrentStory(); result = null;
        }};

        List<Task> tasks = target.getTasks();
        assertThat(tasks.size(), is(0));
    }   
    
   @Test
    public void showStories() {
        new Expectations() {{
            scrumManager.getCurrentSprint(); result = sprint;
            scrumManager.setCurrentSprint(sprint);
        }};

        String view = target.showStories();
        assertThat(view, is("/story/show?faces-redirect=true"));
    }
 
    @Test
    public void checkUniqueStoryName_valid() {
        new Expectations(target) {{
            scrumManager.getCurrentStory(); result = story;
            scrumManager.getCurrentTask(); result = task;

            taskRepository.countOtherTasksWithName(story, task, "test"); result = 0L;
        }};
        
        target.checkUniqueTaskName(null, null, "test");
    }
        
    @Test(expected=ValidatorException.class)
    public void checkUniqueStoryName_invalid() {
        new Expectations(target) {{
            scrumManager.getCurrentStory(); result = story;
            scrumManager.getCurrentTask(); result = task;

            taskRepository.countOtherTasksWithName(story, task, "test"); result = 1L;
            Deencapsulation.invoke(target, "getMessageForKey", "task.form.label.name.unique"); result = "test value";
        }};
        
        target.checkUniqueTaskName(null, null, "test");
    }     
}
