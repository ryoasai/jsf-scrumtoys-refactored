/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.demo.scrum.application.scrum_management;

import javax.ejb.Local;
import jsf2.demo.scrum.domain.project.Project;
import jsf2.demo.scrum.domain.sprint.Sprint;
import jsf2.demo.scrum.domain.story.Story;
import jsf2.demo.scrum.domain.task.Task;

/**
 *
 * @author Ryo
 */
@Local
public interface ScrumManager {

    void reset();
    
    Project getCurrentProject();

    void setCurrentProject(Project currentProject);
    
    void saveCurrentProject();

    void removeProject(Project project);

    Sprint getCurrentSprint();

    void setCurrentSprint(Sprint currentSprint);

    void saveCurrentSprint();

    void removeSprint(Sprint sprint);

    Story getCurrentStory();
    
    void setCurrentStory(Story currentStory);
    
    void saveCurrentStory();

    void removeStory(Story story);

    Task getCurrentTask();

    void saveCurrentTask();

    void removeTask(Task task);
    
    void setCurrentTask(Task currentTask);
}
