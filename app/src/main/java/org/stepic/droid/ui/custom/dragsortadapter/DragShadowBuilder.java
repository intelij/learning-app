/*
 * Copyright (C) 2015 Vincent Mi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.stepic.droid.ui.custom.dragsortadapter;

import android.graphics.Canvas;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.View;

public class DragShadowBuilder extends View.DragShadowBuilder {

    public static final String TAG = DragShadowBuilder.class.getSimpleName();

    final Point touchPoint = new Point();

    public DragShadowBuilder(View view, Point touchPoint) {
        super(view);
        this.touchPoint.set(touchPoint.x, touchPoint.y);
    }

    @Override
    public void onProvideShadowMetrics(@NonNull Point shadowSize, @NonNull Point shadowTouchPoint) {
        final View view = getView();
        if (view != null) {
            shadowSize.set(view.getWidth(), view.getHeight());
            shadowTouchPoint.set(touchPoint.x, touchPoint.y);
        } else {
//            Log.d(TAG, "Asked for drag thumb metrics but no view");
        }
    }

    @Override
    public void onDrawShadow(@NonNull Canvas canvas) {
        super.onDrawShadow(canvas);
    }
}
