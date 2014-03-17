package de.mtthsfrdrch.an3mote.sw2;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlListItem;
import com.sonymobile.smartconnect.extension.advancedcontrolsample.controls.ControlManagerSmartWatch2;
import com.sonymobile.smartconnect.extension.advancedcontrolsample.controls.ManagedControlExtension;

import de.mtthsfrdrch.an3mote.An3Mote;
import de.mtthsfrdrch.an3mote.Keycode;

/**
 * Created by mtthsfrdrch on 2/26/14.
 */
public class ATVControlExtension extends ManagedControlExtension {

    protected int lastKnownPosition;
    public final static String EXTRA_INITIAL_POSITION = "EXTRA_INITIAL_POSITION";

    public ATVControlExtension(Context context, String hostAppPackageName,
                               ControlManagerSmartWatch2 controlManager, Intent intent) {
        super(context, hostAppPackageName, controlManager, intent);
    }

    @Override
    public void onResume() {
        showLayout(R.layout.extension_atv_remote, null);
        sendListCount(R.id.gallery, 4);

        // If requested, move to the correct position in the list.
        lastKnownPosition = getIntent().getIntExtra(EXTRA_INITIAL_POSITION, 1);
        sendListPosition(R.id.gallery, lastKnownPosition);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Position is saved into Control's Intent, possibly to be used later.
        getIntent().putExtra(EXTRA_INITIAL_POSITION, lastKnownPosition);
    }

    @Override
    public void onRequestListItem(final int layoutReference, final int listItemPosition) {
        if (layoutReference != -1 && listItemPosition != -1 && layoutReference == R.id.gallery) {
            ControlListItem item = null;
            if (listItemPosition == 0) {
                item = createItem(listItemPosition, R.layout.item_power);
            } else if (listItemPosition == 1) {
                item = createItem(listItemPosition, R.layout.item_dpad);
            } else if (listItemPosition == 2) {
                item = createItem(listItemPosition, R.layout.item_media);
            } else if (listItemPosition == 3) {
                item = createItem(listItemPosition, R.layout.item_tv);
            }
            if (item != null) {
                sendListItem(item);
            }
        }
    }

    @Override
    public void onListItemSelected(ControlListItem listItem) {
        super.onListItemSelected(listItem);
        // We save the last "selected" position, this is the current visible
        // list item index. The position can later be used on resume
        lastKnownPosition = listItem.listItemPosition;
    }

    @Override
    public void onListItemClick(final ControlListItem listItem, final int clickType,
                                final int itemLayoutReference) {
        Log.d("BLA", "Item clicked. Position " + listItem.listItemPosition
                + ", itemLayoutReference " + itemLayoutReference + ". Type was: "
                + (clickType == Control.Intents.CLICK_TYPE_SHORT ? "SHORT" : "LONG"));
        String keyCode = null;
        switch (itemLayoutReference) {
            case R.id.buttonBack:
                keyCode = Keycode.KEYCODE_BACK;
                break;
            case R.id.buttonChannelDown:
                keyCode = Keycode.KEYCODE_CHANNEL_DOWN;
                break;
            case R.id.buttonChannelUp:
                keyCode = Keycode.KEYCODE_CHANNEL_UP;
                break;
            case R.id.buttonVolUp:
                keyCode = Keycode.KEYCODE_VOLUME_UP;
                break;
            case R.id.buttonVolDown:
                keyCode = Keycode.KEYCODE_VOLUME_DOWN;
                break;
            case R.id.buttonMute:
                keyCode = Keycode.KEYCODE_MUTE;
                break;
            case R.id.buttonMenu:
                keyCode = Keycode.KEYCODE_MENU;
                break;
            case R.id.buttonDpadUp:
                keyCode = Keycode.KEYCODE_DPAD_UP;
                break;
            case R.id.buttonDpadDown:
                keyCode = Keycode.KEYCODE_DPAD_DOWN;
                break;
            case R.id.buttonDpadLeft:
                keyCode = Keycode.KEYCODE_DPAD_LEFT;
                break;
            case R.id.buttonDpadRight:
                keyCode = Keycode.KEYCODE_DPAD_RIGHT;
                break;
            case R.id.buttonDpadCenter:
                keyCode = Keycode.KEYCODE_DPAD_CENTER;
                break;
            case R.id.buttonHome:
                keyCode = Keycode.KEYCODE_HOME;
                break;
            case R.id.buttonPlay:
                keyCode = Keycode.KEYCODE_MEDIA_PLAY;
                break;
            case R.id.buttonPause:
                keyCode = Keycode.KEYCODE_MEDIA_PLAY_PAUSE;
                break;
            case R.id.buttonPower:
                String[] keyCodes = new String[3];
                keyCodes[0] = Keycode.KEYCODE_TV_POWER;
                keyCodes[1] = Keycode.KEYCODE_STB_POWER;
                keyCodes[2] = Keycode.KEYCODE_AVR_POWER;
                sendKeyCodeGroup(keyCodes);
                break;
            case R.id.buttonPowerTV:
                keyCode = Keycode.KEYCODE_TV_POWER;
                break;
            case R.id.buttonPowerSTB:
                keyCode = Keycode.KEYCODE_STB_POWER;
                break;
            case R.id.buttonPowerAVR:
                keyCode = Keycode.KEYCODE_AVR_POWER;
                break;
        }

        if (!TextUtils.isEmpty(keyCode)) {
            sendKeyCode(keyCode);
        }
    }

    private ControlListItem createItem(int position, int layoutId) {
        ControlListItem item = new ControlListItem();
        item.layoutReference = R.id.gallery;
        item.dataXmlLayout = layoutId;
        item.listItemId = position;
        item.listItemPosition = position;
        return item;
    }

    private void sendKeyCode(String keyCode) {
        Intent intent = new Intent();
        intent.setAction(An3Mote.ACTION_API_INVOKE);
        intent.putExtra(An3Mote.EXTRA_TYPE, An3Mote.TYPE_KEYCODE);
        intent.putExtra(An3Mote.EXTRA_KEYCODE, keyCode);
        mContext.sendBroadcast(intent);
    }

    private void sendKeyCodeGroup(String[] keyCodes) {
        Intent intent = new Intent();
        intent.setAction(An3Mote.ACTION_API_INVOKE);
        intent.putExtra(An3Mote.EXTRA_TYPE, An3Mote.TYPE_KEYCODE_GROUP);
        intent.putExtra(An3Mote.EXTRA_KEYCODE_GROUP, keyCodes);
        mContext.sendBroadcast(intent);
    }
}
