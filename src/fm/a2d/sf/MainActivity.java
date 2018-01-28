package fm.a2d.sf;

// GUI Activity:
// Copyright 2011-2015 Michael A. Reid. All rights reserved.

import android.app.Activity;
import android.app.Dialog;

import android.content.Intent;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

public class MainActivity extends Activity {

	private static int m_obinits = 0;
	private static int m_creates = 0;

	private Context mContext = null;
	public static com_api m_com_api = null;

	private gui_gap m_gui_gap = null;
	private static BroadcastReceiver m_gap_bcr = null;

	// Lifecycle:
	public MainActivity() { // empty constructor ?
		m_obinits++;
		com_uti.logd("m_obinits: " + m_obinits);

		mContext = this;
		com_uti.logd("mContext: " + mContext);
		com_uti.logd("m_com_api: " + m_com_api);
		com_uti.logd("m_gap_bcr: " + m_gap_bcr);
		com_uti.logd("m_gui_gap: " + m_gui_gap);

		// Disabled due to remaining main thread issues
		// strict_mode_set(true);

		if (m_com_api == null) { // If a receiver has not initialized yet...
			m_com_api = new com_api(this); // Instantiate Common API class
		}

		//    m_com_api.chass_plug_aud = com_uti.chass_plug_aud_get (mContext);  // Setup Audio Plugin
		//    m_com_api.chass_plug_tnr = com_uti.chass_plug_tnr_get (mContext);  // Setup Tuner Plugin
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		m_creates++;
		com_uti.logd("m_creates: " + m_creates);

		mContext = this;
		com_uti.logd("mContext: " + mContext);
		com_uti.logd("m_com_api: " + m_com_api);
		com_uti.logd("m_gap_bcr: " + m_gap_bcr);
		com_uti.logd("m_gui_gap: " + m_gui_gap);

		com_uti.logd("SpiritF " + com_uti.app_version_get(mContext));

		com_uti.logd("savedInstanceState: " + savedInstanceState);
/*
		if (m_com_api == null) { // If a receiver has not initialized yet...
			m_com_api = new com_api(mContext); // Instantiate Common API
			com_uti.logd("m_com_api: " + m_com_api);
		}
*/
		super.onCreate(savedInstanceState);

		if (m_com_api == null) { // If a receiver has not initialized yet...
			m_com_api = new com_api(mContext); // Instantiate Common API
			com_uti.logd("m_com_api: " + m_com_api);
		}

		// setVolumeControlStream() must be done from an Activity ? Then what stream is used from widget start ?
		setVolumeControlStream(svc_aud.audio_stream);

		gap_bcr_start(); // Start Common API Broadcast Receiver

//    m_com_api.chass_plug_aud = com_uti.chass_plug_aud_get (mContext);  // Setup Audio Plugin
//    m_com_api.chass_plug_tnr = com_uti.chass_plug_tnr_get (mContext);  // Setup Tuner Plugin

		gui_start(); // Start GUI
	}

	// Create        Start,Resume       Pause,Resume        Pause,Stop,Restart       Start,Resume

	// Launch:   Create      Start       Resume
	// Home:                                         Pause       Stop
	// Return:   Restart     Start       Resume
	// Back:                                         Pause       Stop        Destroy


	// Continuing methods in lifecycle order:
	@Override
	public void onStart() {
		super.onStart();
	}

	/**
	 * !! Resume can happen with the FM power off, so try not to do things needing power on
	 */
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	/**
	 * Restart comes between Stop and Start or when returning to the app
	 */
	@Override
	public void onRestart() {
		super.onRestart();
	}

	@Override
	public void onDestroy() {
		// One of these caused crashes; added try / catch below
		gap_bcr_stop();
		gui_stop();

		com_uti.logd("com_uti.num_daemon_get:              " + com_uti.num_daemon_get);
		com_uti.logd("com_uti.num_daemon_set:              " + com_uti.num_daemon_set);

		if (m_com_api != null) {
			com_uti.logd("m_com_api.num_key_set:             " + m_com_api.num_key_set);
			com_uti.logd("m_com_api.num_api_service_update:  " + m_com_api.num_api_service_update);
		}

		/*
		 * super.onDestroy dismisses any dialogs or cursors the activity was managing. If the logic in onDestroy has
		 * something to do with these things, then order may matter.
		 */

		super.onDestroy();
	}


	private void gui_stop() {
		try {
			if (m_gui_gap == null) {
				com_uti.loge("already stopped");
			} else if (!m_gui_gap.gap_state_set("Stop")) {
				com_uti.loge("gui_stop error"); // Stop UI. If error...
			} else {
				com_uti.logd("gui_stop OK");
			}
			m_gui_gap = null;
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void gui_start() {
		try {
			// Instantiate UI
			m_gui_gap = new MainGUI(mContext, m_com_api);
			if (m_gui_gap == null) {
				com_uti.loge("m_gui_gap == null");
			} else if (!m_gui_gap.gap_state_set("Start")) {                   // Start UI. If error...
				com_uti.loge("gui_start error");
				m_gui_gap = null;
			} else {
				com_uti.logd("gui_start OK");
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}


	private void gap_bcr_stop() {
		if (m_gap_bcr != null) {
			// Remove the State listener
			if (mContext != null) {
				try {
					// Caused by: java.lang.IllegalArgumentException: Receiver not registered: fm.a2d.sf.MainActivity$1@42549158
					mContext.unregisterReceiver(m_gap_bcr);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			m_gap_bcr = null;
		}
	}

	/**
	 * Common API Intent result & notification Broadcast Receiver
	 */
	private void gap_bcr_start() {
		if (m_gap_bcr == null) {
			m_gap_bcr = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					String action = intent.getAction();

					com_uti.logv("intent: " + intent + "  action: " + action);
					if (!action.equals(com_uti.api_result_id)) { // If not for us then done
						return;
					}

					if (m_com_api != null && m_gui_gap != null) {
						m_com_api.api_service_update(intent);
						m_gui_gap.gap_service_update(intent);
					}
				}
			};

			IntentFilter intent_filter = new IntentFilter();
			intent_filter.addAction(com_uti.api_result_id); // Can add more actions if needed
			intent_filter.addCategory(Intent.CATEGORY_DEFAULT);

			Intent last_sticky_state_intent = null;
			if (mContext != null)
				last_sticky_state_intent = mContext.registerReceiver(m_gap_bcr, intent_filter, null, null);    // No permission, no handler scheduler thread.
			if (last_sticky_state_intent != null) {
				com_uti.logd("bcast intent last_sticky_state_intent: " + last_sticky_state_intent);
				//m_gap_bcr.onReceive (mContext, last_sticky_state_intent);// Like a resend of last audio status update
			}
		}
	}


	// Dialog methods:

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {               // Create a dialog by calling specific *_dialog_create function    ; Triggered by showDialog (int id);
		com_uti.logd("id: " + id + "  args: " + args);
		Dialog dlg_ret = null;
		if (m_gui_gap != null)
			dlg_ret = m_gui_gap.gap_dialog_create(id, args);
		com_uti.logd("dlg_ret: " + dlg_ret);
		return (dlg_ret);
	}


	public void gap_gui_clicked(View v) {                                // See res/layout/gui_pg2_layout.xml
		m_gui_gap.gap_gui_clicked(v);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return m_gui_gap.gap_menu_create(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	/**
	 * When "Settings" is selected, after pressing Menu key
	 * @param item clicked item
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		m_gui_gap.gap_menu_select(item.getItemId());
		return super.onOptionsItemSelected(item);
	}

}
