package tasks.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.*;
import tasks.utils.TasksOperations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

class TaskOperationsTest {
    private static TasksOperations tasksOps;
    private static SimpleDateFormat sdf;

    @BeforeAll
    static void setUp() {
        ArrayTaskList taskList = new ArrayTaskList();
        ObservableList<Task> observableList = FXCollections.observableArrayList(taskList.getAll());
        tasksOps = new TasksOperations(observableList);
        sdf = Task.getDateFormat();
    }

    @AfterEach
    void tearDown() {
        tasksOps.getTasks().clear();
    }

    @Test
    @DisplayName("FC02_TC01")
    void testFC02_TC01() throws ParseException {
        Task t1 = new Task(
                "mock title",
                sdf.parse("2025-04-04 10:00"),
                sdf.parse("2025-04-05 10:00"),
                30
        );
        tasksOps.getTasks().add(t1);
        Date start = sdf.parse("2025-05-05 10:00");
        Date end = sdf.parse("2025-05-04 10:00");
        Iterable<Task> tasks = tasksOps.incoming(start, end);
        List<Task> filtered = StreamSupport.stream(tasks.spliterator(),false).collect(Collectors.toList());;
        assertEquals(0, filtered.size());
    }

    @Test
    @DisplayName("FC02_TC02")
    void testFC02_TC02() throws ParseException {
        Date start = sdf.parse("2025-05-05 10:00");
        Date end = sdf.parse("2025-05-06 10:00");
        Iterable<Task> tasks = tasksOps.incoming(start, end);
        List<Task> filtered = StreamSupport.stream(tasks.spliterator(),false).collect(Collectors.toList());
        assertEquals(0, filtered.size());
    }

    @Test
    @DisplayName("FC02_TC03")
    void testFC02_TC03() throws ParseException {
        Task t1 = new Task(
                "mock title",
                sdf.parse("2025-05-05 10:00"),
                sdf.parse("2025-05-06 10:00"),
                30
        );
        t1.setActive(true);
        tasksOps.getTasks().add(t1);
        Date start = sdf.parse("2025-05-03 10:00");
        Date end = sdf.parse("2025-05-04 10:00");
        Iterable<Task> tasks = tasksOps.incoming(start, end);
        List<Task> filtered = StreamSupport.stream(tasks.spliterator(),false).collect(Collectors.toList());
        assertEquals(0, filtered.size());
    }

    @Test
    @DisplayName("FC02_TC04")
    void testFC02_TC04() throws ParseException {
        Task t1 = new Task(
                "mock title",
                sdf.parse("2025-05-05 11:00"),
                sdf.parse("2025-05-06 11:00"),
                30
        );
        t1.setActive(true);
        tasksOps.getTasks().add(t1);
        Date start = sdf.parse("2025-05-02 11:00");
        Date end = sdf.parse("2025-05-03 11:00");
        Iterable<Task> tasks = tasksOps.incoming(start, end);
        List<Task> filtered = StreamSupport.stream(tasks.spliterator(),false).collect(Collectors.toList());
        assertEquals(0, filtered.size());
    }

    @Test
    @DisplayName("FC02_TC05")
    void testFC02_TC05() throws ParseException {
        Task t1 = new Task(
                "mock title",
                sdf.parse("2025-05-05 12:00"),
                sdf.parse("2025-05-06 12:00"),
                30
        );
        t1.setActive(true);
        tasksOps.getTasks().add(t1);
        Date start = sdf.parse("2025-05-04 12:00");
        Date end = sdf.parse("2025-05-05 12:00");
        Iterable<Task> tasks = tasksOps.incoming(start, end);
        List<Task> filtered = StreamSupport.stream(tasks.spliterator(),false).collect(Collectors.toList());
        assertEquals(1, filtered.size());
        assertEquals(filtered.get(0), t1);
    }

    @Test
    @DisplayName("FC02_TC06")
    void testFC02_TC06() throws ParseException {
        Task t1 = new Task(
                "mock title",
                sdf.parse("2025-05-05 12:00"),
                sdf.parse("2025-05-06 12:00"),
                30
        );
        t1.setActive(true);
        Task t2 = new Task(
                "mock title",
                sdf.parse("2025-05-05 12:00"),
                sdf.parse("2025-05-05 12:00"),
                30
        );
        t2.setActive(true);
        tasksOps.getTasks().add(t1);
        tasksOps.getTasks().add(t2);
        Date start = sdf.parse("2025-05-03 12:00");
        Date end = sdf.parse("2025-05-07 12:00");
        Iterable<Task> tasks = tasksOps.incoming(start, end);
        List<Task> filtered = StreamSupport.stream(tasks.spliterator(),false).collect(Collectors.toList());
        assertEquals(2, filtered.size());
        assertEquals(filtered.get(0), t1);
        assertEquals(filtered.get(1), t2);
    }
}