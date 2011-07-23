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

import jsf2.demo.scrum.domain.project.Project;
import jsf2.demo.scrum.domain.sprint.Sprint;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import jsf2.demo.scrum.domain.sprint.SprintRepository;
import jsf2.demo.scrum.infra.context.ViewScoped;
import jsf2.demo.scrum.infra.entity.Current;
import jsf2.demo.scrum.infra.manager.BaseCrudManager;
import jsf2.demo.scrum.infra.repository.Repository;

/**
 * @author Dr. Spock (spock at dev.java.net)
 */
@Named
@ConversationScoped
@Stateful
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class SprintManager extends BaseCrudManager<Long, Sprint> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject @Current
    Instance<Project> currentProject;
    
    @Inject
    private SprintRepository sprintRepository;

    @Produces @Current @Named
    public Sprint getCurrentSprint() {
        return getCurrentEntity();
    }

    @Produces @Named @ViewScoped
    public List<Sprint> getSprints() {
        if (getProject() != null) {
            return getProject().getSprints();
        } else {
            return Collections.emptyList();
        }
    }
    
    public Project getProject() {
        return currentProject.get();
    }   

    @Override
    public Sprint doCreate() {
        Sprint sprint = new Sprint();
        getProject().addSprint(sprint);
        
        return sprint;
    }

    @Override
    protected void doRemove(Sprint sprint) {
        sprintRepository.remove(sprint);
        getProject().removeSprint(sprint);
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

        long count = sprintRepository.countOtherSprintsWithName(getProject(), getCurrentEntity(), newName);

        if (count > 0) {
            message = getFacesMessageForKey("sprint.form.label.name.unique").getSummary();
        }

        return message;
    }

    public String showStories(Sprint sprint) {
        setCurrentEntity(sprint);
        return "showStories";
    }

    public String showDashboard(Sprint sprint) {
        setCurrentEntity(sprint);
        return "showDashboard";
    }
    
    @Override
    protected Repository<Long, Sprint> getRepository() {
        return sprintRepository;
    }

}
