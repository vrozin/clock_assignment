package com.vrozin.assignment.services.models

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout

class CustomConstraintLayout(context: Context, attributeSet: AttributeSet)
    : ConstraintLayout(context, attributeSet) {

    /** If we don't override the "removeDetachedView" so it would ignore the "animate"
     * property, we are going to be getting an error while trying to add a view to ConstraintLayout
     * right after removing it because of the animation going on. We either set a delay on adding
     * a View to the layout, or remove animation from detaching*/
    override fun removeDetachedView(child: View?, animate: Boolean) {
        super.removeDetachedView(child,false)
    }
}