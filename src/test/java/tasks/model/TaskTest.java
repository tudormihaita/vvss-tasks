package tasks.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private Task task;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @BeforeEach
    void setUp() {
        try {
            task=new Task("new task", sdf.parse("2023-02-12 10:10"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testTaskCreation() throws ParseException {
       assert task.getTitle() == "new task";
        System.out.println(task.getFormattedDateStart());
        System.out.println(task.getDateFormat().format(sdf.parse("2023-02-12 10:10")));
       assert task.getFormattedDateStart().equals(task.getDateFormat().format(sdf.parse("2023-02-12 10:10")));
    }

    @AfterEach
    void tearDown() {
    }
}