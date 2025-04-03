package tasks.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tasks.model.ArrayTaskList;
import tasks.model.Task;
import tasks.repository.TaskIO;
import tasks.utils.TasksOperations;

import java.time.LocalDate;
import java.util.Date;

public class TasksService {

    private final ArrayTaskList tasks;
    private final DateService dateService;

    public TasksService(ArrayTaskList tasks, DateService dateService) {
        this.tasks = tasks;
        this.dateService = dateService;
    }

    public ObservableList<Task> getObservableList() {
        return FXCollections.observableArrayList(tasks.getAll());
    }

    public String getIntervalInHours(Task task) {
        int seconds = task.getRepeatInterval();
        int minutes = seconds / DateService.SECONDS_IN_MINUTE;
        int hours = minutes / DateService.MINUTES_IN_HOUR;
        minutes = minutes % DateService.MINUTES_IN_HOUR;
        return dateService.formTimeUnit(hours) + ":" + dateService.formTimeUnit(minutes);//hh:MM
    }

    public Iterable<Task> filterTasks(Date start, Date end) {
        TasksOperations tasksOps = new TasksOperations(getObservableList());
        Iterable<Task> filtered = tasksOps.incoming(start, end);
//        Iterable<Task> filtered = tasks.incoming(start, end);

        return filtered;
    }

    public Task buildTask(String title, LocalDate startDate, String startTime, LocalDate endDate, String endTime, boolean isRepeated, String intervalText, boolean isActive) {
        Task task;

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be an empty string");
        }

        if (title.length() > 255) {
            throw new IllegalArgumentException("Title is too long");
        }

        Date startDateTime = dateService.getDateMergedWithTime(startTime, dateService.getDateValueFromLocalDate(startDate));
        if (isRepeated) {
            if (endDate == null || endTime == null || endTime.trim().isEmpty()) {
                throw new IllegalArgumentException("End date and time are required for repeated tasks");
            }

            Date endDateTime = dateService.getDateMergedWithTime(endTime, dateService.getDateValueFromLocalDate(endDate));
            if (startDateTime.after(endDateTime)) {
                throw new IllegalArgumentException("Start time must be before end time");
            }
            int intervalSeconds = dateService.parseFromStringToSeconds(intervalText);
            task = new Task(title, startDateTime, endDateTime, intervalSeconds);
        } else {
            task = new Task(title, startDateTime);
        }

        task.setActive(isActive);
        return task;
    }

    public void addTask(Task task, ObservableList<Task> taskList) {
        taskList.add(task);
        TaskIO.rewriteFile(taskList);
    }

    public void editTask(Task existingTask, Task updatedTask, ObservableList<Task> taskList) {
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).equals(existingTask)) {
                taskList.set(i, updatedTask);
                break;
            }
        }
        TaskIO.rewriteFile(taskList);
    }
}
