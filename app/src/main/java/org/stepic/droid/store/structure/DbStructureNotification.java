package org.stepic.droid.store.structure;

public class DbStructureNotification extends DBStructureBase {
    private static String[] usedColumns = null;

    public static final String NOTIFICATIONS_TEMP = "notifications_temp";

    public static final class Column {
        public static final String ID = "notif_id";
        public static final String IS_UNREAD = "is_unread";
        public static final String IS_MUTED = "is_muted";
        public static final String IS_FAVOURITE = "is_favourite";
        public static final String TIME = "time";
        public static final String TYPE = "type";
        public static final String LEVEL = "level";
        public static final String PRIORITY = "priority";
        public static final String HTML_TEXT = "html_text";
        public static final String ACTION = "action";
        public static final String COURSE_ID = "course_id";

    }

    public static String[] getUsedColumns() {
        if (usedColumns == null) {
            usedColumns = new String[]{
                    Column.ID,
                    Column.IS_UNREAD,
                    Column.IS_MUTED,
                    Column.IS_FAVOURITE,
                    Column.TYPE,
                    Column.LEVEL,
                    Column.PRIORITY,
                    Column.HTML_TEXT,
                    Column.ACTION,
                    Column.COURSE_ID
            };
        }
        return usedColumns;
    }

}
