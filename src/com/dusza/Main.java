package com.dusza;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        Path basePath = FileSystems.getDefault().getPath("Data");
        RWHandler.init(basePath);

        CommandLineInterface cmi = new CommandLineInterface();
        cmi.start();
    }
}
