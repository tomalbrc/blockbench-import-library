package de.tomalbrc.bil.file.importer;

import de.tomalbrc.bil.model.Model;

public interface ModelImporter<T> {
    Model importModel(T model);
}
