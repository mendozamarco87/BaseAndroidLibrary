package com.mm87.android.lib.base.annotation;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by marco.mendoza on 13/01/2017.
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface OnClick {
    /**
     * View IDs to which the method will be bound.
     */
    @IdRes int[] value() default {View.NO_ID};


    /**
     * An implementation of OnClickListener that attempts to lazily load a
     * named click handling method from a parent or ancestor context.
     */
    public static class DeclaredOnClickListener implements View.OnClickListener {
        private final View mHostView = null; // correccion
        private final String mMethodName;

        private Method mResolvedMethod;
        private Context mResolvedContext;
        private Object holder;

        public <VHOLDER> DeclaredOnClickListener(@NonNull VHOLDER context, @NonNull Method methodName) {
            mMethodName = methodName.getName();
            mResolvedMethod = methodName;
            holder = context;
        }

        @Override
        public void onClick(@NonNull View v) {
//            if (mResolvedMethod == null) {
//                resolveMethod(mHostView.getContext(), mMethodName);
//            }

            try {
                mResolvedMethod.invoke(holder, v);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(
                        "Could not execute non-public method for android:onClick", e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(
                        "Could not execute method for android:onClick", e);
            }
        }

        @NonNull
        private void resolveMethod(@Nullable Context context, @NonNull String name) {
            while (context != null) {
                try {
                    if (!context.isRestricted()) {
                        Class c = context.getClass();
                        final Method method = context.getClass().getMethod(mMethodName, View.class);
                        if (method != null) {
                            mResolvedMethod = method;
                            mResolvedContext = context;
                            return;
                        }
                    }
                } catch (NoSuchMethodException e) {
                    // Failed to find method, keep searching up the hierarchy.
                }

                if (context instanceof ContextWrapper) {
                    context = ((ContextWrapper) context).getBaseContext();
                } else {
                    // Can't search up the hierarchy, null out and fail.
                    context = null;
                }
            }

            final int id = mHostView.getId();
            final String idText = id == -1 ? "" : " with id '"
                    + mHostView.getContext().getResources().getResourceEntryName(id) + "'";
            throw new IllegalStateException("Could not find method " + mMethodName
                    + "(View) in a parent or ancestor Context for android:onClick "
                    + "attribute defined on view " + mHostView.getClass() + idText);
        }
    }
}
