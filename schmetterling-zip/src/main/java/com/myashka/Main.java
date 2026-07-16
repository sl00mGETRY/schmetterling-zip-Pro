package com.myashka;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.*;

public class Main extends Application {

    private static final byte[] FAKE_RAR_HEADER = "Rar!\u001a\u0007\u0000".getBytes();
    private static final byte[] MARKER = "MYASH!".getBytes();

    private File currentArchiveFile = null;
    private ArchiveModel archiveModel = new ArchiveModel();
    private String currentPath = "";

   
    private final ObservableList<File> filesToPack = FXCollections.observableArrayList();

   
    private TableView<ArchiveModel.Node> fileTable;
    private TreeView<String> folderTree;
    private Label statusLabel;
    private HBox breadcrumbsContainer;
    private TextField searchField;
    private ProgressBar progressBar;
    private Button btnCancel;
    private Task<Void> activeTask = null;

    private boolean isDarkTheme = true;
    private Scene scene;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Schmetterling-ZIP Ultimate");

        BorderPane root = new BorderPane();

       
        VBox topContainer = new VBox(5);
        topContainer.getStyleClass().add("tab-header-background");
        topContainer.setPadding(new Insets(10));

        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Button btnOpen = new Button("📂 Open");
        Button btnExtract = new Button("📤 Extract Selected");
        Button btnExtractAll = new Button("📦 Extract All");
        Button btnCreateMyash = new Button("🦋 Create .myash");
        Button btnCreateStd = new Button("🗜️ Create Standard");
        Button btnToggleTheme = new Button("🌓 Theme");

        searchField = new TextField();
        searchField.setPromptText("🔍 Search in current folder...");
        searchField.setPrefWidth(200);

        toolbar.getChildren().addAll(btnOpen, btnExtract, btnExtractAll, new Separator(), btnCreateMyash, btnCreateStd, btnToggleTheme, new Pane(), searchField);
        HBox.setHgrow(toolbar.getChildren().get(toolbar.getChildren().size() - 2), Priority.ALWAYS);

       
        breadcrumbsContainer = new HBox(5);
        breadcrumbsContainer.setAlignment(Pos.CENTER_LEFT);
        breadcrumbsContainer.setPadding(new Insets(5, 0, 0, 0));
        updateBreadcrumbs();

        topContainer.getChildren().addAll(toolbar, breadcrumbsContainer);

       
        folderTree = new TreeView<>();
        TreeItem<String> rootTreeItem = new TreeItem<>("Root");
        rootTreeItem.setExpanded(true);
        folderTree.setRoot(rootTreeItem);
        folderTree.setPrefWidth(200);

       
        folderTree.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                navigateToTreeFolder(newVal);
            }
        });

       
        fileTable = new TableView<>();
        fileTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableColumn<ArchiveModel.Node, String> colIcon = new TableColumn<>("");
        colIcon.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isDirectory() ? "📁" : "📄"));
        colIcon.setPrefWidth(40);

        TableColumn<ArchiveModel.Node, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colName.setPrefWidth(250);

        TableColumn<ArchiveModel.Node, String> colSize = new TableColumn<>("Size");
        colSize.setCellValueFactory(cellData -> {
            long bytes = cellData.getValue().getSize();
            if (cellData.getValue().isDirectory()) return new SimpleStringProperty("");
            return new SimpleStringProperty(formatSize(bytes));
        });
        colSize.setPrefWidth(120);

        TableColumn<ArchiveModel.Node, String> colSector = new TableColumn<>("Sector / Status");
        colSector.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSector()));
        colSector.setPrefWidth(130);

        fileTable.getColumns().addAll(colIcon, colName, colSize, colSector);

       
        fileTable.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                ArchiveModel.Node selected = fileTable.getSelectionModel().getSelectedItem();
                if (selected != null && selected.isDirectory()) {
                    enterDirectory(selected.getName());
                }
            }
        });

       
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuOpen = new MenuItem("Open / Enter");
        MenuItem menuExtract = new MenuItem("Extract Selected");
        MenuItem menuDelete = new MenuItem("Remove from pack list (Delete)");
        contextMenu.getItems().addAll(menuOpen, menuExtract, menuDelete);
        fileTable.setContextMenu(contextMenu);

        menuOpen.setOnAction(e -> {
            ArchiveModel.Node selected = fileTable.getSelectionModel().getSelectedItem();
            if (selected != null && selected.isDirectory()) {
                enterDirectory(selected.getName());
            }
        });
        menuExtract.setOnAction(e -> extractSelectedFlow(stage));
        menuDelete.setOnAction(e -> removeSelectedFromPackList());

       
        fileTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                ArchiveModel.Node selected = fileTable.getSelectionModel().getSelectedItem();
                if (selected != null && selected.isDirectory()) {
                    enterDirectory(selected.getName());
                }
            } else if (event.getCode() == KeyCode.BACK_SPACE) {
                navigateUp();
            } else if (event.getCode() == KeyCode.DELETE) {
                removeSelectedFromPackList();
            }
        });

       
        fileTable.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        fileTable.setOnDragDropped(event -> {
            var db = event.getDragboard();
            if (db.hasFiles()) {
                List<File> droppedFiles = db.getFiles();
                if (droppedFiles.size() == 1 && isArchiveFile(droppedFiles.get(0))) {
                    openSpecificArchive(droppedFiles.get(0));
                } else {
                    filesToPack.addAll(droppedFiles);
                    showStatus("Added " + droppedFiles.size() + " elements to pack queue. Press Create button to save.");
                    loadPackListToTable();
                }
            }
            event.setDropCompleted(true);
            event.consume();
        });

       
        HBox statusBar = new HBox(15);
        statusBar.setPadding(new Insets(10));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.getStyleClass().add("tab-header-background");

        statusLabel = new Label("Ready");
        HBox.setHgrow(statusLabel, Priority.ALWAYS);

        progressBar = new ProgressBar(0);
        progressBar.setVisible(false);
        progressBar.setPrefWidth(150);

        btnCancel = new Button("Cancel");
        btnCancel.setVisible(false);
        btnCancel.setOnAction(e -> {
            if (activeTask != null && activeTask.isRunning()) {
                activeTask.cancel();
                showStatus("Operation cancelled by user.");
            }
        });

        statusBar.getChildren().addAll(statusLabel, progressBar, btnCancel);

       
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(folderTree, fileTable);
        splitPane.setDividerPositions(0.25);

        root.setTop(topContainer);
        root.setCenter(splitPane);
        root.setBottom(statusBar);

       
        btnOpen.setOnAction(e -> openArchiveFlow(stage));
        btnExtract.setOnAction(e -> extractSelectedFlow(stage));
        btnExtractAll.setOnAction(e -> extractAllFlow(stage));
        btnCreateMyash.setOnAction(e -> createMyashFlow(stage));
        btnCreateStd.setOnAction(e -> createStandardFlow(stage));
        btnToggleTheme.setOnAction(e -> toggleTheme());

       
        searchField.textProperty().addListener((obs, oldText, newText) -> filterCurrentDirectory(newText));

        scene = new Scene(root, 950, 650);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

       
        List<String> args = getParameters().getRaw();
        if (!args.isEmpty()) {
            openSpecificArchive(new File(args.get(0)));
        }

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

   

    private void enterDirectory(String dirName) {
        if (currentPath.isEmpty()) {
            currentPath = dirName;
        } else {
            currentPath = currentPath + "/" + dirName;
        }
        updateBreadcrumbs();
        loadCurrentPathToTable();
    }

    private void navigateUp() {
        if (currentPath.isEmpty()) return;
        int lastSlash = currentPath.lastIndexOf('/');
        if (lastSlash == -1) {
            currentPath = "";
        } else {
            currentPath = currentPath.substring(0, lastSlash);
        }
        updateBreadcrumbs();
        loadCurrentPathToTable();
    }

    private void navigateToBreadcrumb(String targetPath) {
        currentPath = targetPath;
        updateBreadcrumbs();
        loadCurrentPathToTable();
    }

    private void navigateToTreeFolder(TreeItem<String> treeItem) {
        StringBuilder pathBuilder = new StringBuilder(treeItem.getValue());
        TreeItem<String> parent = treeItem.getParent();
        while (parent != null && !parent.getValue().equals("Root")) {
            pathBuilder.insert(0, parent.getValue() + "/");
            parent = parent.getParent();
        }
        String path = pathBuilder.toString();
        if (path.equals("Root")) {
            currentPath = "";
        } else {
            currentPath = path;
        }
        updateBreadcrumbs();
        loadCurrentPathToTable();
    }

    private void updateBreadcrumbs() {
        breadcrumbsContainer.getChildren().clear();

        Button btnRoot = new Button("Root 📦");
        btnRoot.getStyleClass().add("breadcrumb-button");
        btnRoot.setOnAction(e -> navigateToBreadcrumb(""));
        breadcrumbsContainer.getChildren().add(btnRoot);

        if (currentPath.isEmpty()) return;

        String[] parts = currentPath.split("/");
        StringBuilder accumulatedPath = new StringBuilder();

        for (String part : parts) {
            if (accumulatedPath.length() > 0) {
                accumulatedPath.append("/");
            }
            accumulatedPath.append(part);
            String target = accumulatedPath.toString();

            Label arrow = new Label(" > ");
            arrow.setStyle("-fx-text-fill: #8a2be2;");

            Button btnPart = new Button(part);
            btnPart.getStyleClass().add("breadcrumb-button");
            btnPart.setOnAction(e -> navigateToBreadcrumb(target));

            breadcrumbsContainer.getChildren().addAll(arrow, btnPart);
        }
    }

    private void loadCurrentPathToTable() {
        if (currentArchiveFile == null && filesToPack.isEmpty()) return;

        if (currentArchiveFile != null) {
            List<ArchiveModel.Node> nodes = archiveModel.getChildrenAt(currentPath);
            fileTable.setItems(FXCollections.observableArrayList(nodes));
        }
        searchField.clear();
    }

    private void filterCurrentDirectory(String query) {
        if (currentArchiveFile == null) return;
        List<ArchiveModel.Node> currentNodes = archiveModel.getChildrenAt(currentPath);
        FilteredList<ArchiveModel.Node> filtered = new FilteredList<>(FXCollections.observableArrayList(currentNodes));

        if (query == null || query.trim().isEmpty()) {
            fileTable.setItems(filtered);
        } else {
            filtered.setPredicate(node -> node.getName().toLowerCase().contains(query.toLowerCase()));
            fileTable.setItems(filtered);
        }
    }

    private void loadPackListToTable() {
        ObservableList<ArchiveModel.Node> packNodes = FXCollections.observableArrayList();
        for (File f : filesToPack) {
            packNodes.add(new ArchiveModel.Node(f.getName(), f.getAbsolutePath(), f.isDirectory(), f.length(), "To Pack", null, null));
        }
        fileTable.setItems(packNodes);
    }

    private void removeSelectedFromPackList() {
        if (!filesToPack.isEmpty()) {
            List<ArchiveModel.Node> selected = new ArrayList<>(fileTable.getSelectionModel().getSelectedItems());
            for (ArchiveModel.Node node : selected) {
                filesToPack.removeIf(f -> f.getAbsolutePath().equals(node.getPath()));
            }
            loadPackListToTable();
            showStatus("Removed selected elements from preparation queue.");
        }
    }

   

    private void openArchiveFlow(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archives", "*.myash", "*.zip", "*.7z", "*.rar", "*.tar", "*.tar.gz", "*.iso"));
        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            openSpecificArchive(file);
        }
    }

    private void openSpecificArchive(File file) {
        currentArchiveFile = file;
        currentPath = "";
        filesToPack.clear();
        archiveModel = new ArchiveModel();
        updateBreadcrumbs();

        showProgress(true);
        showStatus("Analyzing archive structure...");

        Task<Void> parseTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (file.getName().endsWith(".myash")) {
                    byte[] data = Files.readAllBytes(file.toPath());
                    int markerIdx = findMarker(data);

                    byte[] publicZip = (markerIdx == -1) ? data : cutBytes(data, FAKE_RAR_HEADER.length, markerIdx);
                    byte[] privateZip = (markerIdx == -1) ? new byte[0] : cutBytes(data, markerIdx + MARKER.length, data.length);

                    parseZipBytes(publicZip, "Public Sector");
                    if (privateZip.length > 0) {
                        parseZipBytes(privateZip, "Secret Sector");
                    }
                } else {
                    parseWith7z(file);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                showProgress(false);
                showStatus("Archive loaded successfully.");
                buildFolderTree();
                loadCurrentPathToTable();
            }

            @Override
            protected void failed() {
                showProgress(false);
                showError("Failed to parse archive", getException());
            }
        };

        runAsyncTask(parseTask);
    }

    private void parseZipBytes(byte[] zipData, String sector) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipData))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    baos.write(buffer, 0, len);
                }
                archiveModel.addEntry(entry.getName(), entry.getSize() >= 0 ? entry.getSize() : baos.size(), sector, entry.isDirectory(), baos.toByteArray());
                zis.closeEntry();
            }
        }
    }

    private void parseWith7z(File file) throws Exception {
        Process process = new ProcessBuilder("7z", "l", "-slt", file.getAbsolutePath()).start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            String path = "";
            long size = 0;
            boolean isFolder = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Path = ") && !line.equals("Path = " + file.getAbsolutePath())) {
                    path = line.substring(7);
                } else if (line.startsWith("Size = ")) {
                    try {
                        size = Long.parseLong(line.substring(7).trim());
                    } catch (Exception ignored) {}
                } else if (line.startsWith("Folder = ")) {
                    isFolder = line.substring(9).trim().equals("+");
                } else if (line.isEmpty() && !path.isEmpty()) {
                    archiveModel.addEntry(path, size, "Standard", isFolder, null);
                    path = "";
                    size = 0;
                    isFolder = false;
                }
            }
        }
    }

    private void buildFolderTree() {
        TreeItem<String> rootItem = new TreeItem<>("Root");
        rootItem.setExpanded(true);
        populateTreeBranch(archiveModel.getRoot(), rootItem);
        folderTree.setRoot(rootItem);
    }

    private void populateTreeBranch(ArchiveModel.Node node, TreeItem<String> treeItem) {
        for (ArchiveModel.Node child : node.getChildren().values()) {
            if (child.isDirectory()) {
                TreeItem<String> childItem = new TreeItem<>(child.getName());
                treeItem.getChildren().add(childItem);
                populateTreeBranch(child, childItem);
            }
        }
    }

   

    private void extractSelectedFlow(Stage stage) {
        if (currentArchiveFile == null || fileTable.getSelectionModel().getSelectedItems().isEmpty()) {
            showError("No elements selected", new Exception("Please select one or more files from the list."));
            return;
        }

        DirectoryChooser chooser = new DirectoryChooser();
        File destDir = chooser.showDialog(stage);
        if (destDir == null) return;

        List<ArchiveModel.Node> selected = new ArrayList<>(fileTable.getSelectionModel().getSelectedItems());
        showProgress(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (currentArchiveFile.getName().endsWith(".myash")) {
                    int idx = 0;
                    for (ArchiveModel.Node node : selected) {
                        if (isCancelled()) break;
                        extractMyashNode(node, destDir.toPath());
                        updateProgress(++idx, selected.size());
                    }
                } else {
                    List<String> command = new ArrayList<>(List.of("7z", "x", currentArchiveFile.getAbsolutePath(), "-o" + destDir.getAbsolutePath(), "-y"));
                    for (ArchiveModel.Node node : selected) {
                        command.add(node.getPath());
                    }
                    Process proc = new ProcessBuilder(command).start();
                    proc.waitFor();
                }
                return null;
            }

            @Override
            protected void succeeded() {
                showProgress(false);
                showStatus("Selected items successfully extracted.");
                showAlert(Alert.AlertType.INFORMATION, "Success", "Extraction finished safely.");
            }

            @Override
            protected void failed() {
                showProgress(false);
                showError("Extraction failed", getException());
            }
        };

        runAsyncTask(task);
    }

    private void extractMyashNode(ArchiveModel.Node node, Path baseDest) throws IOException {
        Path targetPath = baseDest.resolve(node.getPath()).normalize();
        if (node.isDirectory()) {
            Files.createDirectories(targetPath);
            for (ArchiveModel.Node child : node.getChildren().values()) {
                extractMyashNode(child, baseDest);
            }
        } else {
            Files.createDirectories(targetPath.getParent());
            if (node.getFileData() != null) {
                Files.write(targetPath, node.getFileData());
            }
        }
    }

    private void extractAllFlow(Stage stage) {
        if (currentArchiveFile == null) return;

        DirectoryChooser chooser = new DirectoryChooser();
        File destDir = chooser.showDialog(stage);
        if (destDir == null) return;

        showProgress(true);
        showStatus("Extracting all elements...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (currentArchiveFile.getName().endsWith(".myash")) {
                    List<ArchiveModel.Node> rootNodes = new ArrayList<>(archiveModel.getRoot().getChildren().values());
                    int idx = 0;
                    for (ArchiveModel.Node node : rootNodes) {
                        if (isCancelled()) break;
                        extractMyashNode(node, destDir.toPath());
                        updateProgress(++idx, rootNodes.size());
                    }
                } else {
                    Process proc = new ProcessBuilder("7z", "x", currentArchiveFile.getAbsolutePath(), "-o" + destDir.getAbsolutePath(), "-y").start();
                    proc.waitFor();
                }
                return null;
            }

            @Override
            protected void succeeded() {
                showProgress(false);
                showStatus("All files extracted.");
                showAlert(Alert.AlertType.INFORMATION, "Success", "Full extraction finished successfully.");
            }

            @Override
            protected void failed() {
                showProgress(false);
                showError("Global extraction failed", getException());
            }
        };

        runAsyncTask(task);
    }

    private void createMyashFlow(Stage stage) {
        if (filesToPack.isEmpty()) {
            showError("Empty queue", new Exception("Drag and drop some files into the list first."));
            return;
        }

        ChoiceDialog<String> sectorDialog = new ChoiceDialog<>("Public", "Public", "Secret");
        sectorDialog.setTitle("Target Sector");
        sectorDialog.setHeaderText("Choose sector inside .myash container for added files:");
        var sectorResult = sectorDialog.showAndWait();
        if (sectorResult.isEmpty()) return;

        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName("butterfly_archive.myash");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Myash Container", "*.myash"));
        File saveFile = chooser.showSaveDialog(stage);
        if (saveFile == null) return;

        showProgress(true);
        showStatus("Compressing to .myash...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                    fos.write(FAKE_RAR_HEADER);

                    byte[] emptyZip = createZipBuffer(new ArrayList<>());
                    byte[] filledZip = createZipBuffer(new ArrayList<>(filesToPack));

                    if (sectorResult.get().equals("Public")) {
                        fos.write(filledZip);
                        fos.write(MARKER);
                        fos.write(emptyZip);
                    } else {
                        fos.write(emptyZip);
                        fos.write(MARKER);
                        fos.write(filledZip);
                    }
                }
                return null;
            }

            @Override
            protected void succeeded() {
                showProgress(false);
                filesToPack.clear();
                fileTable.getItems().clear();
                showStatus("Polyglot container created!");
                showAlert(Alert.AlertType.INFORMATION, "Success", "Polyglot .myash archive saved successfully.");
            }

            @Override
            protected void failed() {
                showProgress(false);
                showError("Myash compression failed", getException());
            }
        };

        runAsyncTask(task);
    }

    private void createStandardFlow(Stage stage) {
        if (filesToPack.isEmpty()) {
            showError("Empty queue", new Exception("Drag and drop some files into the list first."));
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("7-Zip Archive", "*.7z"),
                new FileChooser.ExtensionFilter("ZIP Archive", "*.zip"),
                new FileChooser.ExtensionFilter("GZip Tarball", "*.tar.gz")
        );
        File saveFile = chooser.showSaveDialog(stage);
        if (saveFile == null) return;

        showProgress(true);
        showStatus("Calling 7z compiler...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                List<String> command = new ArrayList<>(List.of("7z", "a", saveFile.getAbsolutePath()));
                for (File f : filesToPack) {
                    command.add(f.getAbsolutePath());
                }
                Process proc = new ProcessBuilder(command).start();
                proc.waitFor();
                return null;
            }

            @Override
            protected void succeeded() {
                showProgress(false);
                filesToPack.clear();
                fileTable.getItems().clear();
                showStatus("Standard archive generated via 7z.");
                showAlert(Alert.AlertType.INFORMATION, "Success", "Compression finished successfully.");
            }

            @Override
            protected void failed() {
                showProgress(false);
                showError("7z compilation failed", getException());
            }
        };

        runAsyncTask(task);
    }

   

    private byte[] createZipBuffer(List<File> files) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (File file : files) {
                addFileToZip(zos, file, file.getName());
            }
        }
        return baos.toByteArray();
    }

    private void addFileToZip(ZipOutputStream zos, File file, String name) throws IOException {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    addFileToZip(zos, child, name + "/" + child.getName());
                }
            }
        } else {
            ZipEntry entry = new ZipEntry(name);
            zos.putNextEntry(entry);
            Files.copy(file.toPath(), zos);
            zos.closeEntry();
        }
    }

    private int findMarker(byte[] data) {
        for (int i = 0; i <= data.length - MARKER.length; i++) {
            boolean found = true;
            for (int j = 0; j < MARKER.length; j++) {
                if (data[i + j] != MARKER[j]) {
                    found = false;
                    break;
                }
            }
            if (found) return i;
        }
        return -1;
    }

    private byte[] cutBytes(byte[] src, int start, int end) {
        byte[] dest = new byte[end - start];
        System.arraycopy(src, start, dest, 0, dest.length);
        return dest;
    }

    private void runAsyncTask(Task<Void> task) {
        activeTask = task;
        progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }

    private void showProgress(boolean show) {
        Platform.runLater(() -> {
            progressBar.setVisible(show);
            btnCancel.setVisible(show);
        });
    }

    private void showStatus(String text) {
        Platform.runLater(() -> statusLabel.setText(text));
    }

    private void showError(String context, Throwable ex) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Encountered");
            alert.setHeaderText(context);
            alert.setContentText(ex.getMessage() != null ? ex.getMessage() : "An unexpected issue happened.");
            alert.showAndWait();
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    private void toggleTheme() {
        isDarkTheme = !isDarkTheme;
        scene.getStylesheets().clear();
        if (isDarkTheme) {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } else {
            scene.getStylesheets().add(getClass().getResource("/light.css").toExternalForm());
        }
    }

    private boolean isArchiveFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".myash") || name.endsWith(".zip") || name.endsWith(".7z") ||
                name.endsWith(".rar") || name.endsWith(".tar") || name.endsWith(".gz") || name.endsWith(".iso");
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %cB", bytes / Math.pow(1024, exp), pre);
    }

    public static void main(String[] args) {
        launch(args);
    }
}