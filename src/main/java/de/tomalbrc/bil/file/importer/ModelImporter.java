package de.tomalbrc.bil.file.importer;

import de.tomalbrc.bil.core.model.Model;

public interface ModelImporter<T> {
    Model importModel(T model);
}
