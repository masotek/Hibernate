package com.infoshareacademy.web;

import com.infoshareacademy.dao.AddressDao;
import com.infoshareacademy.dao.ComputerDao;
import com.infoshareacademy.dao.CourseDao;
import com.infoshareacademy.dao.StudentDao;
import com.infoshareacademy.model.Address;
import com.infoshareacademy.model.Computer;
import com.infoshareacademy.model.Course;
import com.infoshareacademy.model.Student;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(urlPatterns = "/student")
public class StudentServlet extends HttpServlet {

    private Logger LOG = LoggerFactory.getLogger(StudentServlet.class);

    @Inject
    private StudentDao studentDao;

    @Inject
    private ComputerDao computerDao;

    @Inject
    private AddressDao addressDao;

    @Inject
    private CourseDao courseDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // Test data

        // Courses
        Course course1 = new Course("JJDD3");
        courseDao.save(course1);

        Course course2 = new Course("JJFD2");
        courseDao.save(course2);

        // Addresses
        Address a1 = new Address("Via ti rollo", "Carolina");
        addressDao.save(a1);

        // Computers
        Computer c1 = new Computer("HP8460P", "Windows XP");
        computerDao.save(c1);

        Computer c2 = new Computer("Dell Inspiron 1234", "Ubuntu Linux");
        computerDao.save(c2);

        // Students
        Set<Course> student1Courses = new HashSet<>();
        student1Courses.add(course1);
        student1Courses.add(course2);

        Student s1 = new Student("Michal", "Nowak", LocalDate.of(2000, 2, 14), c1, a1, student1Courses);
        studentDao.save(s1);

        Set<Course> student2Courses = new HashSet<>();
        student2Courses.add(course2);
        Student s2 = new Student("Marek", "Kowalski", LocalDate.of(1989, 12, 24), c2, a1, student2Courses);
        studentDao.save(s2);

        LOG.info("System time zone is: {}", ZoneId.systemDefault());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {

        final String action = req.getParameter("action");
        LOG.info("Requested action: {}", action);
        if (action == null || action.isEmpty()) {
            resp.getWriter().write("Empty action parameter.");
            return;
        }

        if (action.equals("findAll")) {
            findAll(req, resp);
        } else if (action.equals("add")) {
            addStudent(req, resp);
        } else if (action.equals("delete")) {
            deleteStudent(req, resp);
        } else if (action.equals("update")) {
            updateStudent(req, resp);
        } else if (action.equals("addToCourse")) {
            addStudentToCourse(req, resp);
        } else {
            resp.getWriter().write("Unknown action.");
        }
    }

    private void addStudentToCourse(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
        final Long id = Long.parseLong(req.getParameter("id"));
        LOG.info("Updating Student with id = {}", id);

        final Student existingStudent = studentDao.findById(id);
        if (existingStudent == null) {
            LOG.info("No Student found for id = {}, nothing to be updated", id);
        } else {
            final Long courseId = Long.parseLong(req.getParameter("courseId"));
            final Course course = courseDao.findById(courseId);

            Set<Course> courses = new HashSet<>();
            courses.add(course);
            courses.addAll(existingStudent.getCourses());

            existingStudent.setCourses(courses);

            studentDao.update(existingStudent);
            LOG.info("Student object updated: {}", existingStudent);
        }

        // Return all persisted objects
        findAll(req, resp);
    }

    private void updateStudent(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
        final Long id = Long.parseLong(req.getParameter("id"));
        LOG.info("Updating Student with id = {}", id);

        final Student existingStudent = studentDao.findById(id);
        if (existingStudent == null) {
            LOG.info("No Student found for id = {}, nothing to be updated", id);
        } else {
            existingStudent.setName(req.getParameter("name"));
            existingStudent.setSurname(req.getParameter("surname"));
            existingStudent.setDateOfBirth(LocalDate.parse(req.getParameter("dateOfBirth")));

            final Long computerId = Long.parseLong(req.getParameter("computerId"));
            final Computer computer = computerDao.findById(computerId);
            LOG.info("Found Computer with id {}: {}", computerId, computer);
            existingStudent.setComputer(computer);

            final Long addressId = Long.parseLong(req.getParameter("addressId"));
            final Address address = addressDao.findById(addressId);
            existingStudent.setAddress(address);

            studentDao.update(existingStudent);
            LOG.info("Student object updated: {}", existingStudent);
        }

        // Return all persisted objects
        findAll(req, resp);
    }

    private void addStudent(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {

        final Student p = new Student();
        p.setName(req.getParameter("name"));
        p.setSurname(req.getParameter("surname"));
        p.setDateOfBirth(LocalDate.parse(req.getParameter("dateOfBirth")));

        final Long computerId = Long.parseLong(req.getParameter("computerId"));
        final Computer computer = computerDao.findById(computerId);
        LOG.info("Found Computer with id {}: {}", computerId, computer);
        p.setComputer(computer);

        final Long addressId = Long.parseLong(req.getParameter("addressId"));
        final Address address = addressDao.findById(addressId);
        p.setAddress(address);

        studentDao.save(p);
        LOG.info("Saved a new Student object: {}", p);

        // Return all persisted objects
        findAll(req, resp);
    }

    private void deleteStudent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final Long id = Long.parseLong(req.getParameter("id"));
        LOG.info("Removing Student with id = {}", id);

        studentDao.delete(id);

        // Return all persisted objects
        findAll(req, resp);
    }

    private void findAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        List<Student> result;

        String dateString = req.getParameter("date");
        if (dateString != null) {
            result = studentDao.findAllBornAfter(LocalDate.parse(dateString));
        } else {
            result = studentDao.findAll();
        }

        LOG.info("Found {} objects", result.size());
        for (Student p : result) {
            resp.getWriter().write(p.toString() + "\n");
        }
    }
}

