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
package jsf2.demo.scrum.application.scrum_management.impl;


import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import jsf2.demo.scrum.application.scrum_management.ScrumManager;
import jsf2.demo.scrum.domain.project.Project;
import jsf2.demo.scrum.domain.project.ProjectRepository;
import jsf2.demo.scrum.domain.sprint.Sprint;
import jsf2.demo.scrum.domain.sprint.SprintRepository;
import jsf2.demo.scrum.domain.story.Story;
import jsf2.demo.scrum.domain.story.StoryRepository;
import jsf2.demo.scrum.domain.task.Task;
import jsf2.demo.scrum.domain.task.TaskRepository;
import jsf2.demo.scrum.infra.entity.Current;
import static jsf2.demo.scrum.infra.entity.EntityStatusAssertions.*;

/**
 * Because of the limitation of extended persistence context progagation,
 * all states related to a conversation must be consolidated to a single bean.
 * 
 * @author Ryo Asai.
 */
@Named("scrumManager")
@Stateful
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@ConversationScoped
public class ScrumManagerImpl implements ScrumManager, Serializable {

    private static final long serialVersionUID = 1L;

    private Project currentProject;
    private Sprint currentSprint;
    private Story currentStory;
    private Task currentTask;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    SprintRepository sprintRepository;

    @Inject
    StoryRepository storyRepository;

    @Inject
    TaskRepository taskRepository;
    
    @Inject
    @SuppressWarnings("NonConstantLogger")
    transient Logger logger;
    
    @PersistenceContext(type= PersistenceContextType.EXTENDED)
    protected EntityManager em;
    
    //=========================================================================
    // Bean lifecycle callbacks
    //=========================================================================
    
    @PostConstruct
    public void construct() {
        logger.log(Level.INFO, "new intance of {0} in conversation", getClass().getName());
    }

    @PreDestroy
    public void destroy() {
        logger.log(Level.INFO, "destroy intance of {0} in conversation", getClass().getName());
    }
   
    //=========================================================================
    // Project management
    //=========================================================================
        
    @Produces @Current @Named    
    @Override
    public Project getCurrentProject() {
        return currentProject;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void setCurrentProject(Project currentProject) {
        this.currentProject = projectRepository.toManaged(currentProject);
        
        currentSprint = null;
        currentStory = null;
        currentTask = null;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void saveCurrentProject() {
        assertThatEntityIsNotNull(currentProject);
        if (!currentProject.isNew()) return;
        
        projectRepository.persist(currentProject);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void removeProject(Project project) {
        projectRepository.remove(projectRepository.findById(project.getId()));
    }

    //=========================================================================
    // Sprint management
    //=========================================================================
    
    @Produces @Current @Named    
    @Override
    public Sprint getCurrentSprint() {
        return currentSprint;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void setCurrentSprint(Sprint currentSprint) {
        this.currentSprint = sprintRepository.toManaged(currentSprint);
        
        currentStory = null;
        currentTask = null;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)    
    @Override
    public void saveCurrentSprint() {
        assertThatEntityIsNotNull(currentProject);
        assertThatEntityIsNotNull(currentSprint);
        if (!currentSprint.isNew()) return;
        
        currentProject.addSprint(currentSprint);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void removeSprint(Sprint sprint) {
        assertThatEntityIsNotNull(currentProject);      

        currentProject.removeSprint(sprint);
    }

    //=========================================================================
    // Story management
    //=========================================================================
    
    @Produces @Current @Named    
    @Override
    public Story getCurrentStory() {
        return currentStory;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void setCurrentStory(Story currentStory) {
        this.currentStory = storyRepository.toManaged(currentStory);

        currentTask = null;
    }    

    @TransactionAttribute(TransactionAttributeType.REQUIRED)    
    @Override
    public void saveCurrentStory() {
        assertThatEntityIsNotNull(currentSprint);
        assertThatEntityIsNotNull(currentStory);
       if (!currentStory.isNew()) return;
       
        currentSprint.addStory(currentStory);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void removeStory(Story story) {
        assertThatEntityIsNotNull(currentSprint);

        currentSprint.removeStory(story);
    }

    //=========================================================================
    // Task Management
    //=========================================================================
    
    @Produces @Current @Named    
    @Override
    public Task getCurrentTask() {
        return currentTask;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)    
    @Override
    public void saveCurrentTask() {
        assertThatEntityIsNotNull(currentStory);
        assertThatEntityIsNotNull(currentTask);
       if (!currentTask.isNew()) return;
       
        currentStory.addTask(currentTask);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void removeTask(Task task) {
        assertThatEntityIsNotNull(currentStory);

        currentStory.removeTask(task);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void setCurrentTask(Task currentTask) {
        this.currentTask = taskRepository.toManaged(currentTask);
    }    
}