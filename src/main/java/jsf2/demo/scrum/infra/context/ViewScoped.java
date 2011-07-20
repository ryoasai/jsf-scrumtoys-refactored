/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.demo.scrum.infra.context;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.enterprise.context.NormalScope;

/**
 *
 * @author Ryo
 */
@Inherited
@NormalScope
@Retention(RUNTIME)
@Target({METHOD, FIELD, TYPE})
public @interface ViewScoped {
}
