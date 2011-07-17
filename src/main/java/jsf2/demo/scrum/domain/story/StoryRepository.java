/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.demo.scrum.domain.story;

import javax.ejb.Stateless;
import javax.persistence.Query;
import jsf2.demo.scrum.domain.sprint.Sprint;
import jsf2.demo.scrum.infra.repository.JpaRepository;

/**
 *
 * @author Ryo
 */
@Stateless
public class StoryRepository extends JpaRepository<Long, Story> {

    public StoryRepository() {
        super(Story.class);
    }
            
    public long countOtherStoriesWithName(Sprint sprint, Story story, String newName) {

        Query query = em.createNamedQuery((story.isNew()) ? "story.new.countByNameAndSprint" : "story.countByNameAndSprint");
        query.setParameter("name", newName);
        query.setParameter("sprint", sprint);
        if (!story.isNew()) {
            query.setParameter("currentStory", story);
        }

       return (Long) query.getSingleResult();
    }
}
