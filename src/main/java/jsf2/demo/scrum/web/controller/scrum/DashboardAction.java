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

import jsf2.demo.scrum.infra.web.controller.AbstractAction;
import jsf2.demo.scrum.domain.story.Story;
import jsf2.demo.scrum.domain.task.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import jsf2.demo.scrum.application.scrum_management.ScrumManager;
import jsf2.demo.scrum.infra.context.ViewScoped;

@Model
public class DashboardAction extends AbstractAction implements Serializable {

    private static final long serialVersionUID = 1L;

    //=========================================================================
    // Fields.
    //=========================================================================    

    @Inject
    TaskAction taskAction;

    @Inject
    StoryAction storyAction;
    
    @Inject
    ScrumManager scrumManager;

    //=========================================================================
    // Properties.
    //=========================================================================
    
    @Produces @ViewScoped @Named
    public List<Task> getToDoTasks() {
        List<Task> toDoTasksList = new ArrayList<Task>();
        for (Story story : storyAction.getStories()) {
            toDoTasksList.addAll(story.getTodoTasks());
        }
       
        return toDoTasksList;
    }

    @Produces @ViewScoped @Named
    public List<Task> getWorkingTasks() {
        List<Task> workingTasksList = new ArrayList<Task>();
        for (Story story : storyAction.getStories()) {
            workingTasksList.addAll(story.getWorkingTasks());
        }
        
        return workingTasksList;
    }

    @Produces @ViewScoped @Named
    public List<Task> getDoneTasks() {
        List<Task> doneTasksList = new ArrayList<Task>();
        for (Story story : storyAction.getStories()) {
            doneTasksList.addAll(story.getDoneTasks());
        }
        
        return doneTasksList;
    }

    //=========================================================================
    // Actions.
    //=========================================================================    

    public String editTask(Task currentTask) {
        if (currentTask == null) {
            return "";
        }

        Story currentStory = scrumManager.getCurrentStory();
        if (currentStory != currentTask.getStory()) {
            storyAction.selectCurrentEntity(currentTask.getStory());
        }
        
        taskAction.selectCurrentEntity(currentTask);        
        
        return redirectTo("/task/edit");
    }
}
