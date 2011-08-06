/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.demo.scrum.application.scrum_management;

import javax.enterprise.inject.spi.BeanManager;
import jsf2.demo.scrum.domain.project.*;
import com.googlecode.jeeunit.JeeunitRunner;
import java.util.List;
import javax.inject.Inject;
import jsf2.demo.scrum.domain.sprint.Sprint;
import jsf2.demo.scrum.domain.story.Story;
import jsf2.demo.scrum.domain.task.Task;
import jsf2.demo.scrum.domain.task.TaskStatus;
import jsf2.demo.scrum.infra.util.Dates;
import org.jboss.weld.context.http.HttpConversationContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Ryo
 */
@RunWith(JeeunitRunner.class)
// @Transactional Because the target statful bean uses extended context, test class
// cannot demarcate transaction boundary.
public class ScrumManagerIT {

    @Inject
    ScrumManager scrumManager;

    @Inject
    BeanManager beanManager;
    
    // workaround for activating conversation context during test.
    @Inject
    HttpConversationContext conversationContext;

    @Inject 
    ProjectRepository projectRepository;
    
    @Before
    public void setUp() {
        conversationContext.activate();
 
        // clean up all data before test.
        List<Project> projects = projectRepository.findByNamedQuery("project.getAll");
        for (Project project : projects) {
            projectRepository.remove(project);
        }
    }

    @After
    public void tearDown() {
        conversationContext.deactivate();
    }
        
    @Test
    public void projectManagement() throws Exception {
        Project project = new Project("new project", Dates.create(2011, 8, 6));
        scrumManager.setCurrentProject(project);
        
        // insert
        scrumManager.saveCurrentProject();
        
        project.setEndDate(Dates.create(2011, 9, 6));

        // update
        scrumManager.saveCurrentProject();
        
        // delete
        scrumManager.removeProject(project);
    }

    @Test
    public void sprintManagement() throws Exception {
        Project project = new Project("new project", Dates.create(2011, 8, 6));
        scrumManager.setCurrentProject(project);
        scrumManager.saveCurrentProject();

        Sprint sprint = new Sprint("new sprint");
        // insert
        scrumManager.setCurrentSprint(sprint);
        scrumManager.saveCurrentSprint();

        // update
        sprint.setEndDate(Dates.create(2011, 9, 6));
        sprint.setGainedStoryPoints(10);
        sprint.setIterationScope(3);
        sprint.setDailyMeetingTime(Dates.create(2011, 9, 6, 1, 1, 1));
        
        scrumManager.saveCurrentSprint();
        
        // delete
        scrumManager.removeSprint(sprint);
    }

    @Test
    public void storyManagement() throws Exception {
        Project project = new Project("new project", Dates.create(2011, 8, 6));
        scrumManager.setCurrentProject(project);
        scrumManager.saveCurrentProject();
       
        Sprint sprint = new Sprint("new sprint");
        scrumManager.setCurrentSprint(sprint);
        scrumManager.saveCurrentSprint();

        // insert
        Story story = new Story("new story");
        scrumManager.setCurrentStory(story);        
        scrumManager.saveCurrentStory();
        
        // update
        story.setEndDate(Dates.create(2011, 9, 6));
        story.setAcceptance("test");
        story.setPriority(3);
        
        scrumManager.saveCurrentStory();
        
        // delete
        scrumManager.removeStory(story);
    }
    
    @Test
    public void taskManagement() throws Exception {
        Project project = new Project("new project", Dates.create(2011, 8, 6));
        scrumManager.setCurrentProject(project);
        scrumManager.saveCurrentProject();
        
        Sprint sprint = new Sprint("new sprint");
        scrumManager.setCurrentSprint(sprint);
        scrumManager.saveCurrentSprint();

        Story story = new Story("new story");
        scrumManager.setCurrentStory(story);        
        scrumManager.saveCurrentStory();

        // insert
        Task task = new Task("new task");
        scrumManager.setCurrentTask(task);        
        scrumManager.saveCurrentTask();
                
        // update
        task.setEndDate(Dates.create(2011, 9, 6));
        task.setStatus(TaskStatus.WORKING);
        scrumManager.saveCurrentTask();
        
        // delete
        scrumManager.removeTask(task);
    }    
}
