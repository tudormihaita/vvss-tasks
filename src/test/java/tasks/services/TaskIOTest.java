package tasks.services;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasks.model.ArrayTaskList;
import tasks.model.Task;
import tasks.repository.TaskIO;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TaskIOTest {
    private ObservableList<Task> tasks;
    private DateService dateService;
    @BeforeEach
    void setUp() {
        ArrayTaskList taskList = new ArrayTaskList();
        dateService = new DateService();
        TaskService service = new TaskService(taskList,dateService);
        tasks = service.getObservableList();
    }

    @AfterEach
    void tearDown() {
        tasks.clear();
    }

    @Test
    @DisplayName("Add Valid Task ECP")
    void testECP_1() {
        assertEquals(tasks.size(), 0);
        Task taskDTO = new Task("Doing things", new Date(2024, 4, 5, 10, 0));
        TaskIO.insertTask(taskDTO, tasks);
        assertEquals(tasks.size(), 1);
    }

    @Test
    @DisplayName("Add Invalid Task ECP")
    void testECP_2() {
        assertEquals(tasks.size(), 0);
        Task taskDTO = new Task("Doing things", new Date(2024, 4, 5, 10, 0), new Date(2024, 4, 5, 12, 0), 30);
        try {
            TaskIO.insertTask(taskDTO, tasks);
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "interval should be > 0");
        }
        assertEquals(tasks.size(), 0);
    }

    @Test
    @DisplayName("Add Valid Task BVA")
    void testBVA_1() {
        assertEquals(tasks.size(), 0);
        Task taskDTO = new Task("Doing things", new Date(0), new Date(1), 3);
        TaskIO.insertTask(taskDTO, tasks);
        assertEquals(tasks.size(), 1);
    }

    @Test
    @DisplayName("Add Invalid Task BVA")
    void testBVA_2() {
        assertEquals(tasks.size(), 0);
        Task taskDTO = new Task("Doing things", new Date(-1), new Date(1), 3);
        try {
            TaskIO.insertTask(taskDTO, tasks);
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Time cannot be negative");
        }
        assertEquals(tasks.size(), 0);
    }
}