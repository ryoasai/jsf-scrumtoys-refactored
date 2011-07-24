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
package jsf2.demo.scrum.infra.manager;


import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PreDestroy;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.Conversation;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import jsf2.demo.scrum.infra.entity.PersistentEntity;
import jsf2.demo.scrum.infra.repository.Repository;

/**
 * @author Ryo Asai.
 */
@Named
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public abstract class BaseCrudManager<K extends Serializable, E extends PersistentEntity<K>> extends AbstractManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private E currentEntity;

    private boolean conversationNested;
    
    @Inject
    protected Conversation conversation;

    @PersistenceContext(type= PersistenceContextType.EXTENDED)
    protected EntityManager em;
            
    @PostConstruct
    public void construct() {
        getLogger(getClass()).log(Level.INFO, "new intance of taskManager in conversation");
    }

    @PreDestroy
    public void destroy() {
        getLogger(getClass()).log(Level.INFO, "destroy intance of taskManager in conversation");
    }

    public boolean isConversationNested() {
        return conversationNested;
    }
        
    public void beginConversation() {
        if (conversation.isTransient()) {
            conversationNested = false;
            conversation.begin();
        } else {
            conversationNested = true;
        }
    }
    
    public void endConversation() {
        if (!isConversationNested() && !conversation.isTransient()) {
            conversation.end();
        }
        
        this.currentEntity = null;
    }
    
    public E getCurrentEntity() {
        return currentEntity;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setCurrentEntity(E currentEntity) {
        if (currentEntity == null) {
            this.currentEntity = currentEntity;
            endConversation();
            return;
        }
        
        beginConversation();
        
        if (currentEntity.isNew()) {
            this.currentEntity = currentEntity;
        } else {
            this.currentEntity = findById(currentEntity.getId());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String create() {
        E entity = doCreate();

        setCurrentEntity(entity);
        
        return "create?faces-redirect=true";
    }

    protected abstract Repository<K, E> getRepository();
    
    protected abstract E doCreate();
    
    public E findById(K id) {
        return getRepository().findById(id);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String edit(E entity) {
        // set managed entity
        setCurrentEntity(entity);
        
        return "edit?faces-redirect=true";
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String save() {                
        if (currentEntity != null && currentEntity.isNew()) {
            doPersist(currentEntity);
        }
        
        endConversation();
                
        return "show?faces-redirect=true";
    }
    
    protected void doPersist(E project) {
        getRepository().persist(project);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String remove(E entity) {
        if (entity != null) {
            doRemove(entity);
        }

        return "show?faces-redirect=true";
    }

    protected abstract void doRemove(E entity);
    
    public String cancelEdit() {
        endConversation();
        
        return "show?faces-redirect=true";
    }
 
}