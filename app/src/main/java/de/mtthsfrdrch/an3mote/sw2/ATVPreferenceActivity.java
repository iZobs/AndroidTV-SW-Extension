package de.mtthsfrdrch.an3mote.sw2;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.widget.Toast;

public class ATVPreferenceActivity extends android.preference.PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new AboutFragment())
                .commit();
    }

    public static class AboutFragment extends PreferenceFragment implements OnPreferenceClickListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.about);

            findPreference("pref_an3mote").setOnPreferenceClickListener(this);
            findPreference("pref_share").setOnPreferenceClickListener(this);
            findPreference("pref_rate").setOnPreferenceClickListener(this);
            findPreference("pref_feedback").setOnPreferenceClickListener(this);
        }

        private void shareAppLink() {
            String link = getString(R.string.about_playStoreLink, getActivity().getPackageName());
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, link);
            intent.setType("text/plain");
            startActivity(intent);
        }

        private void rateOnGooglePlay() {
            String uri = getString(R.string.about_playStoreUri, getActivity().getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                String url = getString(R.string.about_playStoreLink, getActivity().getPackageName());
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        }

        public void sendFeedback() {
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                    new String[]{getString(R.string.about_mail)});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                    getString(R.string.app_name) + ' ' + getString(R.string.app_version));

            String androidVersion = "Android " + Build.VERSION.RELEASE + " (" + Build.MODEL + ")";
            String text = "Hi,\n\n\n\n" + androidVersion;
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
            startActivity(emailIntent);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();
            switch (key) {
                case "pref_an3mote":
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_an3mote_url)));
                        startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), R.string.error_noActivity, Toast.LENGTH_LONG).show();
                    }
                    break;
                case "pref_share":
                    shareAppLink();
                    break;
                case "pref_rate":
                    rateOnGooglePlay();
                    break;
                case "pref_feedback":
                    sendFeedback();
                    break;
            }

            return true;
        }
    }


}
