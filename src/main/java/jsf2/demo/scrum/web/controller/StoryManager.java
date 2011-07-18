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

import jsf2.demo.scrum.domain.sprint.Sprint;
import jsf2.demo.scrum.domain.story.Story;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import jsf2.demo.scrum.domain.story.StoryRepository;
import jsf2.demo.scrum.infra.manager.BaseCrudManager;

@Named
@SessionScoped
public class StoryManager extends BaseCrudManager<Story> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private SprintManager sprintManager;
   
    @Inject
    private StoryRepository storyRepository;

    @Override
    public void init() {
        Sprint currentSprint = sprintManager.getCurrentSprint();

        if (currentSprint != null) {
            Story story = new Story();
            story.setSprint(currentSprint);
            setCurrentEntity(story);
            
            setEntities(currentSprint.getStories());
        } else {
            List<Story> stories = Collections.emptyList();
            setEntities(stories);
        }
    }

    public Story getCurrentStory() {
        return getCurrentEntity();
    }

    public List<Story> getStories() {
        return getEntities();
    }

    public Sprint getSprint() {
        return sprintManager.getCurrentSprint();
    }

    @Override
    protected Story doCreate() {
        Story story = new Story();
        story.setSprint(sprintManager.getCurrentSprint());
        return story;
    }

    @Override
    protected void doSave(Story story) {
        Story merged = storyRepository.save(story);
        sprintManager.getCurrentSprint().addStory(merged);
    }

    @Override
    protected void doRemove(Story story) {
         storyRepository.remove(story);
         sprintManager.getCurrentSprint().removeStory(story);
    }
    
    public void checkUniqueStoryName(FacesContext context, UIComponent component, Object newValue) {
        final String newName = (String) newValue;

        long count = storyRepository.countOtherStoriesWithName(sprintManager.getCurrentSprint(), getCurrentEntity(), newName);
        if (count > 0) {
            throw new ValidatorException(getFacesMessageForKey("story.form.label.name.unique"));
        }
    }

    public String showTasks(Story story) {
        setCurrentEntity(story);
        return "showTasks";
    }
}
