package com.myashka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArchiveModel {

    public static class Node {
        private final String name;
        private final String path;
        private final boolean isDirectory;
        private final long size;
        private final String sector;
        private final byte[] fileData;
        private final Node parent;
        private final Map<String, Node> children = new HashMap<>();

        public Node(String name, String path, boolean isDirectory, long size, String sector, byte[] fileData, Node parent) {
            this.name = name;
            this.path = path;
            this.isDirectory = isDirectory;
            this.size = size;
            this.sector = sector;
            this.fileData = fileData;
            this.parent = parent;
        }

        public String getName() { return name; }
        public String getPath() { return path; }
        public boolean isDirectory() { return isDirectory; }
        public long getSize() { return size; }
        public String getSector() { return sector; }
        public byte[] getFileData() { return fileData; }
        public Node getParent() { return parent; }
        public Map<String, Node> getChildren() { return children; }
    }

    private final Node root = new Node("", "", true, 0, "Root", null, null);

    public Node getRoot() {
        return root;
    }

    public void addEntry(String fullPath, long size, String sector, boolean isDirectory, byte[] data) {
        String normalizedPath = fullPath.replace("\\", "/");
        if (normalizedPath.startsWith("/")) {
            normalizedPath = normalizedPath.substring(1);
        }
        if (normalizedPath.endsWith("/")) {
            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
        }

        if (normalizedPath.isEmpty()) return;

        String[] parts = normalizedPath.split("/");
        Node current = root;
        StringBuilder currentPathAccumulator = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (currentPathAccumulator.length() > 0) {
                currentPathAccumulator.append("/");
            }
            currentPathAccumulator.append(part);

            boolean isLast = (i == parts.length - 1);
            boolean nodeIsDir = !isLast || isDirectory;

            if (!current.children.containsKey(part)) {
                Node child = new Node(
                        part,
                        currentPathAccumulator.toString(),
                        nodeIsDir,
                        isLast ? size : 0,
                        sector,
                        isLast ? data : null,
                        current
                );
                current.children.put(part, child);
            }
            current = current.children.get(part);
        }
    }

    public List<Node> getChildrenAt(String path) {
        Node node = findNode(path);
        if (node != null) {
            return new ArrayList<>(node.getChildren().values());
        }
        return new ArrayList<>();
    }

    public Node findNode(String path) {
        if (path == null || path.isEmpty()) {
            return root;
        }
        String normalizedPath = path.replace("\\", "/");
        String[] parts = normalizedPath.split("/");
        Node current = root;

        for (String part : parts) {
            current = current.children.get(part);
            if (current == null) return null;
        }
        return current;
    }
}