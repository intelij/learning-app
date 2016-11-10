package org.stepic.droid.notifications;


import org.junit.Test;
import static junit.framework.Assert.*;

public class NotificationHelperTest {
    @Test
    public void testIsValidByAction_True() {
        assertTrue(NotificationHelper.INSTANCE.isNotificationValidByAction("soft_deadline_approach"));
    }

    @Test
    public void testIsValidByAction_False() {
        assertFalse(NotificationHelper.INSTANCE.isNotificationValidByAction("invalid_not_812uqw12"));
    }

    @Test
    public void testIsValidByAction_Null_False(){
        assertFalse(NotificationHelper.INSTANCE.isNotificationValidByAction(null));
    }
}
