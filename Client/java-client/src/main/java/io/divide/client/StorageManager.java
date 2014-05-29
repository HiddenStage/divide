package io.divide.client;

import io.divide.shared.file.Storage;

/**
 * Created by williamwebb on 4/28/14.
 */
public interface StorageManager {
    public Storage getNewStorage(String fileName);
}
