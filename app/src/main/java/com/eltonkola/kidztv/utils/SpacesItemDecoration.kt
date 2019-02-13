package com.eltonkola.kidztv.utils

import android.content.res.Resources
import android.graphics.Rect
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View


class SpacesItemDecoration @JvmOverloads constructor(
        res: Resources,
        @DimenRes spacingResId: Int,
        @DimenRes topMarginResId: Int? = null,
        @DimenRes bottomMarginResId: Int? = null,
        @DimenRes leftMarginResId: Int? = null,
        @DimenRes rightMarginResId: Int? = null
) : RecyclerView.ItemDecoration() {

    private val topMargin: Int = topMarginResId?.let { res.getDimensionPixelSize(topMarginResId) }
            ?: 0
    private val bottomMargin: Int = bottomMarginResId?.let { res.getDimensionPixelSize(bottomMarginResId) }
            ?: 0
    private val leftMargin: Int = leftMarginResId?.let { res.getDimensionPixelSize(leftMarginResId) }
            ?: 0
    private val rightMargin: Int = rightMarginResId?.let { res.getDimensionPixelSize(rightMarginResId) }
            ?: 0
    private val spacing: Int = res.getDimensionPixelSize(spacingResId) / 2

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        when (parent.layoutManager) {
            is GridLayoutManager -> offsetWithGrid(outRect, view, parent, parent.layoutManager as GridLayoutManager)
            is LinearLayoutManager -> offsetWithList(outRect, view, parent, parent.layoutManager as LinearLayoutManager)
            null -> throw NullPointerException("LayoutManager is missing")
            else -> TODO("Unsupported layout manager : ${parent.layoutManager}")
        }
    }

    private fun offsetWithList(outRect: Rect, view: View, parent: RecyclerView, layoutManager: LinearLayoutManager) {
        when (layoutManager.orientation) {
            RecyclerView.VERTICAL -> {
                outRect.top = spacing
                outRect.bottom = spacing
                outRect.left = leftMargin
                outRect.right = rightMargin
                when (parent.getChildLayoutPosition(view)) {
                    0 -> outRect.top = topMargin
                    layoutManager.itemCount - 1 -> outRect.bottom = bottomMargin
                }
            }
            RecyclerView.HORIZONTAL -> {
                outRect.top = 0
                outRect.bottom = 0
                outRect.left = -leftMargin
                outRect.right = 0
                when (parent.getChildLayoutPosition(view)) {
                    0 -> outRect.left = 0
                }
            }
        }
    }

    private fun offsetWithGrid(outRect: Rect, view: View, parent: RecyclerView, layoutManager: GridLayoutManager) {
        val viewPosition = parent.getChildAdapterPosition(view)
        val numTotalRows = Math.ceil(layoutManager.itemCount.toDouble() / layoutManager.spanCount).toInt()

        val isLeftEdge = viewPosition % layoutManager.spanCount == 0
        val isRightEdge = viewPosition % layoutManager.spanCount == layoutManager.spanCount - 1
        val isTopEdge = viewPosition / layoutManager.spanCount == 0
        val isBottomEdge = viewPosition / layoutManager.spanCount == numTotalRows - 1

        val isTopLeft = isTopEdge && isLeftEdge
        val isTopRight = isTopEdge && isRightEdge
        val isBottomLeft = isBottomEdge && isLeftEdge
        val isBottomRight = isBottomEdge && isRightEdge

        outRect.apply {
            left = spacing
            right = spacing
            top = spacing
            bottom = spacing
            when {
                isTopLeft -> {
                    top = topMargin
                    left = leftMargin
                }
                isTopRight -> {
                    top = topMargin
                    right = rightMargin
                }
                isBottomLeft -> {
                    bottom = bottomMargin
                    left = leftMargin
                }
                isBottomRight -> {
                    bottom = bottomMargin
                    right = rightMargin
                }
                isTopEdge ->
                    top = topMargin
                isBottomEdge ->
                    bottom = bottomMargin
                isLeftEdge ->
                    left = leftMargin
                isRightEdge ->
                    right = rightMargin
            }
        }
    }
}