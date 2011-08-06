/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.demo.scrum.web.controller.scrum;

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
public class ProjectActionTest {

    ProjectAction target = new ProjectAction();
    
    @Mocked
    ScrumManager scrumManager;

    @Mocked
    ProjectRepository projectRepository;
    
    Project project = new Project();

    @Before
    public void setUp() {
        target.scrumManager = scrumManager;
        target.projectRepository = projectRepository;
    }

    @Test
    public void getCurrentProject() {
        new Expectations() {{
            scrumManager.getCurrentProject(); result = project;
        }};

        Project result = target.getCurrentProject();
        assertThat(result, is(project));
    }

    @Test
    public void setCurrentProject() {
        new Expectations() {{
            scrumManager.setCurrentProject(project);
        }};

        target.setCurrentProject(project);
    }

    @Test
    public void getProjects() {
        new Expectations() {{
            projectRepository.findByNamedQuery("project.getAll"); result = Arrays.asList(project);
        }};

        List<Project> projects = target.getProjects();
        assertThat(projects.size(), is(1));
        assertThat(projects.contains(project), is(true));
    }

   @Test
    public void showSprint() {
        new Expectations() {{
            scrumManager.setCurrentProject(project);
        }};

        String view = target.showSprints(project);
        assertThat(view, is("/sprint/show?faces-redirect=true"));
    }

    @Test
    public void reset() {
        new Expectations() {{
            scrumManager.reset();
        }};

        target.reset();
    }

    @Test
    public void checkUniqueProjectName_valid() {
        new Expectations(target) {{
            scrumManager.getCurrentProject(); result = project;
            projectRepository.countOtherProjectsWithName(project, "test"); result = 0L;
        }};
        
        target.checkUniqueProjectName(null, null, "test");
    }
        
    @Test(expected=ValidatorException.class)
    public void checkUniqueProjectName_invalid() {
        new Expectations(target) {{
            scrumManager.getCurrentProject(); result = project;
            projectRepository.countOtherProjectsWithName(project, "test"); result = 1L;
            Deencapsulation.invoke(target, "getMessageForKey", "project.form.label.name.unique"); result = "test value";
        }};
        
        target.checkUniqueProjectName(null, null, "test");
    }     
}
