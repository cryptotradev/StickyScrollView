package com.amar.library.ui.presenter

import android.util.Log
import androidx.annotation.StyleableRes
import com.amar.library.provider.interfaces.IResourceProvider
import com.amar.library.provider.interfaces.IScreenInfoProvider
import com.amar.library.ui.presentation.IStickyScrollPresentation
import java.lang.Integer.min

/**
 * Created by Amar Jain on 17/03/17.
 */
class StickyScrollPresenter(
    stickyScrollPresentation: IStickyScrollPresentation,
    screenInfoProvider: IScreenInfoProvider,
    typedArrayResourceProvider: IResourceProvider
) {
    private val mTypedArrayResourceProvider: IResourceProvider
    private val mStickyScrollPresentation: IStickyScrollPresentation
    private val mDeviceHeight: Int
//    private var mStickyFooterHeight = 0
//    private var mStickyFooterInitialTranslation = 0
//    private var mStickyFooterInitialLocation = 0
    private var mStickyHeaderInitialLocation = 0
    private var mStickyTopHeaderInitialLocation = 0
//    var isFooterSticky = false
//        private set
    var isHeaderSticky = false
        private set

    var isTopHeaderSticky = false
        private set

    var mScrolled = false
    fun onGlobalLayoutChange(
        @StyleableRes headerTop: Int,
        @StyleableRes headerRes: Int,
        @StyleableRes headerContainerRes: Int,
        @StyleableRes footerRes: Int
    ) {
        val headerTopId = mTypedArrayResourceProvider.getResourceId(headerTop)
        val headerId = mTypedArrayResourceProvider.getResourceId(headerRes)
        val headerContainerId = mTypedArrayResourceProvider.getResourceId(headerContainerRes)
        if (headerId != 0) {
            mStickyScrollPresentation.initHeaderView(headerTopId, headerId, headerContainerId)
        }
//        val footerId = mTypedArrayResourceProvider.getResourceId(footerRes)
//        if (footerId != 0) {
//            mStickyScrollPresentation.initFooterView(footerId)
//        }
        mTypedArrayResourceProvider.recycle()
    }

//    fun initStickyFooter(measuredHeight: Int, initialStickyFooterLocation: Int) {
//        mStickyFooterHeight = measuredHeight
//        mStickyFooterInitialLocation = initialStickyFooterLocation
//        mStickyFooterInitialTranslation =
//            mDeviceHeight - initialStickyFooterLocation - mStickyFooterHeight
//        if (mStickyFooterInitialLocation > mDeviceHeight - mStickyFooterHeight) {
//            mStickyScrollPresentation.stickFooter(mStickyFooterInitialTranslation)
//            isFooterSticky = true
//        }
//    }

    fun initStickyHeader(headerTop: Int) {
        Log.i(LOG_TAG, "initStickyHeader() $headerTop")
        mStickyHeaderInitialLocation = headerTop
    }

    fun initStickyHeaderTop(headerTop: Int) {
        Log.i(LOG_TAG, "initStickyHeaderTop() $headerTop")

        mStickyTopHeaderInitialLocation = headerTop
    }

    fun onScroll(scrollY: Int) {
        mScrolled = true
        //handleFooterStickiness(scrollY)
        handleHeaderStickiness(scrollY)
    }

//    private fun handleFooterStickiness(scrollY: Int) {
//        isFooterSticky = if (scrollY > mStickyFooterInitialLocation - mDeviceHeight + mStickyFooterHeight) {
//            mStickyScrollPresentation.freeFooter()
//            false
//        } else {
//            mStickyScrollPresentation.stickFooter(mStickyFooterInitialTranslation + scrollY)
//            true
//        }
//    }

    private fun handleHeaderStickiness(scrollY: Int) {
        if(mStickyTopHeaderInitialLocation == 0) {
            mStickyTopHeaderInitialLocation = mStickyHeaderInitialLocation
        }

        if (scrollY > Math.min(mStickyHeaderInitialLocation, mStickyTopHeaderInitialLocation)) {
            mStickyScrollPresentation.stickHeader(scrollY - mStickyHeaderInitialLocation)
            isHeaderSticky = true
        } else {
            mStickyScrollPresentation.freeHeader()
            isHeaderSticky = false
        }

        if(scrollY > mStickyTopHeaderInitialLocation) {
            mStickyScrollPresentation.stickTopHeader(scrollY - mStickyTopHeaderInitialLocation)
            isTopHeaderSticky = true
        } else {
            mStickyScrollPresentation.freeTopHeader()
            isTopHeaderSticky = false
        }
    }

//    fun recomputeFooterLocation(footerTop: Int) {
//        if (mScrolled) {
//            mStickyFooterInitialTranslation = mDeviceHeight - footerTop - mStickyFooterHeight
//            mStickyFooterInitialLocation = footerTop
//        } else {
//            initStickyFooter(mStickyFooterHeight, footerTop)
//        }
//        handleFooterStickiness(mStickyScrollPresentation.currentScrollYPos)
//    }

    fun recomputeHeaderLocation(headerTop: Int) {
        initStickyHeader(headerTop)
        handleHeaderStickiness(mStickyScrollPresentation.currentScrollYPos)
    }

    fun recomputeTopHeaderLocation(headerTop: Int) {
        initStickyHeaderTop(headerTop)
        handleHeaderStickiness(mStickyScrollPresentation.currentScrollYPos)
    }

    companion object {
        private const val LOG_TAG = "StickyScrollPresenter"
    }

    init {
        mDeviceHeight = screenInfoProvider.screenHeight
        mTypedArrayResourceProvider = typedArrayResourceProvider
        mStickyScrollPresentation = stickyScrollPresentation
    }
}