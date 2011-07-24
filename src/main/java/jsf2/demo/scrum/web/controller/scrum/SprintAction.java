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
package jsf2.demo.scrum.web.controller.scrum;

import jsf2.demo.scrum.domain.project.Project;
import jsf2.demo.scrum.domain.sprint.Sprint;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import jsf2.demo.scrum.application.scrum_management.ScrumManager;
import jsf2.demo.scrum.domain.sprint.SprintRepository;
import jsf2.demo.scrum.infra.context.ViewScoped;
import jsf2.demo.scrum.infra.web.controller.BaseCrudAction;

/**
 * @author Dr. Spock (spock at dev.java.net)
 */
@Named
@ConversationScoped
public class SprintAction extends BaseCrudAction<Long, Sprint> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    ScrumManager scrumManager;
    
    @Inject
    private SprintRepository sprintRepository;

    public Sprint getCurrentSprint() {
        return scrumManager.getCurrentSprint();
    }

    @Override
    protected void onSelectCurrentEntity(Sprint sprint) {
        scrumManager.setCurrentSprint(sprint);
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
        return scrumManager.getCurrentProject();
    }   

    @Override
    public Sprint doCreate() {
        return new Sprint();
    }
    
    @Override
    protected void doSave() {
        scrumManager.persistCurrentSprint();
    }

    @Override
    protected void doRemove(Sprint sprint) {
        scrumManager.removeSprint(sprint);
    }

    /*
     * This method can be pointed to by a validator methodExpression, such as:
     *
     * <h:inputText id="itName" value="#{sprintAction.currentSprint.name}" required="true"
     *   requiredMessage="#{i18n['sprint.form.label.name.required']}" maxLength="30" size="30"
     *   validator="#{sprintAction.checkUniqueSprintName}" />
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
    
}
