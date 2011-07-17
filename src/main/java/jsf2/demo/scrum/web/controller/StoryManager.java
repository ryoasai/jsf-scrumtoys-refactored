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
import jsf2.demo.scrum.domain.sprint.Sprint;
import jsf2.demo.scrum.domain.story.Story;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import jsf2.demo.scrum.domain.story.StoryRepository;

@Named
@ConversationScoped
public class StoryManager extends AbstractManager implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private SprintManager sprintManager;
   
    @Inject
    private StoryRepository storyRepository;
    
    @Inject
    private Conversation conversation;
    
    private Story currentStory;
    
    private DataModel<Story> stories;
    private List<Story> storyList;

    @PostConstruct
    public void construct() {
        getLogger(getClass()).log(Level.INFO, "new intance of storyManager in conversation");

        init();
    }

    @PreDestroy
    public void destroy() {
        getLogger(getClass()).log(Level.INFO, "destroy intance of storyManager in conversation");
    }
    
    public void init() {

        Sprint currentSprint = sprintManager.getCurrentSprint();

        if (currentSprint != null) {
            Story story = new Story();
            setStoryList(new LinkedList<Story>(currentSprint.getStories()));
            story.setSprint(currentSprint);
            setCurrentStory(story);
        } else {
            setStoryList(new ArrayList<Story>());
        }
        
        stories = new ListDataModel<Story>(getStoryList());
    }

    public Story getCurrentStory() {
        return currentStory;
    }

    public void setCurrentStory(Story currentStory) {
        this.currentStory = currentStory;
    }

    public DataModel<Story> getStories() {
        if (sprintManager.getCurrentSprint() != null) {
            this.stories = new ListDataModel(sprintManager.getCurrentSprint().getStories());
            return stories;
        } else {
            return new ListDataModel<Story>();
        }
    }

    public void setStories(DataModel<Story> stories) {
        this.stories = stories;
    }

    public Sprint getSprint() {
        return sprintManager.getCurrentSprint();
    }

    public void setSprint(Sprint sprint) {
        sprintManager.setCurrentSprint(sprint);
    }

    /**
     * @return the storyList
     */
    public List<Story> getStoryList() {
        if (sprintManager.getCurrentSprint() != null) {
            this.storyList = sprintManager.getCurrentSprint().getStories();
        }
        return this.storyList;
    }

    /**
     * @param storyList the storyList to set
     */
    public void setStoryList(List<Story> storyList) {
        this.storyList = storyList;
    }

    /**
     * @return the sprintManager
     */
    public SprintManager getSprintManager() {
        return sprintManager;
    }

    /**
     * @param sprintManager the sprintManager to set
     */
    public void setSprintManager(SprintManager sprintManager) {
        this.sprintManager = sprintManager;
    }

    public String create() {
        conversation.begin();
                
        Story story = new Story();
        story.setSprint(sprintManager.getCurrentSprint());
        setCurrentStory(story);
        
        return "create";
    }

    public String save() {
        if (currentStory != null) {
            Story merged = storyRepository.save(currentStory);

            sprintManager.getCurrentSprint().addStory(merged);
        }
        
        conversation.end();

        return "show";
    }

    public String edit() {
        conversation.begin();

        setCurrentStory(stories.getRowData());
        
        return "edit";
    }

    public String remove() {
        Story story = stories.getRowData();
        if (story != null) {
            storyRepository.remove(story);

            sprintManager.getCurrentSprint().removeStory(story);
        }

        return "show";
    }

    public void checkUniqueStoryName(FacesContext context, UIComponent component, Object newValue) {
        final String newName = (String) newValue;

        long count = storyRepository.countOtherStoriesWithName(sprintManager.getCurrentSprint(), currentStory, newName);
        if (count > 0) {
            throw new ValidatorException(getFacesMessageForKey("story.form.label.name.unique"));
        }
    }

    public String cancelEdit() {
        conversation.end();        
        
        return "show";
    }

    public String showTasks() {
        setCurrentStory(stories.getRowData());
        return "showTasks";
    }
}
