package sample;

import java.io.File;

public class FileRecord {
    private File actualFile;
    private String fileName;

    public FileRecord(String fileName) {
        this.fileName = fileName;
    }
    public FileRecord(File actualFile) {
        this.actualFile = actualFile;
        this.fileName = actualFile.getName();
    }
    public String getFileName() {
        //return this.fileName;
        return fileName;
    }
    public File getActualFile() {
        return actualFile;
    }
}
