
/*
 * Copyright (C) 2015 Archie L. Cobbs. All rights reserved.
 */

package org.jsimpledb.core;

/**
 * Represents storage associated with an index.
 */
abstract class IndexStorageInfo extends StorageInfo {

    IndexStorageInfo(int storageId) {
        super(storageId);
    }
}

