package fm.a2d.sf;

import android.app.Activity;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.view.View.OnLongClickListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
    
import android.media.AudioManager;
import android.media.audiofx.AudioEffect;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.os.Bundle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

@SuppressWarnings({"DanglingJavadoc", "UnusedAssignment", "unused", "deprecation"})
public class MainGUI implements gui_gap {//, gui_dlg.gui_dlg_lstnr {

	private static int m_obinits = 1;

	private Activity mActivity = null;
	private Context mContext = null;
	private com_api m_com_api = null;


	// User Interface:
	private Animation m_ani_button = null;

	// Text:
	private TextView m_tv_rssi = null;
	private TextView m_tv_svc_count = null;
	private TextView m_tv_svc_phase = null;
	private TextView m_tv_svc_cdown = null;
	private TextView m_tv_pilot = null;
	private TextView mtvBand = null;
	private TextView m_tv_freq = null;

	// RDS data:
	private TextView m_tv_picl = null;
	private TextView m_tv_ps = null;
	private TextView m_tv_ptyn = null;
	private TextView m_tv_rt = null;

	// ImageView Buttons:
	private ImageView m_iv_seekup = null;
	private ImageView m_iv_seekdn = null;

	private ImageView m_iv_prev = null;
	private ImageView m_iv_next = null;

	private ImageView mivPlayPause = null;
	private ImageView m_iv_stop = null;
	private ImageView m_iv_pause = null;
	private ImageView mIvOutput = null; // ImageView for Speaker/Headset toggle
	private ImageView m_iv_volume = null;
	private ImageView mivRecord = null;
	private ImageView m_iv_menu = null;
	private ImageView m_iv_pwr = null;

	// Radio Group/Buttons:
	private RadioGroup m_rg_band = null;
	;
	private RadioButton rb_band_eu = null;
	private RadioButton rb_band_us = null;
	private RadioButton rb_band_uu = null;

	// Checkboxes:
	private CheckBox mcbSpeaker = null;


	// Presets: 16 = com_api.chass_preset_max

	private int m_presets_curr = 0;
	private ImageButton[] mibPresets = {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null};   // 16 Preset Image Buttons
	private TextView[] mtvPresets = {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null};   // 16 Preset Text Views
	private String[] m_preset_freq = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};   //  Frequencies
	private String[] m_preset_name = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};   //  Names


	private int pixel_width = 480;
	private int pixel_height = 800;
	private float pixel_density = 1.5f;


	// Dial:
	private Handler delay_dial_handler = null;
	private Runnable delay_dial_runnable = null;

	private DialView mDial = null;
	private long last_rotate_time = 0;

	private double freq_at_210 = 85200;
	private double freqPercentFactor = 251.5;

	private int lastFrequency = -1;

	// Color:
	private int lite_clr = Color.WHITE;
	private int dark_clr = Color.GRAY;
	private int blue_clr = Color.BLUE;


	private Dialog mDialogDaemonStart = null;
	private Dialog daemon_dialog = null;

	private String last_rt = "";
	private int lastAudioSessidInt = 0;


	// Constructor
	public MainGUI(Context c, com_api the_com_api) {
		com_uti.logd("m_obinits: " + m_obinits++);

		mContext = c;
		mActivity = (Activity) c;
		m_com_api = the_com_api;
	}

/*
  private gui_dlg start_dlg_frag = null;
  private gui_dlg stop_dlg_frag  = null;
  private boolean start_gui_dlg_active = false;
  private boolean stop_gui_dlg_active = false;

  private void start_gui_dlg_show (boolean start) {
    //start_dlg_frag.dismiss ();    // Dismiss previous

    // DialogFragment.show() will take care of adding the fragment in a transaction.  We also want to remove any currently showing dialog, so make our own transaction and take care of that here.
    FragmentTransaction ft = mActivity.getFragmentManager ().beginTransaction ();
    Fragment prev = mActivity.getFragmentManager ().findFragmentByTag ("start_stop_dialog");
    if (prev != null) {
        ft.remove (prev);
    }
    ft.addToBackStack (null);

    if (start) {
      start_dlg_frag = gui_dlg.init (R.drawable.img_icon_128, m_com_api.chass_phase, m_com_api.chass_phtmo, null, null, mContext.getString (android.R.string.cancel));//"", mContext.getString (android.R.string.ok), "", null);//"");
      start_dlg_frag.setgui_dlg_lstnr (this);//mActivity);
      start_gui_dlg_active = true;
      start_dlg_frag.show (mActivity.getFragmentManager (), "start_stop_dialog");
      start_dlg_frag.show (ft, "start_stop_dialog");
    }
    else
      start_gui_dlg_active = false;
  }

  private void stop_gui_dlg_show (boolean start) {
    // DialogFragment.show() will take care of adding the fragment in a transaction.  We also want to remove any currently showing dialog, so make our own transaction and take care of that here.
    FragmentTransaction ft = mActivity.getFragmentManager ().beginTransaction ();
    Fragment prev = mActivity.getFragmentManager ().findFragmentByTag ("start_stop_dialog");
    if (prev != null) {
        ft.remove (prev);
    }
    ft.addToBackStack (null);

    if (start) {
      stop_dlg_frag = gui_dlg.init (android.R.drawable.stat_sys_headset, "Stop", null, null, null, null);//"", mContext.getString (android.R.string.ok), "", null);//"");
      stop_dlg_frag.setgui_dlg_lstnr (this);//mActivity);
      stop_gui_dlg_active = true;
      //stop_dlg_frag.show (mActivity.getFragmentManager (), "start_stop_dialog");
      stop_dlg_frag.show (ft, "start_stop_dialog");
    }
    else
      stop_gui_dlg_active = false;
  }
*/

	// Lifecycle API

	public boolean gap_state_set(String state) {
		boolean ret = false;
		if (state.equals("Start"))
			ret = gui_start();
		else if (state.equals("Stop"))
			ret = gui_stop();
		return (ret);
	}

	private boolean gui_stop() {
		isGUIInited = false;
		return true;
	}

	private boolean isGUIInited = false;

	private boolean gui_start() {

		isGUIInited = false;

		// !! Hack for s2d comms to allow network activity on UI thread
		com_uti.strict_mode_set(false);

		DisplayMetrics dm = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		pixel_width = dm.widthPixels;
		pixel_height = dm.heightPixels;
		pixel_density = mContext.getResources().getDisplayMetrics().density;
		com_uti.logd("pixel_width: " + pixel_width + "  pixel_height: " + pixel_height + "  pixel_density: " + pixel_density);

		mActivity.requestWindowFeature(Window.FEATURE_NO_TITLE);            // No title to save screen space
		mActivity.setContentView(R.layout.gui_gui_layout);                   // Main Layout


/*  Programmatic harder than XML (?? Should do before setContentView !! ??

//    LinearLayout main_linear_layout =  (LinearLayout) mActivity.findViewById (R.id.main_hll);
    LinearLayout                            gui_pg1_layout          =  (LinearLayout) mActivity.findViewById (R.id.gui_pg1_layout);
    //FrameLayout                             new_frame_layout        = (FrameLayout) mActivity.findViewById (R.id.new_fl);//new FrameLayout (mContext);
    FrameLayout                             new_frame_layout        = new FrameLayout (mContext);
    FrameLayout.LayoutParams new_frame_layout_params = new android.widget.FrameLayout.LayoutParams ((int) (pixel_width / pixel_density), ViewGroup.LayoutParams.MATCH_PARENT);
    //com_uti.loge ("gui_pg1_layout: " + gui_pg1_layout + "  new_frame_layout_params: " + new_frame_layout_params);
    new_frame_layout.addView (gui_pg1_layout, new_frame_layout_params);
//  To:     new_frame_layout    FrameLayout  View       with new_frame_layout_params FrameLayout.LayoutParams
//  add:    gui_pg1_layout   LinearLayout View

    LinearLayout gui_pg2_layout =  (LinearLayout) mActivity.findViewById (R.id.gui_pg2_layout);
    FrameLayout                 old_frame_layout = (FrameLayout) mActivity.findViewById (R.id.old_fl);//new FrameLayout (mContext);
    FrameLayout.LayoutParams    old_frame_layout_params = new android.widget.FrameLayout.LayoutParams ((int) (pixel_width / pixel_density), ViewGroup.LayoutParams.MATCH_PARENT);
    old_frame_layout.addView (gui_pg2_layout, old_frame_layout_params);

//    main_linear_layout.addView (gui_pg1_layout);
//    main_linear_layout.addView (gui_pg2_layout);
*/

		LinearLayout.LayoutParams frame_layout_params = new android.widget.LinearLayout.LayoutParams(pixel_width, ViewGroup.LayoutParams.MATCH_PARENT);

		FrameLayout new_fl_view = (FrameLayout) mActivity.findViewById(R.id.new_fl);
		new_fl_view.setLayoutParams(frame_layout_params);

		FrameLayout old_fl_view = (FrameLayout) mActivity.findViewById(R.id.old_fl);
		old_fl_view.setLayoutParams(frame_layout_params);

		initDial(); // Initialize frequency disl

		// Set button animation
		m_ani_button = AnimationUtils.loadAnimation(mContext, R.anim.ani_button);

		mtvBand = (TextView) mActivity.findViewById(R.id.tv_band);
		m_tv_pilot = (TextView) mActivity.findViewById(R.id.tv_pilot);
		m_tv_rssi = (TextView) mActivity.findViewById(R.id.tv_rssi);

		m_tv_svc_count = (TextView) mActivity.findViewById(R.id.tv_svc_count);
		m_tv_svc_phase = (TextView) mActivity.findViewById(R.id.tv_svc_phase);
		m_tv_svc_cdown = (TextView) mActivity.findViewById(R.id.tv_svc_cdown);

		m_tv_picl = (TextView) mActivity.findViewById(R.id.tv_picl);
		m_tv_ps = (TextView) mActivity.findViewById(R.id.tv_ps);
		m_tv_ptyn = (TextView) mActivity.findViewById(R.id.tv_ptyn);
		m_tv_rt = (TextView) mActivity.findViewById(R.id.tv_rt);

		m_iv_seekdn = (ImageView) mActivity.findViewById(R.id.iv_seekdn);
		m_iv_seekdn.setOnClickListener(short_click_lstnr);

		m_iv_seekup = (ImageView) mActivity.findViewById(R.id.iv_seekup);
		m_iv_seekup.setOnClickListener(short_click_lstnr);

		m_iv_prev = (ImageView) mActivity.findViewById(R.id.iv_prev);
		m_iv_prev.setOnClickListener(short_click_lstnr);
		m_iv_prev.setId(R.id.iv_prev);

		m_iv_next = (ImageView) mActivity.findViewById(R.id.iv_next);
		m_iv_next.setOnClickListener(short_click_lstnr);
		m_iv_next.setId(R.id.iv_next);

		m_tv_freq = (TextView) mActivity.findViewById(R.id.tv_freq);
		m_tv_freq.setOnClickListener(short_click_lstnr);

		mivPlayPause = (ImageView) mActivity.findViewById(R.id.iv_paupla);
		mivPlayPause.setOnClickListener(short_click_lstnr);
		mivPlayPause.setId(R.id.iv_paupla);

		m_iv_stop = (ImageView) mActivity.findViewById(R.id.iv_stop);
		m_iv_stop.setOnClickListener(short_click_lstnr);
		m_iv_stop.setId(R.id.iv_stop);

		m_iv_pause = (ImageView) mActivity.findViewById(R.id.iv_pause);
		m_iv_pause.setOnClickListener(short_click_lstnr);
		m_iv_pause.setId(R.id.iv_pause);

		mIvOutput = (ImageView) mActivity.findViewById(R.id.iv_output);
		mIvOutput.setOnClickListener(short_click_lstnr);
		mIvOutput.setId(R.id.iv_output);

		m_iv_volume = (ImageView) mActivity.findViewById(R.id.iv_volume);
		m_iv_volume.setOnClickListener(short_click_lstnr);
		m_iv_volume.setId(R.id.iv_volume);

		mivRecord = (ImageView) mActivity.findViewById(R.id.iv_record);
		mivRecord.setOnClickListener(short_click_lstnr);
		//mivRecord.setOnLongClickListener (long_click_lstnr);
		mivRecord.setId(R.id.iv_record);

		m_iv_menu = (ImageView) mActivity.findViewById(R.id.iv_menu);
		m_iv_menu.setOnClickListener(short_click_lstnr);
		m_iv_menu.setOnLongClickListener(long_click_lstnr);
		m_iv_menu.setId(R.id.iv_menu);

		rb_band_eu = (RadioButton) mActivity.findViewById(R.id.rb_band_eu);
		rb_band_us = (RadioButton) mActivity.findViewById(R.id.rb_band_us);
		rb_band_uu = (RadioButton) mActivity.findViewById(R.id.rb_band_uu);

		mcbSpeaker = (CheckBox) mActivity.findViewById(R.id.cb_speaker);

		try {
			lite_clr = Color.parseColor("#ffffffff"); // lite like PS
			dark_clr = Color.parseColor("#ffa3a3a3"); // grey like RT
			blue_clr = Color.parseColor("#ff32b5e5"); // ICS Blue
		} catch (Throwable e) {
			e.printStackTrace();
		}
		;
		m_tv_rt.setTextColor(lite_clr);
		m_tv_ps.setTextColor(lite_clr);

		setupPresets(lite_clr);

		gui_pwr_update(false);

		long curr_time = com_uti.utc_ms_get();
		long gui_start_first = com_uti.getLong(com_uti.prefs_get(mContext, "gui_start_first", ""));
		if (gui_start_first <= 0L) {
			com_uti.prefs_set(mContext, "gui_start_first", "" + curr_time);
		}

		// Set preferences defaults:
/* ?? Don't need ??
    com_uti.def_set (mContext, "tuner_band");
    com_uti.def_set (mContext, "tuner_freq");
    com_uti.def_set (mContext, "tuner_stereo");
    com_uti.def_set (mContext, "tuner_rds_state");
    com_uti.def_set (mContext, "tuner_rds_af_state");

    com_uti.def_set (mContext, "audio_output");
    com_uti.def_set (mContext, "audio_stereo");
    com_uti.def_set (mContext, "audio_record_directory");
    com_uti.def_set (mContext, "audio_record_filename");

    com_uti.def_set (mContext, "audio_digital_amp");
    com_uti.def_set (mContext, "audio_samplerate");
    com_uti.def_set (mContext, "audio_buffersize");
    com_uti.def_set (mContext, "audio_mode");
    com_uti.def_set (mContext, "audio_pseudo_source");

    com_uti.def_set (mContext, "gui_display_name");
    com_uti.def_set (mContext, "gui_display_type");

    com_uti.def_set (mContext, "service_action_media_pauseplay");
    com_uti.def_set (mContext, "service_action_media_previous");
    com_uti.def_set (mContext, "service_action_media_next");

    com_uti.def_set (mContext, "service_update_notification");
    com_uti.def_set (mContext, "service_notifremote_title");
    com_uti.def_set (mContext, "service_notifremote_text");

    com_uti.def_set (mContext, "service_update_gui");

    com_uti.def_set (mContext, "service_update_remote");

    com_uti.def_set (mContext, "chass_plug_tnr");
    com_uti.def_set (mContext, "chass_plug_aud");

    com_uti.def_set (mContext, "debug_debug");
*/

		int gui_start_count = com_uti.prefs_get(mContext, "gui_start_count", 0);
		gui_start_count++;
		com_uti.prefs_set(mContext, "gui_start_count", gui_start_count);

		m_rg_band = (RadioGroup) mActivity.findViewById(R.id.rg_band);

		// !! tuner_band_set() is now the first thing that starts svc_svc, if not already started

		m_com_api.chass_plug_aud = com_uti.chass_plug_aud_get(mContext);  // Setup Audio Plugin
		m_com_api.chass_plug_tnr = com_uti.chass_plug_tnr_get(mContext);  // Setup Tuner Plugin

		if (gui_start_count <= 1) {// If known device and first 1 runs...
			String cc = com_uti.getCountry(mContext).toUpperCase();
			if (cc.equals("US") || cc.equals("CA") || cc.equals("MX")) {   // If USA, Canada or Mexico
				com_uti.logd("Setting band US");
				tuner_band_set("US");                                          // Band = US
			} else {
				com_uti.logd("Setting band EU");
				tuner_band_set("EU");                                          // Else Band = EU
			}
		}

		// If daemon not running
		if (!com_uti.isFileExists("/dev/s2d_running")) {
			mActivity.showDialog(DAEMON_START_DIALOG);                       // Show the Start dialog
		}

		String band = com_uti.prefs_get(mContext, "tuner_band", "EU");
		//if (! m_com_api.chass_plug_aud.equals ("UNK"))
		tuner_band_set(band);
		switch (m_com_api.tuner_band) {
			case "EU":
				rb_band_eu.setChecked(true);
				rb_band_us.setChecked(false);
				rb_band_uu.setChecked(false);
				break;
			case "US":
				rb_band_eu.setChecked(false);
				rb_band_us.setChecked(true);
				rb_band_uu.setChecked(false);
				break;
			case "UU":
				rb_band_eu.setChecked(false);
				rb_band_us.setChecked(false);
				rb_band_uu.setChecked(true);
				break;
		}

		loadPreferences();

		// If known device and tuner not started...
		if (!m_com_api.chass_plug_aud.equals("UNK") && !m_com_api.tuner_state.equals("Start")) {
			// Start audio service (which starts tuner (and daemon) first, if not already started)
			m_com_api.key_set("audio_state", "Start");
		}

		isGUIInited = true;

		return true;
	}


	private void initDial() {

		// Dial Relative Layout:
		RelativeLayout dialWrap = (RelativeLayout) mActivity.findViewById(R.id.freq_dial);

		LayoutParams lpDial = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); // WRAP_CONTENT
		lpDial.addRule(RelativeLayout.CENTER_IN_PARENT);

		int dial_size = (pixel_width * 7) / 8;
		mDial = new DialView(mContext, R.drawable.freq_dial_needle, -1, dial_size, dial_size); // Get dial instance/RelativeLayout view

		dialWrap.addView(mDial, lpDial);


		// Dial internal Power Relative Layout:
		LayoutParams lp_power = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp_power.addRule(RelativeLayout.CENTER_IN_PARENT);

		m_iv_pwr = new ImageView(mContext);
		m_iv_pwr.setImageResource(R.drawable.dial_power_off);
		dialWrap.addView(m_iv_pwr, lp_power);

		// Setup listener for onStateChanged() and onAngleChanged()
		mDial.setOnDialChangedListener(new DialView.OnDialChangedListener() {

			public boolean prev_go() {
				if (!m_com_api.tuner_state.equals("Start")) {
					com_uti.logd("via gui_dia abort tuner_state: " + m_com_api.tuner_state);
					return (false);                                               // Not Consumed
				}
				ani(m_iv_prev);
				m_com_api.key_set("tuner_freq", "Down");
				return (true);                                                  // Consumed
			}

			public boolean next_go() {
				if (!m_com_api.tuner_state.equals("Start")) {
					com_uti.logd("via gui_dia abort tuner_state: " + m_com_api.tuner_state);
					return (false);                                               // Not Consumed
				}
				ani(m_iv_next);
				m_com_api.key_set("tuner_freq", "Up");
				return (true);                                                  // Consumed
			}

			public boolean onStateChanged() {
				com_uti.logd("via gui_dia m_com_api.audio_state: " + m_com_api.audio_state);
				if (m_com_api.audio_state.equals("Start"))
					m_com_api.key_set("tuner_state", "Stop");
				else
					m_com_api.key_set("audio_state", "Start");
				return (true);                                                  // Consumed
			}

			public boolean freq_go() {
				com_uti.logd("via gui_dia");
				if (!m_com_api.tuner_state.equals("Start")) {
					com_uti.logd("via gui_dia abort tuner_state: " + m_com_api.tuner_state);
					return (false);                                               // Not Consumed
				}
				if (lastDialFrequency < com_uti.bandFrequencyLower || lastDialFrequency > com_uti.bandFrequencyHigher)
					return (false);                                               // Not Consumed
				m_com_api.key_set("tuner_freq", "" + lastDialFrequency);
				return (true);                                                  // Consumed
			}

			private int lastDialFrequency = 0;

			public boolean onAngleChanged(double angle) {
				if (!m_com_api.tuner_state.equals("Start")) {
					com_uti.logd("via gui_dia abort tuner_state: " + m_com_api.tuner_state);
					return (false);                                               // Not Consumed
				}
				long curr_time = com_uti.tmr_ms_get();
				int freq = getFrequency(angle);
				com_uti.logd("via gui_dia angle: " + angle + "  freq: " + freq);
				freq += 25;
				freq /= 50;
				freq *= 50;
				if (freq < com_uti.bandFrequencyLower || freq > com_uti.bandFrequencyHigher)
					return (false);                                               // Not Consumed
				freq = com_uti.tnru_freq_enforce(freq);
				com_uti.logd("via gui_dia freq: " + freq + "  curr_time: " + curr_time + "  last_rotate_time: " + last_rotate_time);
				setDialFrequency(freq);   // !! Better to set fast !!
				lastDialFrequency = freq;

				if (delay_dial_handler != null) {
					if (delay_dial_runnable != null)
						delay_dial_handler.removeCallbacks(delay_dial_runnable);
				} else
					delay_dial_handler = new Handler();

				delay_dial_runnable = new Runnable() {
					public void run() {
						m_com_api.key_set("tuner_freq", "" + lastDialFrequency);
					}
				};
				delay_dial_handler.postDelayed(delay_dial_runnable, 50);

				return true; // Consumed
			}
		});
	}

	private int getFrequency(double angle) {
		return (int) (freqPercentFactor * ((angle + 150) / 3) + freq_at_210);
	}


	private void setDialFrequency(int freq) {
		// Optimize
		if (lastFrequency == freq)    {
			return;
		}
		if (mDial == null) {
			return;
		}
		lastFrequency = freq;
		double percent = (freq - freq_at_210) / freqPercentFactor;
		double angle = (percent * 3) - 150;
		mDial.setAngleDial(angle);
	}

	// 16 Max Preset Buttons hardcoded
	private void setupPresets(int color) {

		// Textviews:
		mtvPresets[0] = (TextView) mActivity.findViewById(R.id.tv_preset_0);
		mtvPresets[1] = (TextView) mActivity.findViewById(R.id.tv_preset_1);
		mtvPresets[2] = (TextView) mActivity.findViewById(R.id.tv_preset_2);
		mtvPresets[3] = (TextView) mActivity.findViewById(R.id.tv_preset_3);
		mtvPresets[4] = (TextView) mActivity.findViewById(R.id.tv_preset_4);
		mtvPresets[5] = (TextView) mActivity.findViewById(R.id.tv_preset_5);
		mtvPresets[6] = (TextView) mActivity.findViewById(R.id.tv_preset_6);
		mtvPresets[7] = (TextView) mActivity.findViewById(R.id.tv_preset_7);
		mtvPresets[8] = (TextView) mActivity.findViewById(R.id.tv_preset_8);
		mtvPresets[9] = (TextView) mActivity.findViewById(R.id.tv_preset_9);
		mtvPresets[10] = (TextView) mActivity.findViewById(R.id.tv_preset_10);
		mtvPresets[11] = (TextView) mActivity.findViewById(R.id.tv_preset_11);
		mtvPresets[12] = (TextView) mActivity.findViewById(R.id.tv_preset_12);
		mtvPresets[13] = (TextView) mActivity.findViewById(R.id.tv_preset_13);
		mtvPresets[14] = (TextView) mActivity.findViewById(R.id.tv_preset_14);
		mtvPresets[15] = (TextView) mActivity.findViewById(R.id.tv_preset_15);

		// Imagebuttons:
		mibPresets[0] = (ImageButton) mActivity.findViewById(R.id.ib_preset_0);
		mibPresets[1] = (ImageButton) mActivity.findViewById(R.id.ib_preset_1);
		mibPresets[2] = (ImageButton) mActivity.findViewById(R.id.ib_preset_2);
		mibPresets[3] = (ImageButton) mActivity.findViewById(R.id.ib_preset_3);
		mibPresets[4] = (ImageButton) mActivity.findViewById(R.id.ib_preset_4);
		mibPresets[5] = (ImageButton) mActivity.findViewById(R.id.ib_preset_5);
		mibPresets[6] = (ImageButton) mActivity.findViewById(R.id.ib_preset_6);
		mibPresets[7] = (ImageButton) mActivity.findViewById(R.id.ib_preset_7);
		mibPresets[8] = (ImageButton) mActivity.findViewById(R.id.ib_preset_8);
		mibPresets[9] = (ImageButton) mActivity.findViewById(R.id.ib_preset_9);
		mibPresets[10] = (ImageButton) mActivity.findViewById(R.id.ib_preset_10);
		mibPresets[11] = (ImageButton) mActivity.findViewById(R.id.ib_preset_11);
		mibPresets[12] = (ImageButton) mActivity.findViewById(R.id.ib_preset_12);
		mibPresets[13] = (ImageButton) mActivity.findViewById(R.id.ib_preset_13);
		mibPresets[14] = (ImageButton) mActivity.findViewById(R.id.ib_preset_14);
		mibPresets[15] = (ImageButton) mActivity.findViewById(R.id.ib_preset_15);

		for (TextView tv : mtvPresets) { // For all presets...
			tv.setOnClickListener(preset_select_lstnr); // Set click listener
			tv.setOnLongClickListener(preset_change_lstnr); // Set long click listener
		}

		for (int idx = 0; idx < com_api.chass_preset_max; idx++) { // For all presets...
			String name = com_uti.prefs_get(mContext, "chass_preset_name_" + idx, "");
			String freq = com_uti.prefs_get(mContext, "chass_preset_freq_" + idx, "");
			if (freq != null && !freq.equals("")) { // If non empty frequency (if setting exists)
				m_presets_curr = idx + 1; // Update number of presets
				m_preset_freq[idx] = freq;
				m_preset_name[idx] = name;

				mtvPresets[idx].setText(name != null ? name : String.valueOf(((double) com_uti.getInt(freq)) / 1000));
				mibPresets[idx].setImageResource(R.drawable.transparent);
				//mibPresets [idx].setEnabled (false);
			} else {
				//m_presets_curr = idx + 1;                                     // DISABLED: Update number of presets (For blank ones now too)
				m_preset_freq[idx] = "";
				m_preset_name[idx] = "";
				//mibPresets [idx].setImageResource (R.drawable.btn_preset);
				//mibPresets [idx].setEnabled (true);
			}
			mtvPresets[idx].setTextColor(color);
		}

		com_uti.logd("m_presets_curr: " + m_presets_curr);
	}


	private void resetAllTextView() {
		m_tv_pilot.setText("");
		mtvBand.setText("");

		m_tv_rssi.setText("");
		m_tv_ps.setText("");
		m_tv_picl.setText("");
		m_tv_ptyn.setText("");
		m_tv_rt.setText("");
		m_tv_rt.setSelected(true); // Need setSelected() for marquis
	}


	private void startEqualizer() {
		int audio_sessid_int = com_uti.getInt(m_com_api.audioSessionId);
		com_uti.logd("audioSessionId: " + m_com_api.audioSessionId + "  audio_sessid_int: " + audio_sessid_int);
		try {
			// Not every phone/ROM has EQ installed, especially stock ROMs
			Intent i = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);

			/**
			 * The EXTRA_CONTENT_TYPE extra will help the control panel application customize both the UI layout and the default audio effect settings if none are already stored for the calling application
			 */
			i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, audio_sessid_int);
			mActivity.startActivityForResult(i, 0);
		} catch (Throwable e) {
			com_uti.loge("exception");
		}
	}


	// Dialog methods:
	private static final int DAEMON_START_DIALOG = 1; // Daemon start
	private static final int DAEMON_ERROR_DIALOG = 2; // Daemon error
	private static final int TUNER_API_ERROR_DIALOG = 3; // Tuner API error
	private static final int TUNER_ERROR_DIALOG = 4; // Tuner error
	private static final int FREQ_SET_DIALOG = 5; // Frequency set
	private static final int GUI_MENU_DIALOG = 6; // Menu
	private static final int GUI_ABOUT_DIALOG = 7; // About
	private static final int GUI_TEST_DIALOG = 8; // Test
	private static final int GUI_DEBUG_DIALOG = 9; // Debug
	private static final int GUI_SHIM_DIALOG = 10; // Shim
	private static final int GUI_ACDB_DIALOG = 11; // ACDB Fix
	private static final int PRESET_CHANGE_DIALOG = 12; // Preset functions
	private static final int PRESET_RENAME_DIALOG = 14; // Preset rename

	// Create a dialog by calling specific *_dialog_create function
	// Triggered by showDialog (int id);
	public Dialog createDialog(int id, Bundle args) {
		//public DialogFragment createDialog (int id, Bundle args) {
		com_uti.logd("id: " + id + "  args: " + args);

		// DialogFragment ret = null;
		Dialog ret = null;
		switch (id) {
			case DAEMON_START_DIALOG:
				ret = createDialogStartDaemon(id);
				break;
			case DAEMON_ERROR_DIALOG:
				ret = createDialogErrorDaemon(id);
				break;
			case TUNER_API_ERROR_DIALOG:
				ret = createDialogTunerApiError(id);
				break;
			case TUNER_ERROR_DIALOG:
				ret = createDialogTunerError(id);
				break;
			case GUI_MENU_DIALOG:
				ret = createDialogMenu(id);
				break;
			case GUI_ABOUT_DIALOG:
				ret = createDialogAbout(id);
				break;
			case GUI_TEST_DIALOG:
				ret = createDialogTest(id);
				break;
			case GUI_DEBUG_DIALOG:
				ret = createDialogDebug(id);
				break;
			case GUI_SHIM_DIALOG:
				ret = createDialogShim(id);
				break;
			case GUI_ACDB_DIALOG:
				ret = createDialogAcdb(id);
				break;
			case FREQ_SET_DIALOG:
				ret = createDialogSetFrequency(id);
				break;
			case PRESET_CHANGE_DIALOG:
				ret = createDialogPresetChange(id);
				break;
			case PRESET_RENAME_DIALOG:
				ret = createDialogPresetRename(id);
				break;
		}
		//com_uti.logd ("dialog: " + ret);
		return (ret);
	}


	boolean free = false;

	private Dialog createDialogTest(final int id) {
		com_uti.logd("id: " + id);
		AlertDialog.Builder dlg_bldr = new AlertDialog.Builder(mContext);
		return (dlg_bldr.create());
	}

	private Dialog createDialogDebug(final int id) {
		com_uti.logd("id: " + id);

		AlertDialog.Builder dlg_bldr = new AlertDialog.Builder(mContext);
		dlg_bldr.setTitle("Debug");
		ArrayList<String> array_list = new ArrayList<>();
		array_list.add("SHIM");
		array_list.add("ACDB");
		array_list.add("Log");
		array_list.add("Digital");
		array_list.add("Analog");
		//array_list.add ("SELinux On");
		//array_list.add ("SELinux Off");

		dlg_bldr.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
			}
		});

		String[] items = new String[array_list.size()];
		array_list.toArray(items);

		dlg_bldr.setItems(items, new OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				com_uti.logd("item: " + item);
				gui_debug_menu_select(item);
			}

		});

		return (dlg_bldr.create());
	}

	public void gui_debug_menu_select(int itemId) {
		// When "Settings" is selected, after pressing Menu key
		com_uti.logd("itemId: " + itemId);
		switch (itemId) {
			case 0:
				mActivity.showDialog(GUI_SHIM_DIALOG);
				return;
			case 1:
				mActivity.showDialog(GUI_ACDB_DIALOG);
				return;
			case 3:
				m_com_api.key_set("audio_mode", "Digital");
				Toast.makeText(mContext, "audio_mode = Digital", Toast.LENGTH_LONG).show();
				return;
			case 4:
				m_com_api.key_set("audio_mode", "Analog");
				Toast.makeText(mContext, "audio_mode = Analog", Toast.LENGTH_LONG).show();
				/*
      case 5:
        ret = com_uti.runCommand ("setenforce 1", true);
        Toast.makeText (mContext, "setenforce 1 ret: " + ret, Toast.LENGTH_LONG).show ();
        return (true);
      case 6:
        Toast.makeText (mContext, "DISABLING SELINUX IS BAD FOR SECURITY !!!! ; USE FOR TESTING ONLY !!", Toast.LENGTH_LONG).show ();
        ret = com_uti.runCommand ("setenforce 0", true);
        Toast.makeText (mContext, "setenforce 0 ret: " + ret, Toast.LENGTH_LONG).show ();
        return (true);
*/
		}
	}

	private Dialog createDialogAbout(final int id) {
		com_uti.logd("id: " + id);

		AlertDialog.Builder dlg_bldr = new AlertDialog.Builder(mContext);

		dlg_bldr.setTitle("SpiritF " + com_uti.getApplicationVersion(mContext));

		String menu_msg = "Select Google Play or Cancel";
		dlg_bldr.setMessage(menu_msg);

		dlg_bldr.setNegativeButton("Cancel", new OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});

		dlg_bldr.setNeutralButton("Debug", new OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mActivity.showDialog(GUI_DEBUG_DIALOG);                        // Show the Debug dialog
			}
		});

		return (dlg_bldr.create());
	}

	private Dialog createDialogShim(final int id) {
/*  Code from svc_svc:
      boolean unfriendly_auto_install_and_reboot = false;
      if (unfriendly_auto_install_and_reboot) {
        if (! com_uti.isShimFilesOperational ()) {                    // If shim files not operational...
          if (com_uti.isBluetoothEnabled ()) {                                        // July 31, 2014: only install shim if BT is on
            com_uti.bt_set (false, true);                                 // Bluetooth off, and wait for off

            com_uti.rfkill_bt_wait (false);     // Wait for BT off
            //com_uti.logd ("Start 4 second delay after BT Off");
            //com_uti.sleep (4000);                                      // Extra 4 second delay to ensure BT is off !!
            //com_uti.logd ("End 4 second delay after BT Off");

            com_uti.shimInstall ();                                      // Install shim

            com_uti.bt_set (true, true);                                  // Bluetooth on, and wait for on  (Need to set BT on so reboot has it on.)

            //Toast.makeText (mContext, "WARM RESTART PENDING FOR SHIM INSTALL !!", Toast.LENGTH_LONG).show ();  java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()
            // Don't need a delay before reboot because BT is on enough to stay on after reboot
            //com_uti.sys_WAS_run ("kill `pidof system_server`", true);

            com_uti.runCommand ("reboot now", true);                         // M7 GPE requires reboot

            fresh_shim_install = true;
          }
        }
      }//if (unfriendly_auto_install_and_reboot) {
*/
		com_uti.logd("id: " + id);

		AlertDialog.Builder dlg_bldr = new AlertDialog.Builder(mContext);

		dlg_bldr.setTitle("Bluetooth SHIM");

		String menu_msg = "";
		menu_msg += "BT SHIM is only needed when BT is on for HTC One M7, LG G2, Xperia Z2/Z3.\n\n";

		menu_msg += "LG G2 can have audio problems unless SpiritF run once with BT off.\n\n";

		menu_msg += "Select: Install BT Shim to install. Reboot after.\n";
		menu_msg += "Select: Remove BT Shim to remove.";
		dlg_bldr.setMessage(menu_msg);

		dlg_bldr.setNegativeButton("Cancel", new OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});

		dlg_bldr.setNeutralButton("Install", new OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (com_uti.isShimFilesOperational()) {
					/*boolean reinstall_destroys_original = true;
					if (reinstall_destroys_original) {*/
						Toast.makeText(mContext, "Shim file already installed. Can't reinstall...", Toast.LENGTH_LONG).show();
					/*} else {
						Toast.makeText(mContext, "Shim file already installed. Reinstalling...", Toast.LENGTH_LONG).show();
						com_uti.shimInstall();
					}*/
				} else {
					Toast.makeText(mContext, "Shim file installing...", Toast.LENGTH_LONG).show();
					com_uti.shimInstall();
				}
			}
		});

		dlg_bldr.setPositiveButton("Remove", new OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (com_uti.isShimFilesOperational()) {
					Toast.makeText(mContext, "Shim file installed. Removing...", Toast.LENGTH_LONG).show();
					com_uti.shimRemove();
				} else
					Toast.makeText(mContext, "Shim file not installed !!!", Toast.LENGTH_LONG).show();
			}
		});

		return (dlg_bldr.create());
	}

	private Dialog createDialogAcdb(final int id) {
		com_uti.logd("id: " + id);

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		builder.setTitle("ACDB Fix");

		String menuMessage = "ACDB Fix is only needed when sound is bad on LG G3, Xperia Z1/2/3.\n\n";
		menuMessage += "Select: Install to install. Restart SpiritF after 10 seconds.\n";
		menuMessage += "Select: Remove to remove.";
		builder.setMessage(menuMessage);

		builder.setNegativeButton("Cancel", new OnClickListener() {     //
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});

		builder.setNeutralButton("Install", new OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
        /*if (com_uti.acdbfix_files_operational_get ()) {
          boolean reinstall_destroys_original = true;
          if (reinstall_destroys_original) {
            Toast.makeText (mContext, "ACDB Fix already installed. Can't reinstall...", Toast.LENGTH_LONG).show ();
          }
          else {
            Toast.makeText (mContext, "ACDB Fix already installed. Reinstalling...", Toast.LENGTH_LONG).show ();
            com_uti.acdbfix_install (mContext);
          }
        }
        else {*/
				Toast.makeText(mContext, "ACDB Fix installing...", Toast.LENGTH_LONG).show();

				m_com_api.key_set("tuner_state", "Stop");
				com_uti.silentSleep(2000);
				com_uti.acdbfix_install(mContext);
				//}
			}
		});

		builder.setPositiveButton("Remove", new OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//if (com_uti.acdbfix_files_operational_get ()) {
				Toast.makeText(mContext, "ACDB Fix installed. Removing...", Toast.LENGTH_LONG).show();

				m_com_api.key_set("tuner_state", "Stop");
				com_uti.silentSleep(2000);
				com_uti.acdbfix_remove(mContext);
        /*}
        else
          Toast.makeText (mContext, "ACDB Fix not installed !!!", Toast.LENGTH_LONG).show ();*/
			}
		});

		return builder.create();
	}


	private Dialog createDialogMenu(final int id) {
		com_uti.logd("id: " + id);

		AlertDialog.Builder dlg_bldr = new AlertDialog.Builder(mContext);
		dlg_bldr.setTitle("SpiritF " + com_uti.getApplicationVersion(mContext));
		ArrayList<String> array_list = new ArrayList<>();
		array_list.add("Set");
		array_list.add("EQ");
		//array_list.add ("SHIM");
		//array_list.add ("ACDB");
		array_list.add("About");
		//array_list.add ("Digital");
		//array_list.add ("Analog");
		//array_list.add ("Sleep");
		//array_list.add ("Test");
		//array_list.add ("Debug");

		dlg_bldr.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
			}
		});

		String[] items = new String[array_list.size()];
		array_list.toArray(items);

		dlg_bldr.setItems(items, new OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				com_uti.logd("item: " + item);
				onMenuSelect(item);
			}

		});

		return (dlg_bldr.create());
	}


	// Old style hardware Menu button stuff:

	private static final int MENU_SET = 0;
	private static final int MENU_EQ = 1;
	private static final int MENU_ABOUT = 2;
	//private static final int MENU_SHIM    = 2;
	//private static final int MENU_ACDB    = 3;
	//private static final int MENU_DIG     = 5;
	//private static final int MENU_ANA     = 6;
	//private static final int MENU_SLEEP   = 7;
	//private static final int MENU_TEST    = 8;
	//private static final int MENU_DEBUG   = 9;

	public boolean onMenuCreate(Menu menu) {
		com_uti.logd("menu: " + menu);
		try {
			menu.add(Menu.NONE, MENU_SET, Menu.NONE, "Set").setIcon(R.drawable.ic_menu_preferences);
			menu.add(Menu.NONE, MENU_EQ, Menu.NONE, "EQ");
			//menu.add (Menu.NONE, MENU_SHIM,   Menu.NONE,         "SHIM");//.setIcon (R.drawable.ic_menu_view);
			//menu.add (Menu.NONE, MENU_ACDB,   Menu.NONE,         "ACDB");//.setIcon (R.drawable.ic_menu_help);
			menu.add(Menu.NONE, MENU_ABOUT, Menu.NONE, "About");//.setIcon (R.drawable.ic_menu_info_details);
			//menu.add (Menu.NONE, MENU_DIG,    Menu.NONE,      "Digital");
			//menu.add (Menu.NONE, MENU_ANA,    Menu.NONE,       "Analog");
			//menu.add (Menu.NONE, MENU_SLEEP,  Menu.NONE,        "Sleep");//.setIcon (R.drawable.ic_lock_power_off);
			//menu.add (Menu.NONE, MENU_TEST,   Menu.NONE,         "Test");//.setIcon (R.drawable.ic_menu_view);
			//menu.add (Menu.NONE, MENU_DEBUG,  Menu.NONE,        "Debug");//.setIcon (R.drawable.ic_menu_help);
		} catch (Throwable e) {
			com_uti.loge("Exception: " + e);
			e.printStackTrace();
		}
		return (true);
	}

	public boolean onMenuSelect(int itemid) {
		com_uti.logd("itemid: " + itemid);                                 // When "Settings" is selected, after pressing Menu key
		switch (itemid) {
			case MENU_SET:
				mActivity.startActivity(new Intent(mContext, set_act.class));// Start Settings activity
				return (true);
			case MENU_EQ:
				startEqualizer();
				return (true);
			case MENU_ABOUT:
				mActivity.showDialog(GUI_ABOUT_DIALOG);
				return (true);
      /*case MENU_SHIM:
        mActivity.showDialog (GUI_SHIM_DIALOG);
        return (true);
      case MENU_ACDB:
        mActivity.showDialog (GUI_ACDB_DIALOG);
        return (true);
      case MENU_DIG:
        m_com_api.key_set ("audio_mode", "Digital");
        return (true);
      case MENU_ANA:
        m_com_api.key_set ("audio_mode", "Analog");
        return (true);*/
			//case MENU_SLEEP:
			//  return (true);
			//case MENU_TEST:
			//  mActivity.showDialog (GUI_TEST_DIALOG);
			//  return (true);
      /*case MENU_DEBUG:
        mActivity.showDialog (GUI_DEBUG_DIALOG);
        return (true);*/
		}
		return (false);                                                     // Not consumed ?
	}

	// Root daemon stuff:

	private void daemon_start_dialog_dismiss() {
		com_uti.logd("");
//    daemon_timeout_stop ();
		if (mDialogDaemonStart != null)
			mDialogDaemonStart.dismiss();
		mDialogDaemonStart = null;
	}

	private Dialog createDialogStartDaemon(final int id) {
		com_uti.logd("id: " + id);

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		builder.setTitle("SpiritF " + com_uti.getApplicationVersion(mContext));

		boolean needDaemon = false;
		String message = "";

		if (!com_uti.isSuInstalled()) {
			message += "ERROR: NO SuperUser/SuperSU/Root  SpiritF REQUIRES Root.\n\n";
		} else if (m_com_api.chass_plug_aud.equals("UNK")) {
			message += "ERROR: Unknown Device. SpiritF REQUIRES International GS1/GS2/GS3/Note/Note2, HTC One, LG G2, Xperia Z+/Qualcomm.\n\n";
		} else if (m_com_api.chass_plug_tnr.equals("BCH")) {
			needDaemon = true;
			message += "SpiritF Root Daemon Starting...\n\n" +
					"HTC One, LG G2 & Sony Z2+ can take 7 seconds and may REBOOT on install.\n\n";
		} else {
			needDaemon = true;
			message += "SpiritF Root Daemon Starting...\n\n";
		}

		/*if (need_daemon) {
			daemon_timeout_start();
		}*/

		builder.setMessage(message);

		// Save so we can dismiss dialog later
		mDialogDaemonStart = builder.create();
		return mDialogDaemonStart;
	}

// mActivity.showDialog (DAEMON_ERROR_DIALOG);    // daemon_dialog    createDialogErrorDaemon

	// Allow up to 15 seconds for SU prompt to be answered. SuperSU should time itself out by then.
	//private int daemon_timeout = 15;

	private Dialog createDialogErrorDaemon(int id) {
		com_uti.logd("id: " + id);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("SpiritF " + com_uti.getApplicationVersion(mContext));
		String daemon_msg = "ERROR: Root Daemon did not start or stop after " + (svc_tnr.service_timeout_daemon_start) + "  seconds.\n\n" +
				"SpiritF REQUIRES Root and did not get it.\n\n" +
				"You need to tap System Settings-> About phone-> Build 7 times to enable System Settings-> Developer Options.\n\n" +
				"Then in Developer Options you need to set Root access to Apps or Apps and ADB.\n\n" +
				"Check SuperSU app or System Settings->Superuser and ensure SpiritF is enabled..\n\n";
		builder.setMessage(daemon_msg);
		return builder.create();
	}

	private Dialog createDialogTunerApiError(int id) {
		com_uti.logd("id: " + id);
		AlertDialog.Builder dlg_bldr = new AlertDialog.Builder(mContext);
		dlg_bldr.setTitle("SpiritF " + com_uti.getApplicationVersion(mContext));
		String daemon_msg = "ERROR: Tuner API did not start or stop.\n\n" +
				"This device may need a kernel with a working FM driver.\n\n";
		dlg_bldr.setMessage(daemon_msg);
		return (dlg_bldr.create());
	}

	private Dialog createDialogTunerError(final int id) {
		com_uti.logd("id: " + id);
		AlertDialog.Builder dlg_bldr = new AlertDialog.Builder(mContext);
		dlg_bldr.setTitle("SpiritF " + com_uti.getApplicationVersion(mContext));
		String daemon_msg = "ERROR: Tuner did not start or stop.\n\n" +
				"This device may need a kernel with a working FM driver.\n\n";
		dlg_bldr.setMessage(daemon_msg);
		return (dlg_bldr.create());
	}

	// Regional settings:

	/**
	 * Set new frequency
	 */
	private Dialog createDialogSetFrequency(int id) {
		com_uti.logd("id: " + id);
		LayoutInflater factory = LayoutInflater.from(mContext);
		final View vRoot = factory.inflate(R.layout.edit_number, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Set Frequency");
		builder.setView(vRoot);
		builder.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				setFrequency(((EditText) vRoot.findViewById(R.id.edit_number)).getText().toString());
			}
		});
		builder.setNegativeButton("Cancel", new OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		return builder.create();
	}

	/**
	 * Rename preset
	 */
	private Dialog createDialogPresetRename(int id) {
		LayoutInflater factory = LayoutInflater.from(mContext);
		final View vRoot = factory.inflate(R.layout.edit_text, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Preset Rename");
		builder.setView(vRoot);
		builder.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				setPresetName(cur_preset_idx, ((EditText) vRoot.findViewById(R.id.edit_text)).getText().toString());
			}
		});
		builder.setNegativeButton("Cancel", new OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {}
		});
		return builder.create();
	}


	private Dialog createDialogPresetChange(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Preset");
		ArrayList<String> alItems = new ArrayList<>();
		//alItems.add("Tune");
		alItems.add("Replace");
		alItems.add("Rename");
		alItems.add("Delete");

		builder.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) { }
		});

		builder.setItems(alItems.toArray(new String[alItems.size()]), new OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
					case -1: // Tune to station
						com_uti.logd("createDialogPresetChange onClick Tune freq: " + m_preset_freq[cur_preset_idx]);
						com_uti.logd("createDialogPresetChange onClick Tune name: " + m_preset_name[cur_preset_idx]);
						setFrequency(String.valueOf(m_preset_freq[cur_preset_idx])); // Change to preset frequency
						break;

					case 0: // Replace preset with currently tuned station
						com_uti.logd("createDialogPresetChange onClick Replace freq: " + m_preset_freq[cur_preset_idx]);
						com_uti.logd("createDialogPresetChange onClick Replace name: " + m_preset_name[cur_preset_idx]);
						replacePresetByCurrentFrequency(cur_preset_idx);
						com_uti.logd("createDialogPresetChange onClick Replace freq: " + m_preset_freq[cur_preset_idx]);
						com_uti.logd("createDialogPresetChange onClick Replace name: " + m_preset_name[cur_preset_idx]);
						break;

					case 1: // Rename preset
						com_uti.logd("createDialogPresetChange onClick Rename freq: " + m_preset_freq[cur_preset_idx]);
						com_uti.logd("createDialogPresetChange onClick Rename name: " + m_preset_name[cur_preset_idx]);
						mActivity.showDialog(PRESET_RENAME_DIALOG);
						com_uti.logd("createDialogPresetChange onClick Rename freq: " + m_preset_freq[cur_preset_idx]);
						com_uti.logd("createDialogPresetChange onClick Rename name: " + m_preset_name[cur_preset_idx]);
						break;

					case 2: // Delete preset   !! Deletes w/ no confirmation
						com_uti.logd("createDialogPresetChange onClick Delete freq: " + m_preset_freq[cur_preset_idx]);
						com_uti.logd("createDialogPresetChange onClick Delete name: " + m_preset_name[cur_preset_idx]);
						removePreset(cur_preset_idx);
						com_uti.logd("createDialogPresetChange onClick Delete freq: " + m_preset_freq[cur_preset_idx]);
						com_uti.logd("createDialogPresetChange onClick Delete name: " + m_preset_name[cur_preset_idx]);
						break;

					default: // Should not happen
						break;
				}

			}
		});

		return builder.create();
	}


	private void setFrequency(String nFreq) {
		// If an empty string...
		if (nFreq.isEmpty()) {
			return;
		}

		Float ffreq = 0f;
		try {
			ffreq = Float.valueOf(nFreq);
		} catch (Throwable e) {
			com_uti.loge("ffreq = Float.valueOf (nFreq); failed");
			//e.printStackTrace ();
		}

// 40.000-399.999
// 400.00-3999.99
// 4000.0-39999.9
// 40000.-399999.

		int frequencyLower = 40000;
		int frequencyHigher = 399999;

		int freq = (int) (ffreq * 1000);

		if (freq < frequencyLower || freq > frequencyHigher * 1000) {
			com_uti.loge("1 Frequency invalid ffreq: " + ffreq + "  freq: " + freq);
			return;
		} else if (freq <= frequencyHigher) {    // For 40 - 399
			freq /= 1;
		} else if (freq >= frequencyLower * 10 && freq <= frequencyHigher * 10) {    // For 400 - 3999
			freq /= 10;
		} else if (freq >= frequencyLower * 100 && freq <= frequencyHigher * 100) {    // For 4000 - 39999
			freq /= 100;
		} else if (freq >= frequencyLower * 1000 && freq <= frequencyHigher * 1000) {    // For 40000 - 399999
			freq /= 1000;
		}

		if (freq < 0) {
			com_uti.loge("2 Frequency invalid ffreq: " + ffreq + "  freq: " + freq);
		} else if (freq > 199999 && freq < 400000) {                          // Codes 200.000 - 299.999 / 300.000 - 399.999 = get/set private Broadcom/Qualcomm control
			m_com_api.key_set("tuner_extension", "" + freq);
			com_uti.logd("get/set tuner extension: " + freq);
		} else if (freq >= com_uti.bandFrequencyLower && freq <= com_uti.bandFrequencyHigher) {
			com_uti.logd("Frequency changing to : " + freq + " KHz");
			m_com_api.key_set("tuner_freq", "" + freq);
		} else {
			com_uti.loge("3 Frequency invalid ffreq: " + ffreq + "  freq: " + freq);
		}
	}


	// :

	private void gui_pwr_update(boolean pwr) { // Enables/disables buttons based on power
		if (pwr) {
			//m_iv_stop.setImageResource (R.drawable.btn_stop);
			m_iv_stop.setImageResource(R.drawable.dial_power_on_250sc);
			m_iv_pwr.setImageResource(R.drawable.dial_power_on);
		} else {
			//m_iv_stop.setImageResource (R.drawable.btn_play);
			m_iv_stop.setImageResource(R.drawable.dial_power_off_250sc);
			m_iv_pwr.setImageResource(R.drawable.dial_power_off);
			resetAllTextView(); // Set all displayable text fields to initial OFF defaults
		}

		// Power button is always enabled
		mivRecord.setEnabled(true); // Leave record button enabled for debug log
		m_iv_seekup.setEnabled(pwr);
		m_iv_seekdn.setEnabled(pwr);
		m_tv_rt.setEnabled(pwr);

		for (int idx = 0; idx < com_api.chass_preset_max; idx++) { // For all presets...
			mibPresets[idx].setEnabled(pwr);
		}
	}


	// CDown timeout stuff:     See m_com_api.service_update_send()

	private int svc_count = 0;
	private int svc_cdown = 0;
	private String svc_phase = "";

	private Handler cdown_timeout_handler = null;
	private Runnable cdown_timeout_runnable = null;

	private void cdown_timeout_stop() {
		com_uti.logd("svc_phase: " + svc_phase + "  svc_count: " + svc_count + "  svc_cdown: " + svc_cdown);
		if (cdown_timeout_handler != null) {
			if (cdown_timeout_runnable != null)
				cdown_timeout_handler.removeCallbacks(cdown_timeout_runnable);
		}
		cdown_timeout_handler = null;
		cdown_timeout_runnable = null;

		m_com_api.service_update_send(null, "", "");  // Prevent future detections++

		m_tv_svc_count.setText("");
	}

	int update_interval = 2;  // 1 is too often

	private void cdown_timeout_start(String phase, int timeout) {
		svc_count = timeout;
		svc_cdown = timeout;
		svc_phase = phase;
		com_uti.logd("svc_phase: " + svc_phase + "  svc_count: " + svc_count + "  svc_cdown: " + svc_cdown);
		m_tv_svc_count.setText(String.valueOf(svc_count));
		if (cdown_timeout_handler != null) { // If there is already a countdown in progress...
			cdown_timeout_stop(); // Stop that countdown
		}

		cdown_timeout_handler = new Handler(); // Create Handler to post delayed
		cdown_timeout_runnable = new Runnable() { // Create runnable to be called and re-scheduled every update_interval seconds until timeout or success
			public void run() {
				svc_cdown -= update_interval;                                   // Count down
				com_uti.logd("svc_phase: " + svc_phase + "  svc_count: " + svc_count + "  svc_cdown: " + svc_cdown);

				if (svc_cdown > 0) { // If timeout not finished yet... Toast Update
					Toast.makeText(mContext, "" + svc_cdown + " " + svc_phase, Toast.LENGTH_SHORT).show();
					m_tv_svc_cdown.setText(String.valueOf(svc_cdown)); // Display new countdown
					if (cdown_timeout_handler != null) { // Schedule next callback/run in update_interval seconds
						cdown_timeout_handler.postDelayed(cdown_timeout_runnable, update_interval * 1000);
					}
				} else { // Else if timeout is finished... handle it w/ error code -777 = timeout
					cdown_error_handle(-777);
				}
			}
		};
		if (cdown_timeout_handler != null) {
			cdown_timeout_handler.postDelayed(cdown_timeout_runnable, update_interval * 1000);
		}
	}

	private void cdown_error_handle(int err) {
		com_uti.logd("svc_phase: " + svc_phase + "  svc_count: " + svc_count + "  svc_cdown: " + svc_cdown);
		cdown_timeout_stop();
		svc_count = err;
		String err_str = svc_phase;
		if (err == -777)
			err_str = "TIMEOUT " + svc_phase;
		else if (!svc_phase.startsWith("ERROR") && !svc_phase.startsWith("TIMEOUT"))
			err_str = "ERROR " + err + " " + svc_phase;

		if (err_str.toLowerCase().contains("daemon")) {
			com_uti.loge("ERROR Daemon /dev/s2d_running: " + com_uti.isFileExistSilent("/dev/s2d_running"));
			daemon_start_dialog_dismiss();
			//if (! com_uti.isFileExistSilent ("/dev/s2d_running"))
			mActivity.showDialog(DAEMON_ERROR_DIALOG);
		} else if (err_str.toLowerCase().contains("tuner api")) {
			com_uti.loge("ERROR Tuner API /dev/s2d_running: " + com_uti.isFileExistSilent("/dev/s2d_running"));
			daemon_start_dialog_dismiss();
			mActivity.showDialog(TUNER_API_ERROR_DIALOG);
		} else if (err_str.toLowerCase().contains("tuner")) {
			com_uti.loge("ERROR Tuner /dev/s2d_running: " + com_uti.isFileExistSilent("/dev/s2d_running"));
			daemon_start_dialog_dismiss();
			mActivity.showDialog(TUNER_ERROR_DIALOG);
		} else if (err_str.toLowerCase().contains("bluetooth")) {
			com_uti.loge("ERROR Broadcom Bluetooth /dev/s2d_running: " + com_uti.isFileExistSilent("/dev/s2d_running"));
			daemon_start_dialog_dismiss();
		}

		com_uti.loge("err_str: " + err_str);
		Toast.makeText(mContext, err_str, Toast.LENGTH_LONG).show();

		if (m_tv_svc_phase != null) {
			m_tv_svc_phase.setText(err_str);
		} else
			com_uti.loge("m_tv_svc_phase == null  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		if (m_tv_svc_count != null)
			m_tv_svc_count.setText("");
		if (m_tv_svc_cdown != null)
			m_tv_svc_cdown.setText("");
	}


	// API Callback:

	public void gap_service_update(Intent intent) {
		//com_uti.logd ("");

		/*if (false) {//! isGUIInited) {
			com_uti.loge("Sticky broadcast too early: isGUIInited: " + isGUIInited);
			return;
		}*/

		// If daemon is running and daemon start dialog is showing, dismiss dialog
		if (mDialogDaemonStart != null && com_uti.isFileExistSilent("/dev/s2d_running")) {
			daemon_start_dialog_dismiss();
		}

		// Power:
		if (m_com_api.tuner_state.equals("Start"))
			gui_pwr_update(true);
		else
			gui_pwr_update(false);


		// Debug / Phase Info:

		m_tv_svc_phase.setText(m_com_api.chass_phase);
		m_tv_svc_cdown.setText(m_com_api.chass_phtmo);

		if (!m_com_api.chass_phtmo.equals("")) {
			com_uti.logd("Intent: " + intent + "  phase: " + m_com_api.chass_phase + "  cdown: " + m_com_api.chass_phtmo);

			if (m_com_api.chass_phtmo.equals("0")) {                       // If Success...
				com_uti.logd("Success m_com_api.chass_phtmo: " + m_com_api.chass_phtmo);
				cdown_timeout_stop();
				//m_com_api.chass_phtmo = "";   // Prevent future detections
			} else {
				int cdown = com_uti.getInt(m_com_api.chass_phtmo);
				if (cdown > 0) {
					com_uti.logd("cdown: " + cdown);
					cdown_timeout_start(m_com_api.chass_phase, cdown);
				} else if (cdown == 0) {
					com_uti.loge("cdown: " + cdown);
				} else {
					com_uti.logd("cdown: " + cdown);
					cdown_error_handle(cdown);
				}
			}
		}

		// Audio Session ID:

		int audioSessionId = com_uti.getInt(m_com_api.audioSessionId);
		if (audioSessionId != 0 && lastAudioSessidInt != audioSessionId) { // If audio session ID has changed...
			lastAudioSessidInt = audioSessionId;
			com_uti.logd("m_com_api.audioSessionId: " + m_com_api.audioSessionId + "  audioSessionId: " + audioSessionId);
			/*
			if (audioSessionId == 0) { // If no session, do nothing (or stop visual and EQ)
			} else { // Else if session...
			}
			*/
		}

		// Mode Buttons at bottom:

		// Mute/Unmute:
		if (m_com_api.audio_state.equals("Start")) {
			mivPlayPause.setImageResource(R.drawable.sel_pause);
		} else {
			mivPlayPause.setImageResource(R.drawable.btn_play);
		}

		// Speaker/Headset:
		if (m_com_api.audio_output.equals("Speaker")) { // Else if speaker..., Pressing button goes to headset
			if (mIvOutput != null) {
				mIvOutput.setImageResource(android.R.drawable.stat_sys_headset);//ic_volume_bluetooth_ad2p);
			}
			mcbSpeaker.setChecked(true);
		} else { // Pressing button goes to speaker
			if (mIvOutput != null) {
				mIvOutput.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
			}
			mcbSpeaker.setChecked(false);
		}

		// Record Start/Stop:
		if (m_com_api.audio_record_state.equals("Start")) {
			mivRecord.setImageResource(R.drawable.btn_record_press);
		} else {
			mivRecord.setImageResource(R.drawable.btn_record);
		}

		// Frequency:
		int ifreq = (int) (com_uti.getDouble(m_com_api.tuner_freq) * 1000);
		ifreq = com_uti.tnru_freq_fix(ifreq + 25); // Must fix due to floating point rounding need, else 106.1 = 106.099

		String freq = null;
		if (ifreq >= 50000 && ifreq < 500000) {
			setDialFrequency(ifreq);
			freq = ("" + (double) ifreq / 1000);
		}
		if (freq != null) {
			m_tv_freq.setText(freq);
		}

		m_tv_rssi.setText(m_com_api.tuner_rssi);

		if (m_com_api.tuner_pilot.equals("Mono"))
			m_tv_pilot.setText("M");
		else if (m_com_api.tuner_pilot.equals("Stereo"))
			m_tv_pilot.setText("S");
		else
			m_tv_pilot.setText("");

		mtvBand.setText(m_com_api.tuner_band);
		m_tv_picl.setText(m_com_api.tuner_rds_picl);
		m_tv_ps.setText(m_com_api.tuner_rds_ps);
		m_tv_ptyn.setText(m_com_api.tuner_rds_ptyn);

		if (!last_rt.equals(m_com_api.tuner_rds_rt)) {
			last_rt = m_com_api.tuner_rds_rt;
			m_tv_rt.setText(m_com_api.tuner_rds_rt);
			m_tv_rt.setSelected(true);
		}

	}


	// UI buttons and other controls:

	// Presets:

	private void removePreset(int idx) {
		com_uti.logd("idx: " + idx);
		mtvPresets[idx].setText("");
		m_preset_freq[idx] = "";
		m_preset_name[idx] = "";
		mibPresets[idx].setImageResource(R.drawable.btn_preset);
		m_com_api.key_set("chass_preset_name_" + idx, "delete", "chass_preset_freq_" + idx, "delete");   // !! Current implementation requires simultaneous
	}

	private void setPresetName(int idx, String name) {
		com_uti.logd("idx: " + idx);
		mtvPresets[idx].setText(name);
		m_preset_name[idx] = name;
		m_com_api.key_set("chass_preset_name_" + idx, name, "chass_preset_freq_" + idx, m_preset_freq[idx]);   // !! Current implementation requires simultaneous
	}

	private void replacePresetByCurrentFrequency(int idx) {
		if (idx >= com_api.chass_preset_max) {
			com_uti.loge("idx: " + idx + "  com_api.chass_preset_max: " + com_api.chass_preset_max + "  m_presets_curr: " + m_presets_curr);
			return;
		} else if (idx > m_presets_curr) {                                    // If trying to set a preset past the last current one (this avoid blank presets)
			com_uti.logd("idx: " + idx + "  com_api.chass_preset_max: " + com_api.chass_preset_max + "  m_presets_curr: " + m_presets_curr);
			boolean set_past_end = true;
			boolean ignore_past_end = true;
			if (set_past_end)
				com_uti.logd("set_past_end");
			else if (ignore_past_end) {
				com_uti.logd("ignore_past_end");
				return;
			} else {
				com_uti.logd("set next available");
				idx = m_presets_curr;                                           // Set index to last + 1 = next new one
			}
		} else
			com_uti.logd("idx: " + idx + "  com_api.chass_preset_max: " + com_api.chass_preset_max + "  m_presets_curr: " + m_presets_curr);

		String freq_text = m_com_api.tuner_freq;
		if (m_com_api.tuner_band.equals("US")) {
			if (!m_com_api.tuner_rds_picl.equals(""))
				freq_text = m_com_api.tuner_rds_picl;
		} else {
			if (!m_com_api.tuner_rds_ps.trim().equals("")) {
				freq_text = m_com_api.tuner_rds_ps;
			}
		}

		if (m_presets_curr < idx + 1)
			m_presets_curr = idx + 1;                                         // Update number of presets

		mtvPresets[idx].setText(freq_text);
		m_preset_name[idx] = freq_text;
		m_preset_freq[idx] = m_com_api.tuner_freq;
		m_com_api.key_set("chass_preset_name_" + idx, "" + freq_text, "chass_preset_freq_" + idx, m_com_api.tuner_freq);   // !! Current implementation requires simultaneous
		mibPresets[idx].setImageResource(R.drawable.transparent);  // R.drawable.btn_preset
	}

	private View.OnClickListener preset_select_lstnr = new                // Tap: Tune to preset
			View.OnClickListener() {
				public void onClick(View v) {
					ani(v);
					com_uti.logd("view: " + v);

					for (int idx = 0; idx < com_api.chass_preset_max; idx++) {       // For all presets...
						if (v == mibPresets[idx]) {                                   // If this preset...
							com_uti.logd("idx: " + idx);
							try {
								if (m_preset_freq[idx].equals(""))                        // If no preset yet...
									//RE-ENABLED because it's a pain      com_uti.loge ("Must long press to press presets now, to avoid accidental sets");
									replacePresetByCurrentFrequency(idx);                                         // Set preset
								else
									setFrequency("" + m_preset_freq[idx]);                      // Else change to preset frequency
							} catch (Throwable e) {
								e.printStackTrace();
							}
							;
							return;
						}
					}

				}
			};

	private int cur_preset_idx = 0;
	// Long press/Tap and Hold: Show preset change options
	private OnLongClickListener preset_change_lstnr = new OnLongClickListener() {
		public boolean onLongClick(View v) {
			ani(v);
			com_uti.logd("view: " + v);
			for (int idx = 0; idx < com_api.chass_preset_max; idx++) { // For all presets...
				if (v == mibPresets[idx]) { // If this preset...
					cur_preset_idx = idx;
					com_uti.logd("idx: " + idx);
					if (m_preset_freq[idx].equals("")) { // If no preset yet...
						replacePresetByCurrentFrequency(idx); // Set preset
					} else {
						mActivity.showDialog(PRESET_CHANGE_DIALOG); // Else show preset options dialog
					}
					break;
				}
			}
			return true; // Consume long click
		}
	};


	// Disabled eye-candy animation:

	private void ani(View v) {
		//if (v != null)
		//  v.startAnimation (m_ani_button);
	}


	private OnLongClickListener long_click_lstnr = new OnLongClickListener() {
		public boolean onLongClick(View v) {
			com_uti.logd("view: " + v);
			ani(v); // Animate button
			if (v == m_iv_menu) {
				mActivity.startActivity(new Intent(mContext, set_act.class));// Start Settings activity
			} else {
				com_uti.loge("view: " + v);
				return false; // Don't consume long click
			}
			return true; // Consume long click
		}
	};

	private View.OnClickListener short_click_lstnr = new View.OnClickListener() {
		public void onClick(View v) {

			com_uti.logd("view: " + v);
			ani(v);                                                          // Animate button
			if (v == null) {
				com_uti.loge("view: " + v);
			} else if (v == m_iv_menu)
				mActivity.showDialog(GUI_MENU_DIALOG);

			else if (v == mivPlayPause)
				m_com_api.key_set("audio_state", "Toggle");

			else if (v == m_iv_stop) {
				m_com_api.key_set("tuner_state", "Toggle");
				//if (m_com_api.tuner_state.equals ("Start"))
				//  m_com_api.key_set ("tuner_state", "Stop");                      // Full power down/up
				//else
				//  m_com_api.key_set ("tuner_state", "Start");                     // Tuner only start, no audio
			} else if (v == mivRecord)
				m_com_api.key_set("audio_record_state", "Toggle");

			else if (v == m_tv_freq)                                          // Frequency direct entry
				mActivity.showDialog(FREQ_SET_DIALOG);

			else if (v == m_iv_prev)
				m_com_api.key_set("tuner_freq", "Down");

			else if (v == m_iv_next)
				m_com_api.key_set("tuner_freq", "Up");

			else if (v == m_iv_volume) {
				AudioManager m_am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
				if (m_am != null) {
					int stream_vol = m_am.getStreamVolume(svc_aud.audio_stream);
					com_uti.logd("stream_vol : " + stream_vol);                  // Set to current volume (no change) to display system volume change dialog for user input on screen
					m_am.setStreamVolume(svc_aud.audio_stream, stream_vol, AudioManager.FLAG_SHOW_UI);
				}
			} else if (v == mIvOutput) {
				if (m_com_api.audio_output.equals("Speaker"))        // If speaker..., Pressing button goes to headset
					m_com_api.key_set("audio_output", "Headset");
				else
					m_com_api.key_set("audio_output", "Speaker");
			} else if (v == m_iv_seekdn) {                                      // Seek down
				if (com_uti.s2_tx_apk())                                       // Transmit navigates presets instead of seeking
					m_com_api.key_set("service_seek_state", "Down");
				else
					m_com_api.key_set("tuner_seek_state", "Down");
			} else if (v == m_iv_seekup) {                                      // Seek up
				if (com_uti.s2_tx_apk())                                       // Transmit navigates presets instead of seeking
					m_com_api.key_set("service_seek_state", "Up");
				else
					m_com_api.key_set("tuner_seek_state", "Up");
			}

		}
	};

	// Handle GUI clicks for 2nd page settings:

	public void onClicked(View view) {                             // See res/layout/gui_pg2_layout.xml & MainActivity
		int id = view.getId();
		com_uti.logd("id: " + id + "  view: " + view);
		switch (id) {

			case R.id.cb_test:
				break;

			case R.id.cb_visu:
				if (((CheckBox) view).isChecked())
					setVisualizerState("Start");
				else
					setVisualizerState("Stop");
				break;

			case R.id.cb_tuner_stereo:
				cb_tuner_stereo(((CheckBox) view).isChecked());
				break;

			case R.id.cb_audio_stereo:
				cb_audio_stereo(((CheckBox) view).isChecked());
				break;

			case R.id.cb_af:
				cb_af(((CheckBox) view).isChecked());
				break;

			case R.id.cb_speaker:
				if (((CheckBox) view).isChecked())
					m_com_api.key_set("audio_output", "Speaker");
				else
					m_com_api.key_set("audio_output", "Headset");
				break;

			case R.id.rb_band_eu:
				tuner_band_set("EU");
				rb_log(view, (RadioButton) view, ((RadioButton) view).isChecked());
				break;

			case R.id.rb_band_us:
				tuner_band_set("US");
				rb_log(view, (RadioButton) view, ((RadioButton) view).isChecked());
				break;

			case R.id.rb_band_uu:
				tuner_band_set("UU");
				rb_log(view, (RadioButton) view, ((RadioButton) view).isChecked());
				break;
		}
	}


	// Preferences:

	private void loadPreferences() {
		String value;
		value = com_uti.prefs_get(mContext, "audio_output", "");

		mcbSpeaker.setChecked(value.equals("Speaker"));

		value = com_uti.prefs_get(mContext, "tuner_stereo", "");
		((CheckBox) mActivity.findViewById(R.id.cb_tuner_stereo)).setChecked(!value.equals("Mono"));

		value = com_uti.prefs_get(mContext, "audio_stereo", "");
		((CheckBox) mActivity.findViewById(R.id.cb_audio_stereo)).setChecked(!value.equals("Mono"));

		((CheckBox) mActivity.findViewById(R.id.cb_visu)).setChecked(false);
	}

	private void setVisualizerState(String state) {
		com_uti.logd("state: " + state);
		if (state.equals("Start")) {
			((LinearLayout) mActivity.findViewById(R.id.vis)).setVisibility(View.VISIBLE);  //initDial

			mDial.setVisibility(View.INVISIBLE);
			m_iv_pwr.setVisibility(View.INVISIBLE);
			mActivity.findViewById(R.id.frequency_bar).setVisibility(View.INVISIBLE);

			int audio_sessid = com_uti.getInt(m_com_api.audioSessionId);
		} else {
			mActivity.findViewById(R.id.vis).setVisibility(View.INVISIBLE);//GONE);

			mDial.setVisibility(View.VISIBLE);
			m_iv_pwr.setVisibility(View.VISIBLE);
			mActivity.findViewById(R.id.frequency_bar).setVisibility(View.VISIBLE);

		}
	}

	void tuner_band_set(String band) {
		m_com_api.tuner_band = band;
		com_uti.tnru_band_set(band);                                            // To setup band values; different process than service

		if (!m_com_api.chass_plug_aud.equals("UNK"))
			m_com_api.key_set("tuner_band", band);
	}

	private void cb_tuner_stereo(boolean checked) {
		com_uti.logd("checked: " + checked);
		String val = "Stereo";
		if (!checked)
			val = "Mono";
		m_com_api.key_set("tuner_stereo", val);
	}

	private void cb_audio_stereo(boolean checked) {
		com_uti.logd("checked: " + checked);
		String val = "Stereo";
		if (!checked)
			val = "Mono";
		m_com_api.key_set("audio_stereo", val);
	}

	private void cb_af(boolean checked) {
		com_uti.logd("checked: " + checked);
		if (checked)
			m_com_api.key_set("tuner_rds_af_state", "Start");
		else
			m_com_api.key_set("tuner_rds_af_state", "Stop");
	}


	// Radio button logging for test/debug:

	private void rb_log(View view, RadioButton rbt, boolean checked) {
		int btn_id = m_rg_band.getCheckedRadioButtonId();            // get selected radio button from radioGroup
		RadioButton rad_band_btn = (RadioButton) mActivity.findViewById(btn_id);            // find the radiobutton by returned id
		com_uti.logd("view: " + view + "  rbt: " + rbt + "  checked: " + checked + "  rad btn text: " + "  btn_id: " + btn_id + "  rad_band_btn: " + rad_band_btn +
				"  text: " + rad_band_btn.getText());
	}


	// Debug logs Email:

	private boolean new_logs = true;
	String logfile = "/sdcard/bugreport.txt";

	private String cmd_build(String cmd) {
		String cmd_head = " ; ";
		String cmd_tail = " >> " + logfile;
		return (cmd_head + cmd + cmd_tail);
	}

	private String new_logs_cmd_get() {
		String cmd = "rm " + logfile;

		cmd += cmd_build("cat /data/data/fm.a2d.sf/shared_prefs/s2_prefs.xml");
		cmd += cmd_build("id");
		cmd += cmd_build("uname -a");
		cmd += cmd_build("getprop");
		cmd += cmd_build("ps");
		cmd += cmd_build("lsmod");
		// !! Wildcard * can lengthen command line unexpectedly !!
		cmd += cmd_build("modinfo /system/vendor/lib/modules/* /system/lib/modules/*");
		//cmd += cmd_build ("dumpsys");                                     // 11 seconds on MotoG
		cmd += cmd_build("dumpsys audio");
		cmd += cmd_build("dumpsys media.audio_policy");
		cmd += cmd_build("dumpsys media.audio_flinger");

		cmd += cmd_build("dmesg");

		cmd += cmd_build("logcat -d -v time");

		cmd += cmd_build("ls -lR /data/data/fm.a2d.sf/ /data/data/fm.a2d.sf/lib/ /init* /sbin/ /firmware/ /data/anr/ /data/tombstones/ /dev/ /system/ /sys/");

		return cmd;
	}


}
