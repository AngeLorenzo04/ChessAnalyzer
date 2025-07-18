package org.example.UI.stringConverter;// ArchiveStringConverter.java
import javafx.util.StringConverter;
import org.example.API.ChessArchive;

public class ArchiveStringConverter extends StringConverter<ChessArchive> {
    @Override
    public String toString(ChessArchive archive) {
        return archive != null ? archive.toString() : "";
    }

    @Override
    public ChessArchive fromString(String s) {
        return null;
    }


}