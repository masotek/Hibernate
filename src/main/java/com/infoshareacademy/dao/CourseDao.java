package com.infoshareacademy.dao;

import com.infoshareacademy.dto.CourseSummary;
import com.infoshareacademy.model.Course;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class CourseDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Long save(Course c) {
        entityManager.persist(c);
        return c.getId();
    }

    public Course update(Course c) {
        return entityManager.merge(c);
    }

    public void delete(Long id) {
        final Course c = entityManager.find(Course.class, id);
        if (c != null) {
            entityManager.remove(c);
        }
    }

    public Course findById(Long id) {
        return entityManager.find(Course.class, id);
    }

    public List<Course> findAll() {
        final Query query = entityManager.createQuery("SELECT s FROM Course s");

        return query.getResultList();
    }

    public List<CourseSummary> getCoursesAttendees() {
        final Query query = entityManager.createNativeQuery("SELECT COUNT(t.student_id), c.name\n"
            + "  FROM COURSES c\n"
            + "  INNER JOIN STUDENTS_TO_COURSES t\n"
            + "      ON c.id=t.course_id\n"
            + "  GROUP BY c.name;");

        final List<Object[]> objects = query.getResultList();

        final List<CourseSummary> result = new ArrayList<>();
        for (Object[] row : objects) {
            BigInteger c = (BigInteger) row[0];
            result.add(new CourseSummary(c.intValue(), (String) row[1]));
        }
        return result;
    }
}
