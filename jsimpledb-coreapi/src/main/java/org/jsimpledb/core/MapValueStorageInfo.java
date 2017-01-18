
/*
 * Copyright (C) 2015 Archie L. Cobbs. All rights reserved.
 */

package org.jsimpledb.core;

import org.jsimpledb.kv.KVPairIterator;
import org.jsimpledb.util.ByteReader;

class MapValueStorageInfo<K, V> extends ComplexSubFieldStorageInfo<V> {

    final FieldType<K> keyFieldType;

    MapValueStorageInfo(MapField<K, V> field) {
        super(field.valueField);
        this.keyFieldType = field.keyField.fieldType.genericizeForIndex();
    }

    CoreIndex2<V, ObjId, K> getValueIndex(Transaction tx) {
        return new CoreIndex2<>(tx, new Index2View<>(this.storageId, this.fieldType, FieldTypeRegistry.OBJ_ID, this.keyFieldType));
    }

    @Override
    CoreIndex<V, ObjId> getIndex(Transaction tx) {
        return this.getValueIndex(tx).asIndex();
    }

    @Override
    void unreference(Transaction tx, ObjId target, ObjId referrer, byte[] prefix) {
        final FieldTypeMap<?, ?> fieldMap = (FieldTypeMap<?, ?>)tx.readMapField(referrer, this.parentStorageId, false);
        for (KVPairIterator i = new KVPairIterator(tx.kvt, prefix); i.hasNext(); ) {
            final ByteReader reader = new ByteReader(i.next().getKey());
            reader.skip(prefix.length);
            fieldMap.remove(fieldMap.keyFieldType.read(reader));
        }
    }

// Object

    @Override
    public String toString() {
        return "map value field with " + this.fieldType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!super.equals(obj))
            return false;
        final MapValueStorageInfo<?, ?> that = (MapValueStorageInfo<?, ?>)obj;
        return this.keyFieldType.equals(that.keyFieldType);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.keyFieldType.hashCode();
    }
}

