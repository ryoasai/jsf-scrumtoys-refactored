/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.

Oracle and Java are registered trademarks of Oracle and/or its affiliates.
Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package jsf2.demo.scrum.web.controller;

import jsf2.demo.scrum.infra.manager.AbstractManager;
import jsf2.demo.scrum.domain.project.Project;
import jsf2.demo.scrum.domain.sprint.Sprint;

import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import jsf2.demo.scrum.domain.sprint.SprintRepository;

/**
 * @author Dr. Spock (spock at dev.java.net)
 */
@Named
@SessionScoped
public class SprintManager extends AbstractManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private Sprint currentSprint;
    private List<Sprint> sprints;
    
    @Inject
    private ProjectManager projectManager;
    
    @Inject
    private SprintRepository sprintRepository;

    @PostConstruct
    public void construct() {
        getLogger(getClass()).log(Level.INFO, "new intance of sprintManager in conversation");

        init();
    }

    @PreDestroy
    public void destroy() {
        getLogger(getClass()).log(Level.INFO, "destroy intance of sprintManager in conversation");
    }
    
    public void init() {
        Sprint sprint = new Sprint();
        Project currentProject = projectManager.getCurrentProject();
        sprint.setProject(currentProject);
        setCurrentSprint(sprint);

        if (currentProject != null) {
            sprints = currentProject.getSprints();
        } else {
            sprints = Collections.emptyList();
        }
    }

    public Sprint getCurrentSprint() {
        return currentSprint;
    }

    public void setCurrentSprint(Sprint currentSprint) {
        this.currentSprint = currentSprint;
    }

    public List<Sprint> getSprints() {
        return this.sprints;
    }

    public Project getProject() {
        return projectManager.getCurrentProject();
    }   
    
    public void setProject(Project project) {
        projectManager.setCurrentProject(project);
    }
    
    public String create() {
        Sprint sprint = new Sprint();
        sprint.setProject(projectManager.getCurrentProject());
        setCurrentSprint(sprint);

        return "create";
    }

    public String edit(Sprint sprint) {
        setCurrentSprint(sprint);

        return "edit";
    }
        
    public String save() {
        if (currentSprint != null) {
            Sprint merged = sprintRepository.save(currentSprint);
            projectManager.getCurrentProject().addSprint(merged);
        }
        
        init(); // TODO. better to user update event.
                
        return "show";
    }

    public String remove(Sprint sprint) {
        if (sprint != null) {
            sprintRepository.remove(sprint);
            projectManager.getCurrentProject().removeSpring(sprint);
        }

        init(); // TODO. better to user update event.
                
        return "show";
    }

    public String cancelEdit() {
        return "show";
    }
        
    /*
     * This method can be pointed to by a validator methodExpression, such as:
     *
     * <h:inputText id="itName" value="#{sprintManager.currentSprint.name}" required="true"
     *   requiredMessage="#{i18n['sprint.form.label.name.required']}" maxLength="30" size="30"
     *   validator="#{sprintManager.checkUniqueSprintName}" />
     */
    public void checkUniqueSprintNameFacesValidatorMethod(FacesContext context, UIComponent component, Object newValue) {

        final String newName = (String) newValue;
        String message = checkUniqueSprintNameApplicationValidatorMethod(newName);
        if (null != message) {
            throw new ValidatorException(getFacesMessageForKey("sprint.form.label.name.unique"));
        }
    }


    /*
     * This method is called by the JSR-303 SprintNameUniquenessConstraintValidator.
     * If it returns non-null, the result must be interpreted as the localized
     * validation message.
     *
     */
    public String checkUniqueSprintNameApplicationValidatorMethod(String newValue) {
        String message = null;

        final String newName = (String) newValue;

        long count = sprintRepository.countOtherSprintsWithName(projectManager.getCurrentProject(), currentSprint, newName);

        if (count > 0) {
            message = getFacesMessageForKey("sprint.form.label.name.unique").getSummary();
        }

        return message;
    }

    public String showStories(Sprint sprint) {
        setCurrentSprint(sprint);
        return "showStories";
    }

    public String showDashboard(Sprint sprint) {
        setCurrentSprint(sprint);
        return "showDashboard";
    }

}
