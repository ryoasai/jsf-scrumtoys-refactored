/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.demo.scrum.application.scrum_management.impl;

import javax.enterprise.context.Conversation;
import java.util.logging.Logger;
import jsf2.demo.scrum.domain.task.Task;
import jsf2.demo.scrum.domain.story.Story;
import jsf2.demo.scrum.domain.sprint.Sprint;
import jsf2.demo.scrum.domain.task.TaskRepository;
import jsf2.demo.scrum.domain.story.StoryRepository;
import jsf2.demo.scrum.domain.sprint.SprintRepository;
import jsf2.demo.scrum.domain.project.ProjectRepository;
import mockit.Expectations;
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
public class ScrumManagerImplTest {

    ScrumManagerImpl target = new ScrumManagerImpl();
    
    @Mocked
    ProjectRepository projectRepository;

    @Mocked
    SprintRepository sprintRepository;

    @Mocked
    StoryRepository storyRepository;

    @Mocked
    TaskRepository taskRepository;

    @Mocked
    Conversation conversation;
    
    Project currentProject = new Project();
    Sprint currentSprint = new Sprint();
    Story currentStory = new Story();
    Task currentTask  = new Task();

    Logger logger = Logger.getLogger(ScrumManagerImpl.class.getName());
    
    @Before
    public void setUp() {
        target.projectRepository = projectRepository;
        target.sprintRepository = sprintRepository;
        target.storyRepository = storyRepository;
        target.taskRepository = taskRepository;

        target.conversation = conversation;

        target.logger = logger;
    }
    
    //==============================================================
    // project
    //==============================================================

    @Test
    public void getSetCurrentProject() {
        new Expectations() {{
            projectRepository.toManaged(currentProject); result = currentProject;
            conversation.isTransient(); result = true;
            conversation.begin();
        }};

        assertThat(target.getCurrentProject(), is(nullValue()));
        
        target.setCurrentProject(currentProject);
        
        assertThat(target.getCurrentProject(), is(currentProject));
        assertThat(target.getCurrentSprint(), is(nullValue()));
        assertThat(target.getCurrentStory(), is(nullValue()));
        assertThat(target.getCurrentTask(), is(nullValue()));
    }

    @Test
    public void saveCurrentProject_insert() {
        new Expectations(currentProject) {{
            projectRepository.toManaged(currentProject); result = currentProject;
            currentProject.isNew(); result = true;
            projectRepository.persist(currentProject);
        }};

        target.setCurrentProject(currentProject);
        target.saveCurrentProject();
    }    

    @Test
    public void saveCurrentProject_update() {
        new Expectations(currentProject) {{
            projectRepository.toManaged(currentProject); result = currentProject;
            currentProject.isNew(); result = false;
        }};

        target.setCurrentProject(currentProject);
        target.saveCurrentProject();
    }    

    @Test
    public void removeProject() {
        new Expectations() {{
            projectRepository.remove(currentProject);
        }};

        target.removeProject(currentProject);
    }     
 
    //==============================================================
    // sprint
    //==============================================================

    @Test
    public void getSetCurrentSprint() {
        new Expectations() {{
            projectRepository.toManaged(currentProject); result = currentProject;
            sprintRepository.toManaged(currentSprint); result = currentSprint;
        }};

        assertThat(target.getCurrentSprint(), is(nullValue()));

        target.setCurrentProject(currentProject);
        target.setCurrentSprint(currentSprint);
        
        assertThat(target.getCurrentProject(), is(currentProject));
        assertThat(target.getCurrentSprint(), is(currentSprint));
        assertThat(target.getCurrentStory(), is(nullValue()));
        assertThat(target.getCurrentTask(), is(nullValue()));
    }

    @Test
    public void saveCurrentSprint_insert() {
        new Expectations(currentProject, currentSprint) {{
            projectRepository.toManaged(currentProject); result = currentProject;
            sprintRepository.toManaged(currentSprint); result = currentSprint;

            currentSprint.isNew(); result = true;
            currentProject.addSprint(currentSprint);
        }};

        target.setCurrentProject(currentProject);
        target.setCurrentSprint(currentSprint);
        target.saveCurrentSprint();
    }    

    @Test
    public void saveCurrentSprint_update() {
        new Expectations(currentSprint) {{
            projectRepository.toManaged(currentProject); result = currentProject;
            sprintRepository.toManaged(currentSprint); result = currentSprint;

            currentSprint.isNew(); result = false;
        }};

        target.setCurrentProject(currentProject);
        target.setCurrentSprint(currentSprint);
        target.saveCurrentSprint();
    }    

    @Test
    public void removeSprint() {
        new Expectations(currentProject) {{
            projectRepository.toManaged(currentProject); result = currentProject;
            currentProject.removeSprint(currentSprint);
        }};

        target.setCurrentProject(currentProject);
        target.removeSprint(currentSprint);
    }

    //==============================================================
    // story
    //==============================================================

    @Test
    public void getSetCurrentStory() {
        new Expectations() {{
            projectRepository.toManaged(currentProject); result = currentProject;
            sprintRepository.toManaged(currentSprint); result = currentSprint;
            storyRepository.toManaged(currentStory); result = currentStory;
        }};

        assertThat(target.getCurrentSprint(), is(nullValue()));

        target.setCurrentProject(currentProject);
        target.setCurrentSprint(currentSprint);
        target.setCurrentStory(currentStory);
        
        assertThat(target.getCurrentProject(), is(currentProject));
        assertThat(target.getCurrentSprint(), is(currentSprint));
        assertThat(target.getCurrentStory(), is(currentStory));
        assertThat(target.getCurrentTask(), is(nullValue()));
    }

    @Test
    public void saveCurrentStory_insert() {
        new Expectations(currentSprint, currentStory) {{
            projectRepository.toManaged(currentProject); result = currentProject;
            sprintRepository.toManaged(currentSprint); result = currentSprint;
            storyRepository.toManaged(currentStory); result = currentStory;

            currentStory.isNew(); result = true;
            currentSprint.addStory(currentStory);
        }};

        target.setCurrentProject(currentProject);
        target.setCurrentSprint(currentSprint);
        target.setCurrentStory(currentStory);
        
        target.saveCurrentStory();
    }    

    @Test
    public void saveCurrentStory_update() {
        new Expectations(currentStory) {{
            projectRepository.toManaged(currentProject); result = currentProject;
            sprintRepository.toManaged(currentSprint); result = currentSprint;
            storyRepository.toManaged(currentStory); result = currentStory;

            currentStory.isNew(); result = false;
        }};

        target.setCurrentProject(currentProject);
        target.setCurrentSprint(currentSprint);
        target.setCurrentStory(currentStory);
        
        target.saveCurrentStory();
    }    

    @Test
    public void removeStory() {
        new Expectations(currentSprint) {{
            projectRepository.toManaged(currentProject); result = currentProject;
            sprintRepository.toManaged(currentSprint); result = currentSprint;

            currentSprint.removeStory(currentStory);
        }};

        target.setCurrentProject(currentProject);
        target.setCurrentSprint(currentSprint);

        target.removeStory(currentStory);
    }    
    

    //==============================================================
    // task
    //==============================================================

    @Test
    public void getSetCurrentTask() {
        new Expectations() {{
            projectRepository.toManaged(currentProject); result = currentProject;
            sprintRepository.toManaged(currentSprint); result = currentSprint;
            storyRepository.toManaged(currentStory); result = currentStory;
            taskRepository.toManaged(currentTask); result = currentTask;
        }};

        assertThat(target.getCurrentSprint(), is(nullValue()));

        target.setCurrentProject(currentProject);
        target.setCurrentSprint(currentSprint);
        target.setCurrentStory(currentStory);
        target.setCurrentTask(currentTask);
        
        assertThat(target.getCurrentProject(), is(currentProject));
        assertThat(target.getCurrentSprint(), is(currentSprint));
        assertThat(target.getCurrentStory(), is(currentStory));
        assertThat(target.getCurrentTask(), is(currentTask));
    }

    @Test
    public void saveCurrentTask_insert() {
        new Expectations(currentStory, currentTask) {{
            projectRepository.toManaged(currentProject); result = currentProject;
            sprintRepository.toManaged(currentSprint); result = currentSprint;
            storyRepository.toManaged(currentStory); result = currentStory;
            taskRepository.toManaged(currentTask); result = currentTask;
            
            currentTask.isNew(); result = true;
            currentStory.addTask(currentTask);
        }};

        target.setCurrentProject(currentProject);
        target.setCurrentSprint(currentSprint);
        target.setCurrentStory(currentStory);
        target.setCurrentTask(currentTask);
        
        target.saveCurrentTask();
    }    

    @Test
    public void saveCurrentTask_update() {
        new Expectations(currentStory, currentTask) {{
            projectRepository.toManaged(currentProject); result = currentProject;
            sprintRepository.toManaged(currentSprint); result = currentSprint;
            storyRepository.toManaged(currentStory); result = currentStory;
            taskRepository.toManaged(currentTask); result = currentTask;

            currentTask.isNew(); result = false;
        }};

        target.setCurrentProject(currentProject);
        target.setCurrentSprint(currentSprint);
        target.setCurrentStory(currentStory);
        target.setCurrentTask(currentTask);
        
        target.saveCurrentTask();
    }    

    @Test
    public void removeTask() {
        new Expectations(currentStory) {{
            projectRepository.toManaged(currentProject); result = currentProject;
            sprintRepository.toManaged(currentSprint); result = currentSprint;
            storyRepository.toManaged(currentStory); result = currentStory;

            currentStory.removeTask(currentTask);
        }};

        target.setCurrentProject(currentProject);
        target.setCurrentSprint(currentSprint);
        target.setCurrentStory(currentStory);
        
        target.removeTask(currentTask);
    }     
}
