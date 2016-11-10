package org.stepic.droid.store.structure;

public final class DbStructureBlock extends DBStructureBase {
    private static String[] usedColumns = null;

    public static final String BLOCKS = "blocks";

    public static final class Column {
        public static final String STEP_ID = "step_id";
        public static final String NAME = "name";
        public static final String TEXT = "block_text";
    }

    public static String[] getUsedColumns() {
        if (usedColumns == null) {
            usedColumns = new String[]{
                    Column.STEP_ID,
                    Column.NAME,
                    Column.TEXT
            };
        }
        return usedColumns;
    }
}
