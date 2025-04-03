package tasks.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tasks.model.ArrayTaskList;
import tasks.model.Task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class TasksServiceTest {
    private final TasksService tasksService = new TasksService(new ArrayTaskList(), new DateService());
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm");

    // Test 1: Valid Task with title "Meeting"
    @ParameterizedTest
    @CsvSource({
            "'Meeting', '2025-04-03, 16:00', null, false, null, true"
    })
    @DisplayName("Test valid task creation (Meeting)")
    void testValidTaskCreationECP(String title, String startDateTime, String endDateTime,
                               boolean isRepeated, String interval, boolean isActive) {
        // Arrange
        LocalDate startDate = LocalDate.parse(startDateTime.split(",")[0]);
        String startTime = startDateTime.split(",")[1].trim();

        // Act
        Task task = tasksService.buildTask(title, startDate, startTime, null, null, isRepeated, interval, isActive);

        // Assert
        assertNotNull(task);
        assertEquals(title, task.getTitle());
    }

    // Test 2: Task where endDateTime equals startDateTime
    @ParameterizedTest
    @CsvSource({
            "'Shopping', '2025-04-05, 00:00', null, false, 0, true"
    })
    @DisplayName("Test task with equal start and end date (Shopping)")
    void testTaskWithEqualStartAndEndDateECP(String title, String startDateTime, String endDateTime,
                                          boolean isRepeated, String interval, boolean isActive) {
        // Arrange
        LocalDate startDate = LocalDate.parse(startDateTime.split(",")[0]);
        String startTime = startDateTime.split(",")[1].trim();

        // Act
        Task task = tasksService.buildTask(title, startDate, startTime, null, null, isRepeated, interval, isActive);

        // Assert
        assertNotNull(task);
        assertEquals(title, task.getTitle());
    }

    // Test 3: Task with empty title
    @ParameterizedTest
    @CsvSource({
            "'', '2025-04-03, 16:00', '2025-04-04, 16:00', true, 3600, true"
    })
    @DisplayName("Test empty title throws exception")
    void testEmptyTitleThrowsExceptionECP(String title, String startDateTime, String endDateTime,
                                       boolean isRepeated, String interval, boolean isActive) {
        // Arrange
        LocalDate startDate = LocalDate.parse(startDateTime.split(",")[0]);
        String startTime = startDateTime.split(",")[1].trim();

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tasksService.buildTask(title, startDate, startTime, null, null, isRepeated, interval, isActive);
        });

        assertNotNull(exception);
        assertEquals("Title cannot be an empty string", exception.getMessage());
    }

    // Test 4: Task with invalid endDateTime (should throw exception)
    @ParameterizedTest
    @CsvSource({
            "'Task', '2025-04-01, 10:30', '2025-04-01, 09:00', true, 1800, false"
    })
    @DisplayName("Test endDateTime before startDateTime throws exception")
    void testEndDateBeforeStartDateThrowsExceptionECP(String title, String startDateTime, String endDateTime,
                                                   boolean isRepeated, String interval, boolean isActive) {
        // Arrange
        LocalDate startDate = LocalDate.parse(startDateTime.split(",")[0]);
        String startTime = startDateTime.split(",")[1].trim();

        LocalDate endDate = LocalDate.parse(endDateTime.split(",")[0]);
        String endTime = endDateTime.split(",")[1].trim();

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tasksService.buildTask(title, startDate, startTime, endDate, endTime, isRepeated, interval, isActive);
        });

        assertNotNull(exception);
    }

    // Test 5: Valid Task with title "A"
    @ParameterizedTest
    @CsvSource({
            "'A', '2025-04-03, 10:00', '2025-04-03, 19:00', true, 18:00, true"
    })
    @DisplayName("Test valid task creation with title 'A'")
    void testValidTaskCreationWithABVA(String title, String startDateTime, String endDateTime,
                                    boolean isRepeated, String interval, boolean isActive) {
        // Arrange
        LocalDate startDate = LocalDate.parse(startDateTime.split(",")[0]);
        String startTime = startDateTime.split(",")[1].trim();
        LocalDate endDate = LocalDate.parse(endDateTime.split(",")[0]);
        String endTime = endDateTime.split(",")[1].trim();
        // Act
        Task task = tasksService.buildTask(title, startDate, startTime, endDate, endTime, isRepeated, interval, isActive);

        // Assert
        assertNotNull(task);
        assertEquals(title, task.getTitle());
        assertTrue(task.isActive());
    }

    // Test 6: Title length exceeds limit (exception thrown)
    @ParameterizedTest
    @CsvSource({
            "'A'.repeat(256), '2025-04-03, 10:00', null, false, 0, false"
    })
    @DisplayName("Test title length exceeds limit throws exception")
    void testTitleLengthExceedsLimitBVA(String title, String startDateTime, String endDateTime,
                                     boolean isRepeated, String interval, boolean isActive) {
        // Arrange
        LocalDate startDate = LocalDate.parse(startDateTime.split(",")[0]);
        String startTime = startDateTime.split(",")[1].trim();
        String repetitive_title = "A".repeat(256);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tasksService.buildTask(repetitive_title, startDate, startTime, null, null, isRepeated, interval, isActive);
        });

        assertNotNull(exception);
        assertEquals("Title is too long", exception.getMessage());
    }

    // Test 7: Repeated task entry created (valid scenario)
    @ParameterizedTest
    @CsvSource({
            "'Task', '2025-04-03, 10:00', '2025-04-03, 10:01', true, 60:00, true"
    })
    @DisplayName("Test repeated task entry created")
    void testRepeatedTaskCreationBVA(String title, String startDateTime, String endDateTime,
                                  boolean isRepeated, String interval, boolean isActive) {
        // Arrange
        LocalDate startDate = LocalDate.parse(startDateTime.split(",")[0]);
        String startTime = startDateTime.split(",")[1].trim();
        LocalDate endDate = LocalDate.parse(endDateTime.split(",")[0]);
        String endTime = endDateTime.split(",")[1].trim();

        // Act
        Task task = tasksService.buildTask(title, startDate, startTime, endDate, endTime, isRepeated, interval, isActive);

        // Assert
        assertNotNull(task);
        assertTrue(task.isRepeated());
    }

    // Test 8: End date before start date (exception thrown)
    @ParameterizedTest
    @CsvSource({
            "'Task', '2025-04-03, 10:00', '2025-04-03, 09:59', true, 60, false"
    })
    @DisplayName("Test endDate before startDate throws exception")
    void testEndDateBeforeStartDateThrowsExceptionWithABVA(String title, String startDateTime, String endDateTime,
                                                        boolean isRepeated, String interval, boolean isActive) {
        // Arrange
        LocalDate startDate = LocalDate.parse(startDateTime.split(",")[0]);
        String startTime = startDateTime.split(",")[1].trim();

        LocalDate endDate = LocalDate.parse(endDateTime.split(",")[0]);
        String endTime = endDateTime.split(",")[1].trim();

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tasksService.buildTask(title, startDate, startTime, endDate, endTime, isRepeated, interval, isActive);
        });

        assertNotNull(exception);
        assertEquals("Start time must be before end time", exception.getMessage());
    }
}
