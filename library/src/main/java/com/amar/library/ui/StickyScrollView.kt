package com.amar.library.ui

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ScrollView
import com.amar.library.R
import com.amar.library.provider.ResourceProvider
import com.amar.library.provider.ScreenInfoProvider
import com.amar.library.provider.interfaces.IResourceProvider
import com.amar.library.provider.interfaces.IScreenInfoProvider
import com.amar.library.ui.interfaces.IScrollViewListener
import com.amar.library.ui.presentation.IStickyScrollPresentation
import com.amar.library.ui.presenter.StickyScrollPresenter

class StickyScrollView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ScrollView(context, attrs, defStyle), IStickyScrollPresentation {
    var scrollViewListener: IScrollViewListener? = null
    private var stickyFooterView: View? = null
    private var stickyHeaderTopView: View? = null
    private var stickyHeaderView: View? = null
    private var stickyHeaderContainerView: View? = null
    private val mStickyScrollPresenter: StickyScrollPresenter
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (stickyFooterView != null && !changed) {
            //mStickyScrollPresenter.recomputeFooterLocation(getRelativeTop(stickyFooterView));
        }
        if (stickyHeaderView != null) {
            mStickyScrollPresenter.recomputeHeaderLocation(topOfStickyHeaderView)
        }

        if(stickyHeaderTopView != null) {
            mStickyScrollPresenter.recomputeHeaderLocation(topOfStickyHeaderTopView)
        }
    }

    private fun getRelativeTop(myView: View): Int {
        return if (myView.parent === myView.rootView) {
            myView.top
        } else {
            myView.top + getRelativeTop(myView.parent as View)
        }
    }

    override fun initHeaderView(headerTopId: Int, headerId: Int, headerContainerId: Int) {
        stickyHeaderView = findViewById(headerId)
        stickyHeaderContainerView = findViewById(headerContainerId)
        stickyHeaderTopView = findViewById(headerTopId)
        mStickyScrollPresenter.initStickyHeader(topOfStickyHeaderView)
        mStickyScrollPresenter.initStickyHeaderTop(topOfStickyHeaderTopView)
    }

    private val topOfStickyHeaderView: Int
        private get() {
            var top = stickyHeaderView!!.top
            if (stickyHeaderContainerView != null) {
                top = stickyHeaderContainerView!!.top
            }
            return top
        }

    private val topOfStickyHeaderTopView: Int
        get() {
            return stickyHeaderTopView?.top ?: 0
        }

    override fun initFooterView(id: Int) {
        stickyFooterView = findViewById(id)
        //mStickyScrollPresenter.initStickyFooter(stickyFooterView.getMeasuredHeight(), getRelativeTop(stickyFooterView));
    }

    override fun freeHeader() {
        Log.i(LOG_TAG, "freeHeader()")
        if (stickyHeaderView != null) {
            stickyHeaderView?.translationY = 0f//translationY
            PropertySetter.setTranslationZ(stickyHeaderView, 0f)
        }
    }

    override fun freeTopHeader() {
        Log.i(LOG_TAG, "freeTopHeader()")
        if(stickyHeaderTopView != null) {
            stickyHeaderTopView?.translationY = 0f
            PropertySetter.setTranslationZ(stickyHeaderTopView, 0f)
        }
    }

    override fun freeFooter() {
        if (stickyFooterView != null) {
            stickyFooterView!!.translationY = 0f
        }
    }

    override fun stickHeader(translationY: Int) {
        Log.i(LOG_TAG, "stickHeader(), $translationY")
        if (stickyHeaderView != null) {
//            val summedTranslationY = if(stickyHeaderTopView != null) {
//                stickyHeaderTopView!!.height.toFloat() + translationY
//            } else {
//                translationY
//            }
            stickyHeaderView!!.translationY = translationY.toFloat()
            PropertySetter.setTranslationZ(stickyHeaderView, 1f)
        }
    }

    override fun stickTopHeader(translationY: Int) {
        Log.i(LOG_TAG, "stickTopHeader(), $translationY")
        if (stickyHeaderTopView != null) {
            stickyHeaderTopView!!.translationY = translationY.toFloat()
            PropertySetter.setTranslationZ(stickyHeaderTopView, 1f)
        }
    }

    override fun stickFooter(translationY: Int) {
        if (stickyFooterView != null) {
            stickyFooterView!!.translationY = translationY.toFloat()
        }
    }

    override fun getCurrentScrollYPos(): Int {
        return scrollY
    }

    override fun onScrollChanged(mScrollX: Int, mScrollY: Int, oldX: Int, oldY: Int) {
        super.onScrollChanged(mScrollX, mScrollY, oldX, oldY)
        mStickyScrollPresenter.onScroll(mScrollY)
        if (scrollViewListener != null) {
            scrollViewListener!!.onScrollChanged(mScrollX, mScrollY, oldX, oldY)
        }
    }

    val isFooterSticky: Boolean
        get() = false //mStickyScrollPresenter.isFooterSticky();
    val isHeaderSticky: Boolean
        get() = mStickyScrollPresenter.isHeaderSticky

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
        if (scrollViewListener != null) {
            scrollViewListener!!.onScrollStopped(clampedY)
        }
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(SUPER_STATE, super.onSaveInstanceState())
        bundle.putBoolean(SCROLL_STATE, mStickyScrollPresenter.mScrolled)
        return bundle
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        var state: Parcelable? = state
        if (state is Bundle) {
            val bundle = state
            mStickyScrollPresenter.mScrolled = bundle.getBoolean(SCROLL_STATE)
            state = bundle.getParcelable(SUPER_STATE)
        }
        super.onRestoreInstanceState(state)
    }

    companion object {
        private const val LOG_TAG = "StickyScrollView"
        private const val SCROLL_STATE = "scroll_state"
        private const val SUPER_STATE = "super_state"
    }

    init {
        val screenInfoProvider: IScreenInfoProvider = ScreenInfoProvider(context)
        val resourceProvider: IResourceProvider =
            ResourceProvider(context, attrs, R.styleable.StickyScrollView)
        mStickyScrollPresenter = StickyScrollPresenter(this, screenInfoProvider, resourceProvider)
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mStickyScrollPresenter.onGlobalLayoutChange(
                    R.styleable.StickyScrollView_stickyHeaderTop,
                    R.styleable.StickyScrollView_stickyHeader,
                    R.styleable.StickyScrollView_stickyHeaderContainer,
                    R.styleable.StickyScrollView_stickyFooter
                )
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
}