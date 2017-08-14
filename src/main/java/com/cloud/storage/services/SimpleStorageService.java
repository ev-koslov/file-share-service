package com.cloud.storage.services;


import com.cloud.common.blo.ServerFile;
import com.cloud.common.components.AspectWatcherBean;
import com.cloud.common.utils.Randomizer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class SimpleStorageService implements StorageService {
    protected String datastoreRoot;

    protected byte[] buffer = new byte[1024000];

    protected final AspectWatcherBean watcherBean;

    public SimpleStorageService(String datastoreRoot, AspectWatcherBean watcherBean) throws IOException {
        Path dirPath = Paths.get(datastoreRoot);

        if (!Files.isDirectory(dirPath) || !Files.isWritable(dirPath) || !Files.isReadable(dirPath)) {
            throw new IllegalArgumentException(String.format("Specified path %s must be a readable/writable directory", datastoreRoot));
        }

        this.datastoreRoot = datastoreRoot;
        this.watcherBean = watcherBean;
    }

    @Override
    public ServerFile store(ServerFile file) throws IOException {
        String storedName = Randomizer.randomString(10)+".stg";

        Path filePath = Paths.get(datastoreRoot+ File.separatorChar+ storedName);

        InputStream source = null;
        OutputStream target = null;

        try {
            source = file.getDataStream();
            target = new BufferedOutputStream(Files.newOutputStream(filePath));

            int read;

//            attempting to store file to local file system
            do {
                synchronized (buffer) {
                    read = source.read(buffer);
                    if (read >= 0) {
                        target.write(buffer, 0, read);
                        file.setBytesTransfered(file.getBytesTransfered() + read);

                        watcherBean.watch(file);
                    }
                }
            } while (read >= 0);

        } catch (IOException e) {

            //if store fails, deleting file
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e1) {

            }
            throw e;

        } finally {

            //closing file data stream
            if (source != null) {
                try {
                    source.close();
                } catch (IOException e) {

                }
            }

            //closing target stream
            if (target != null) {
                try {
                    target.close();
                } catch (IOException e) {

                }
            }
        }

        file.setStoredName(storedName);

        return file;
    }

    @Override
    public ServerFile load(ServerFile file) throws IOException {
        Path filePath = Paths.get(datastoreRoot + File.separatorChar + file.getStoredName());
        InputStream source = Files.newInputStream(filePath, StandardOpenOption.READ);
        file.setDataStream(source);
        return file;
    }

    @Override
    public void delete(ServerFile file) throws IOException {
        Path filePath = Paths.get(datastoreRoot + File.separatorChar + file.getStoredName());
        Files.deleteIfExists(filePath);
    }

}
