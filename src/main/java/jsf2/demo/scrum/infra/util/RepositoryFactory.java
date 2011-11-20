/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.demo.scrum.infra.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jsf2.demo.scrum.infra.repository.Repository;
import jsf2.demo.scrum.infra.repository.Repository;

/**
 * Do manual Lookup Repository.
 * 
 * @author tomo
 */
public class RepositoryFactory {
    
    public static <T extends Repository> T getRepository(Class<T> clazz) {
                Context ctx;
        try {
            ctx = new InitialContext();
            return (T) ctx.lookup("java:module/" + clazz.getSimpleName());
        } catch (NamingException ex) {
            Logger.getLogger(RepositoryFactory.class.getName()).log(Level.SEVERE, null, ex);
            
            throw new RuntimeException(ex);
        }
    }
}
