package de.rhaeus.dndsync;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class MainFragment extends PreferenceFragmentCompat {
    private Preference dndPref;
//    private Preference accPref;
    private SwitchPreferenceCompat bedtimePref;
    private Preference secureSettingsPref;



    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        dndPref = findPreference("dnd_permission_key");
//        accPref = findPreference("acc_permission_key");
        bedtimePref = findPreference("bedtime_key");
        secureSettingsPref = findPreference("secure_settings_permission_key");


        dndPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                if (!checkDNDPermission()) {
                    Toast.makeText(getContext(), "Follow the instructions to grant the permission via ADB!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        secureSettingsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                if (!checkSecureSettingsPermission(getContext())) {
                    Toast.makeText(getContext(), "Follow the instructions to grant the permission via ADB!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

//        accPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//        public boolean onPreferenceClick(Preference preference) {
//            if (!checkAccessibilityService()) {
//                openAccessibility();
//            }
//            return true;
//            }
//        });

        checkDNDPermission();
//        checkAccessibilityService();
        checkSecureSettingsPermission(getContext());
    }

    public Context getAppContext() {
        return getContext();
    }

//    private boolean checkAccessibilityService() {
//        DNDSyncAccessService serv = DNDSyncAccessService.getSharedInstance();
//        boolean connected = serv != null;
//        if (connected) {
//            accPref.setSummary(R.string.acc_permission_allowed);
//            bedtimePref.setEnabled(true);
//        } else {
//            accPref.setSummary(R.string.acc_permission_not_allowed);
//            bedtimePref.setEnabled(false);
//            bedtimePref.setChecked(false);
//        }
//        return connected;
//    }

    private boolean checkDNDPermission() {
        NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        boolean allowed = mNotificationManager.isNotificationPolicyAccessGranted();
        if (allowed) {
            dndPref.setSummary(R.string.dnd_permission_allowed);
        } else {
            dndPref.setSummary(R.string.dnd_permission_not_allowed);
        }
        return allowed;
    }

    private boolean checkSecureSettingsPermission(Context context) {
        boolean allowed;
        allowed = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        if (allowed) {
            secureSettingsPref.setSummary(R.string.secure_settings_permission_allowed);
        } else {
            secureSettingsPref.setSummary(R.string.secure_settings_permission_not_allowed);
        }
        return allowed;
    }

    private void openAccessibility() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }
}