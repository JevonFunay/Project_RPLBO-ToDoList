package org.example.project_rplbo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class TodoListController {

    @FXML
    private TextField taskInput;

    @FXML
    private ComboBox<String> priorityComboBox;

    @FXML
    private ListView<Task> taskListView;

    private ObservableList<Task> tasks;

    @FXML
    public void initialize() {
        tasks = FXCollections.observableArrayList();
        taskListView.setItems(tasks);

        priorityComboBox.setItems(FXCollections.observableArrayList("Rendah", "Sedang", "Tinggi"));
        priorityComboBox.getSelectionModel().selectFirst();

        // Custom cell factory untuk menampilkan tugas dengan prioritas
        taskListView.setCellFactory(lv -> new ListCell<Task>() {
            @Override
            protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDescription() + " (Prioritas: " + item.getPriority() + ")");
                }
            }
        });

        // Mengubah prioritas tugas saat dipilih di list
        taskListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // double click untuk edit prioritas
                Task selected = taskListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    ChoiceDialog<String> dialog = new ChoiceDialog<>(selected.getPriority(), "Rendah", "Sedang", "Tinggi");
                    dialog.setTitle("Ubah Prioritas");
                    dialog.setHeaderText("Ubah prioritas tugas");
                    dialog.setContentText("Pilih prioritas baru:");

                    dialog.showAndWait().ifPresent(newPriority -> {
                        selected.setPriority(newPriority);
                        taskListView.refresh();
                    });
                }
            }
        });
    }

    @FXML
    private void handleAddTask() {
        String desc = taskInput.getText().trim();
        String priority = priorityComboBox.getValue();
        if (!desc.isEmpty()) {
            tasks.add(new Task(desc, priority));
            taskInput.clear();
            priorityComboBox.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleRemoveTask() {
        Task selected = taskListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            tasks.remove(selected);
        }
    }

    // Kelas inner untuk menyimpan tugas dan prioritas
    public static class Task {
        private String description;
        private String priority;

        public Task(String description, String priority) {
            this.description = description;
            this.priority = priority;
        }

        public String getDescription() {
            return description;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }
    }
}
