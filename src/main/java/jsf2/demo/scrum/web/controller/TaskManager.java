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
import jsf2.demo.scrum.domain.story.Story;
import jsf2.demo.scrum.domain.task.Task;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.validator.ValidatorException;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import jsf2.demo.scrum.domain.task.TaskRepository;

/**
 * @author Dr. Spock (spock at dev.java.net)
 */
@Named
@SessionScoped
public class TaskManager extends AbstractManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private Task currentTask;
    private List<Task> tasks;

    @Inject
    private TaskRepository taskRepository;
    
    @Inject
    private StoryManager storyManager;

    @PostConstruct
    public void construct() {
        getLogger(getClass()).log(Level.INFO, "new intance of taskManager in conversation");
        init();
    }

    @PreDestroy
    public void destroy() {
        getLogger(getClass()).log(Level.INFO, "destroy intance of taskManager in conversation");
    }
       
    public void init() {
        Task task = new Task();
        Story currentStory = storyManager.getCurrentStory();
        task.setStory(currentStory);
        setCurrentTask(task);
        
        if (currentStory != null) {
            tasks = currentStory.getTasks();
        } else {
            tasks = Collections.emptyList();
        }
    }
    
    public Task getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(Task currentTask) {
        this.currentTask = currentTask;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public Story getStory() {
        return storyManager.getCurrentStory();
    }

    public String create() {
        Task task = new Task();
        task.setStory(storyManager.getCurrentStory());
        setCurrentTask(task);
        
        return "create";
    }

    public String edit(Task task) {
        setCurrentTask(task);
        return "edit";
    }
        
    public String save() {
        if (currentTask != null) {
            Task merged = taskRepository.save(currentTask);

            storyManager.getCurrentStory().addTask(merged);
        }
        
        init(); // TODO. better to user update event.
        
        return "show";
    }


    public String remove(Task task) {
        if (task != null) {
            taskRepository.remove(task);

            storyManager.getCurrentStory().removeTask(task);
        }

        init(); // TODO. better to user update event.
        
        return "show";
    }

    public void checkUniqueTaskName(FacesContext context, UIComponent component, Object newValue) {
        final String newName = (String) newValue;

        long count = taskRepository.countOtherTasksWithName(storyManager.getCurrentStory(), currentTask, newName);
        if (count > 0) {
            throw new ValidatorException(getFacesMessageForKey("task.form.label.name.unique"));
        }
    }

    public String cancelEdit() {
        return "show";
    }

    public String showStories() {
        return "/story/show";
    }
 
}