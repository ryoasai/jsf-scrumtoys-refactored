/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.demo.scrum.web.controller.scrum;

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
public class SprintActionTest {

    SprintAction target = new SprintAction();
    
    @Cascading
    ScrumManager scrumManager;

    @Mocked
    SprintRepository sprintRepository;
    
    
    Project project = new Project();
    Sprint sprint = new Sprint();

    @Before
    public void setUp() {
        target.scrumManager = scrumManager;
        target.sprintRepository = sprintRepository;
    }

    @Test
    public void getSprints() {
        new Expectations() {{
            scrumManager.getCurrentProject(); result = project;
            scrumManager.getCurrentProject().getSprints(); result = Arrays.asList(sprint);
        }};

        List<Sprint> projects = target.getSprints();
        assertThat(projects.size(), is(1));
        assertThat(projects.contains(sprint), is(true));
    }

    @Test
    public void getSprints_noCurrentProject() {
        new Expectations() {{
            scrumManager.getCurrentProject(); result = null;
        }};

        List<Sprint> sprints = target.getSprints();
        assertThat(sprints.size(), is(0));
    }
        
   @Test
    public void showStories() {
        new Expectations() {{
            scrumManager.setCurrentSprint(sprint);
        }};

        String view = target.showStories(sprint);
        assertThat(view, is("/story/show?faces-redirect=true"));
    }

    @Test
    public void checkUniqueSprintNameFacesValidatorMethod_valid() {
        new Expectations(target) {{
            scrumManager.getCurrentProject(); result = project;
            scrumManager.getCurrentSprint(); result = sprint;

            sprintRepository.countOtherSprintsWithName(project, sprint, "test"); result = 0L;
        }};
        
        target.checkUniqueSprintNameFacesValidatorMethod(null, null, "test");
    }
        
    @Test(expected=ValidatorException.class)
    public void checkUniqueSprintNameFacesValidatorMethod_invalid() {
        new Expectations(target) {{
            scrumManager.getCurrentProject(); result = project;
            scrumManager.getCurrentSprint(); result = sprint;

            sprintRepository.countOtherSprintsWithName(project, sprint, "test"); result = 1L;
            Deencapsulation.invoke(target, "getMessageForKey", "sprint.form.label.name.unique"); result = "test value";
        }};
        
        target.checkUniqueSprintNameFacesValidatorMethod(null, null, "test");
    }     
}
