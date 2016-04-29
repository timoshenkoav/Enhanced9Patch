package com.tunebrains.enhanced9patch;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Alexandr Timoshenko <thick.tav@gmail.com> on 4/28/16.
 */
public class Enhanced9Patch extends Drawable {
    public static class StretchSpace {
        public int start = -1;
        public int end = -1;
        public int index;
        public int width() {
            return end - start;
        }
    }

    protected final Bitmap mDrawable;
    protected final Bitmap mResultBitmap;
    protected final Canvas mResultCanvas;
    protected final Paint mPaint;
    protected int mIntrinsicWidth;
    protected int mIntrinsicHeight;
    //Width
    protected int[] mWidthStretchWidths;
    protected int mTotalWidth;
    protected List<StretchSpace> mWidthStretchSpaces;
    protected List<StretchSpace> mWidthSpaces;
    //Height
    protected int mTotalHeight;
    protected List<StretchSpace> mHeightStretchSpaces;
    protected List<StretchSpace> mHeightSpaces;
    protected int[] mHeightStretchWidths;

    //MainStretch

    int mMainStretchWidthIndex=0;
    int mMainStretchHeightIndex=0;

    public void setMainStretchHeightIndex(int pMainStretchHeightIndex) {
        mMainStretchHeightIndex = pMainStretchHeightIndex;
    }

    public void setMainStretchWidthIndex(int pMainStretchWidthIndex) {
        mMainStretchWidthIndex = pMainStretchWidthIndex;
    }

    protected Rect mPaddings;


    public Enhanced9Patch(Bitmap pDrawable) {
        mDrawable = pDrawable;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mIntrinsicWidth = pDrawable.getWidth();
        mIntrinsicHeight = pDrawable.getHeight();
        mResultBitmap = Bitmap.createBitmap(mIntrinsicWidth - 3, mIntrinsicHeight - 3, Bitmap.Config.ARGB_8888);
        mResultCanvas = new Canvas(mResultBitmap);

        mResultCanvas.save();
        mResultCanvas.translate(-1, -1);
        mResultCanvas.drawBitmap(mDrawable, 0, 0, mPaint);
        mResultCanvas.restore();

        //Width
        mWidthStretchSpaces = new LinkedList<>();
        fillStretchRegionsWidth(mWidthStretchSpaces, 0);
        mWidthSpaces = new LinkedList<>();
        fillStretchRegionsWidth(mWidthSpaces, mDrawable.getHeight() - 2);

        mWidthStretchWidths = new int[mWidthStretchSpaces.size()];
        for (StretchSpace lStretchSpace : mWidthStretchSpaces) {
            mWidthStretchWidths[lStretchSpace.index] = lStretchSpace.width();

        }

        //Height
        mHeightStretchSpaces = new LinkedList<>();
        fillStretchRegionsHeight(mHeightStretchSpaces,0);
        mHeightSpaces = new LinkedList<>();
        fillStretchRegionsHeight(mHeightSpaces, mDrawable.getWidth()-1);
        mHeightStretchWidths = new int[mHeightStretchSpaces.size()];
        for (StretchSpace lStretchSpace : mHeightStretchSpaces) {
            mHeightStretchWidths[lStretchSpace.index] = lStretchSpace.width();
        }

        recalcSize();

        mPaddings = new Rect();
        fillPaddings();


    }

    private void fillPaddings() {
        int left = 0;
        int right = 0;
        int top = 0;
        int bottom = 0;
        for (int i=0;i<mDrawable.getWidth();++i){
            int color = mDrawable.getPixel(i,mDrawable.getHeight()-1);
            if (color == Color.BLACK){
                left=i;
                break;
            }
        }
        for (int i=mDrawable.getWidth()-1;i>=0;--i){
            int color = mDrawable.getPixel(i,mDrawable.getHeight()-1);
            if (color == Color.BLACK){
                right=mDrawable.getWidth()-i;
                break;
            }
        }
        for (int i=0;i<mDrawable.getHeight();++i){
            int color = mDrawable.getPixel(mDrawable.getWidth()-1,i);
            if (color == Color.BLACK){
                top=i;
                break;
            }
        }
        for (int i=mDrawable.getHeight()-1;i>=0;--i){
            int color = mDrawable.getPixel(mDrawable.getWidth()-1,i);
            if (color == Color.BLACK){
                bottom=mDrawable.getHeight()-i;
                break;
            }
        }
        mPaddings.set(left, top, right, bottom);

    }


    public StretchSpace getWidthSpaces(int pIndex) {
        return mWidthSpaces.get(pIndex);
    }
    public StretchSpace getHeightSpaces(int pIndex) {
        return mHeightSpaces.get(pIndex);
    }

    public StretchSpace getWidthRegion(int pIndex){
        return mWidthStretchSpaces.get(pIndex);
    }

    public StretchSpace getHeightRegion(int pIndex){
        return mHeightStretchSpaces.get(pIndex);
    }


    public void stretchWidthRegionTo(int pIndex, int pNewWidth) {

        mWidthStretchWidths[pIndex] = pNewWidth;
        recalcSize();
    }
    public void stretchHeightRegionTo(int pIndex, int pNewWidth) {

        mHeightStretchWidths[pIndex] = pNewWidth;
        recalcSize();
    }

    private void recalcTotalWidth() {
        mTotalWidth = 0;
        for (int i = 0; i < mWidthStretchWidths.length; ++i) {
            mTotalWidth += mWidthStretchWidths[i];
        }
    }
    private void recalcTotalHeight() {
        mTotalHeight = 0;
        for (int i = 0; i < mHeightStretchWidths.length; ++i) {
            mTotalHeight += mHeightStretchWidths[i];
        }
    }

    public int getRegionWidth(int pIndex) {
        StretchSpace lStretchSpace = mWidthStretchSpaces.get(pIndex);
        return lStretchSpace.end - lStretchSpace.start;
    }


    private void fillStretchRegionsHeight(List<StretchSpace> pSpaces, int x) {

        StretchSpace lStretchSpace = new StretchSpace();
        int lastColor = Integer.MAX_VALUE;
        for (int i = 0; i < mDrawable.getHeight(); ++i) {
            int color = mDrawable.getPixel(x, i);
            if (lastColor != color) {
                if (lStretchSpace.start == -1) {
                    lStretchSpace.start = i;
                } else {
                    if (lStretchSpace.end == -1 && lStretchSpace.start != -1) {
                        lStretchSpace.end = i;
                        lStretchSpace.index = pSpaces.size();
                        pSpaces.add(lStretchSpace);
                        lStretchSpace = new StretchSpace();
                        lStretchSpace.start = i;
                    }
                }
                lastColor = color;
            }
        }
        if (lStretchSpace.start != -1 && lStretchSpace.end == -1) {
            lStretchSpace.end = mDrawable.getWidth() - 1;
            lStretchSpace.index = pSpaces.size();
            pSpaces.add(lStretchSpace);
        }
    }
    private void fillStretchRegionsWidth(List<StretchSpace> pSpaces, int y) {

        StretchSpace lStretchSpace = new StretchSpace();
        int lastColor = Integer.MAX_VALUE;
        for (int i = 0; i < mDrawable.getWidth(); ++i) {
            int color = mDrawable.getPixel(i, y);
            if (lastColor != color) {
                if (lStretchSpace.start == -1) {
                    lStretchSpace.start = i;
                } else {
                    if (lStretchSpace.end == -1 && lStretchSpace.start != -1) {
                        lStretchSpace.end = i;
                        lStretchSpace.index = pSpaces.size();
                        pSpaces.add(lStretchSpace);
                        lStretchSpace = new StretchSpace();
                        lStretchSpace.start = i;
                    }
                }
                lastColor = color;
            }
        }
        if (lStretchSpace.start != -1 && lStretchSpace.end == -1) {
            lStretchSpace.end = mDrawable.getWidth() - 1;
            lStretchSpace.index = pSpaces.size();
            pSpaces.add(lStretchSpace);
        }
    }


    @Override
    public void draw(Canvas canvas) {
        Rect lSourceRect = new Rect();
        Rect lDestRect = new Rect();
        int startX = 0;
        int startY = 0;

        for (StretchSpace lWidthSpace : mWidthStretchSpaces) {
            startY=0;
            for (StretchSpace lHeightSpace:mHeightStretchSpaces){
                lSourceRect.set(lWidthSpace.start, lHeightSpace.start, lWidthSpace.end, lHeightSpace.end);
                int destHeight = mHeightStretchWidths[lHeightSpace.index];
                lDestRect.set(startX, startY, startX + mWidthStretchWidths[lWidthSpace.index], startY+destHeight);
                startY+=destHeight;
                canvas.drawBitmap(mResultBitmap, lSourceRect, lDestRect, mPaint);
            }
            startX += mWidthStretchWidths[lWidthSpace.index];
        }
    }

    @Override
    public boolean getPadding(Rect padding) {
        padding.set(mPaddings);
        return true;
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    public void setBounds(Rect bounds) {
        super.setBounds(bounds);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        recalcSize();

    }

    private void recalcSize(){
        recalcTotalHeight();
        recalcTotalWidth();
        while (recalcWithBounds()){
            recalcTotalHeight();
            recalcTotalWidth();
        }
    }
    private boolean recalcWithBounds() {
        Rect lRect = getBounds();
        boolean result = false;
        if (mTotalWidth==0||mTotalHeight==0){
            return true;
        }
        if (lRect.width()==0 || lRect.height()==0)
            return false;
        if (mTotalWidth<lRect.width()){
            mWidthStretchWidths[mMainStretchWidthIndex] += lRect.width()-mTotalWidth;
            result = true;
//            stretchWidthRegionTo(mMainStretchWidthIndex,lRect.width()-mTotalWidth);
        }
        if (mTotalHeight<lRect.height()){
            mHeightStretchWidths[mMainStretchHeightIndex] += lRect.height()-mTotalHeight;
            result = true;
//            stretchHeightRegionTo(mMainStretchHeightIndex, lRect.height()-mTotalHeight);
        }
        return result;
    }

    @Override
    public int getIntrinsicWidth() {
        return mTotalWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mTotalHeight;
    }

    @Override
    public int getMinimumHeight() {
        return mTotalHeight;
    }

    @Override
    public int getMinimumWidth() {
        return mTotalWidth;
    }
}
