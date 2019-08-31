package com.mm87.android.lib.base.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IdRes;

/**
 * Created by marco.mendoza on 12/01/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface BindView {
    @IdRes int value();
}