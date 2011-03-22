package com.thoughtworks.mm;

import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.view.Menu;
import android.view.MenuItem;

/**
 * @author Nicolas Gramlich
 * @since 22:10:28 - 11.04.2010
 */
public abstract class BaseExample extends BaseGameActivity {

    private static final int MENU_TRACE = Menu.FIRST;

    static final int CAMERA_WIDTH = 640;
	static final int CAMERA_HEIGHT = 480;


	/* The categories. */
	public static final short CATEGORYBIT_WALL = 1;
	public static final short CATEGORYBIT_BOX = 2;
	public static final short CATEGORYBIT_CIRCLE = 4;
	public static final short CATEGORYBIT_HOLE = 8;


    /* And what should collide with what. */
    public static final short MASKBITS_WALL = CATEGORYBIT_WALL + CATEGORYBIT_BOX + CATEGORYBIT_CIRCLE;
    // CATEGORYBIT_CIRCLE
    public static final short MASKBITS_CIRCLE = CATEGORYBIT_WALL + CATEGORYBIT_CIRCLE; // Missing: CATEGORYBIT_BOX
    public static final short MASKBITS_HOLE = 0; // Missing: everything
    



    @Override
    public boolean onCreateOptionsMenu(final Menu pMenu) {
        pMenu.add(Menu.NONE, MENU_TRACE, Menu.NONE, "Start Method Tracing");
        return super.onCreateOptionsMenu(pMenu);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu pMenu) {
        pMenu.findItem(MENU_TRACE).setTitle(this.mEngine.isMethodTracing() ? "Stop Method Tracing" : "Start Method Tracing");
        return super.onPrepareOptionsMenu(pMenu);
    }

    @Override
    public boolean onMenuItemSelected(final int pFeatureId, final MenuItem pItem) {
        switch (pItem.getItemId()) {
        case MENU_TRACE:
            if (this.mEngine.isMethodTracing()) {
                this.mEngine.stopMethodTracing();
            } else {
                this.mEngine.startMethodTracing("AndEngine_" + System.currentTimeMillis() + ".trace");
            }
            return true;
        default:
            return super.onMenuItemSelected(pFeatureId, pItem);
        }
    }

}
