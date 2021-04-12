package com.vrozin.assignment.views

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import com.vrozin.assignment.R
import com.vrozin.assignment.databinding.ActivityMainBinding
import com.vrozin.assignment.view_models.MainActivityViewModel

class MainActivity : AppCompatActivity() {
    private companion object {
        const val TAG = "MainActivity"
        const val NETWORK_PERM_REQUEST_CODE = 1000
    }

    // Views
    private lateinit var binding: ActivityMainBinding
    private var animatorExpand: ValueAnimator? = null
    private var animatorShrink: ValueAnimator? = null
    private var dragShadow: CustomDragShadowBuilder? = null

    // ViewModels
    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == NETWORK_PERM_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> networkPermissionGranted()
                RESULT_CANCELED -> networkPermissionNotGranted()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initViewModels()
        initListeners()
    }

    override fun onStart() {
        super.onStart()

        checkNetworkPermissions()
    }

    private fun initViews() {
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
    }

    private fun initViewModels() {
        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
    }

    private fun initListeners() {
        mainActivityViewModel.currentTime.observe(this) { newTime ->
            binding.timer.text = newTime
            rotateView(binding.square)
        }

        binding.square.setOnLongClickListener { view ->
            val clipData = ClipData.newPlainText("label", "^_^")
            dragShadow = CustomDragShadowBuilder(view)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                view.startDragAndDrop(clipData, dragShadow, view, View.DRAG_FLAG_OPAQUE)
            } else {
                @Suppress("DEPRECATION")
                view.startDrag(clipData, dragShadow, view, 0)
            }

            return@setOnLongClickListener true
        }

        binding.main.setOnDragListener(CustomDragging({ onDragStart() },
                                                      { onDragEnd() },
                                                      { onDrop(binding.bottomArea,
                                                               binding.main)
                                                      },
                                                      { onDragEntered() },
                                                      { onDragExited() },
                                                      { x, y -> onDragListener(x, y) }))
        binding.bottomArea.setOnDragListener(CustomDragging({ onDragStart() },
                                                            { onDragEnd() },
                                                            { onDrop(binding.main,
                                                                     binding.bottomArea) },
                                                            { onDragEntered() },
                                                            { onDragExited() },
                                                            { x, y -> onDragListener(x, y) }))
    }

    private fun rotateView(view: View) {
        val startRotation = view.rotation - (view.rotation % 90)
        val endRotation = startRotation + 90f
        val animator = ObjectAnimator.ofFloat(view, "rotation", startRotation, endRotation)
        animator.duration = 600L
        animator.interpolator = LinearInterpolator()
        animator.start()

        dragShadow?.redrawWithNewAngle(endRotation)
    }

    private fun checkNetworkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED) {

            networkPermissionGranted()
        } else {
            networkPermissionNotGranted()
        }
    }

    private fun networkPermissionGranted() {
        mainActivityViewModel.networkPermissionGranted()
    }

    private fun networkPermissionNotGranted() {
        Toast.makeText(this, R.string.network_not_granted, Toast.LENGTH_SHORT).show()

        // Though, a looped permissions request is not the best approach, we need network
        // for this app to work
        ActivityCompat.requestPermissions(this,
                                          arrayOf(android.Manifest.permission.INTERNET),
                                          NETWORK_PERM_REQUEST_CODE)
    }

    private fun onDragStart() {
        binding.square.visibility = View.INVISIBLE
        peekBottomArea()
    }

    private fun onDragEnd() {
        binding.square.visibility = View.VISIBLE
        dragShadow = null
        if (!binding.bottomArea.children.contains(binding.square)) {
            shrinkBottomArea()
        }
    }

    private fun onDrop(fromLayout: ConstraintLayout, atLayout: ConstraintLayout) {
        fromLayout.apply {
            removeView(binding.square)
            requestLayout()
        }

        atLayout.apply {
            removeView(binding.square)
            requestLayout()
            addView(binding.square)
        }

        ConstraintSet().apply {
            clone(atLayout)
            connect(binding.square.id, ConstraintSet.TOP, atLayout.id, ConstraintSet.TOP)
            connect(binding.square.id, ConstraintSet.BOTTOM, atLayout.id, ConstraintSet.BOTTOM)
            connect(binding.square.id, ConstraintSet.START, atLayout.id, ConstraintSet.START)
            connect(binding.square.id, ConstraintSet.END, atLayout.id, ConstraintSet.END)

            applyTo(atLayout)
        }
    }

    private fun onDragEntered() {

    }

    private fun onDragExited() {

    }

    private fun onDragListener(x: Float, y: Float) {
//        binding.square.x = x - binding.square.width/2
//        binding.square.y = y - binding.square.height/2
    }

    private fun peekBottomArea() {
        animatorShrink?.cancel()
        val oldHeight = binding.bottomArea.height

        if (oldHeight == mainActivityViewModel.bottomAreaHeightInitial) {
            val newHeight = binding.square.height + mainActivityViewModel.bottomAreaMargin
            animatorExpand = ValueAnimator.ofInt(oldHeight, newHeight).apply {
                duration = 600L
                interpolator = LinearInterpolator()
                addUpdateListener {
                    binding.bottomArea.layoutParams.height = it.animatedValue as Int
                    binding.bottomArea.requestLayout()
                }
                start()
            }
        }
    }

    private fun shrinkBottomArea() {
        animatorExpand?.cancel()
        val oldHeight = binding.bottomArea.height

        if (oldHeight == binding.square.height + mainActivityViewModel.bottomAreaMargin) {
            val newHeight = mainActivityViewModel.bottomAreaHeightInitial
            animatorShrink = ValueAnimator.ofInt(oldHeight, newHeight).apply {
                duration = 600L
                interpolator = LinearInterpolator()
                addUpdateListener {
                    binding.bottomArea.layoutParams.height = it.animatedValue as Int
                    binding.bottomArea.requestLayout()
                }
                start()
            }
        }
    }

    private class CustomDragging(val onDragStart: () -> Unit,
                                 val onDragEnd: () -> Unit,
                                 val onDrop: () -> Unit,
                                 val onDragEntered: () -> Unit,
                                 val onDragExited: () -> Unit,
                                 val onDragLocation: (Float, Float) -> Unit): View.OnDragListener
    {
        override fun onDrag(v: View?, event: DragEvent?): Boolean {
            when (event?.action) {
                DragEvent.ACTION_DRAG_STARTED -> onDragStart()
                DragEvent.ACTION_DRAG_ENDED -> onDragEnd()
                DragEvent.ACTION_DRAG_ENTERED -> onDragEntered()
                DragEvent.ACTION_DRAG_EXITED -> onDragExited()
                DragEvent.ACTION_DRAG_LOCATION -> onDragLocation(event.x, event.y)
                DragEvent.ACTION_DROP -> onDrop()
            }
            return true
        }
    }

    private class CustomDragShadowBuilder(v: View): View.DragShadowBuilder(v) {
        var canvas: Canvas? = null

        override fun onProvideShadowMetrics(outShadowSize: Point?, outShadowTouchPoint: Point?) {
            super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint)
        }

        override fun onDrawShadow(canvas: Canvas?) {
            this.canvas = canvas
            super.onDrawShadow(canvas)
        }

        fun redrawWithNewAngle(angle: Float) {
            Log.d(TAG, "redrawWithNewAngle: angle = $angle")
            canvas?.let {
                it.rotate(angle, view.pivotX, view.pivotY)
                super.onDrawShadow(it)
            }
        }
    }
}