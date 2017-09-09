package ru.eadm.nobird.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import ru.eadm.nobird.R;
import ru.eadm.nobird.Util;
import ru.eadm.nobird.data.ImageMgr;
import ru.eadm.nobird.data.twitter.TwitterMgr;
import ru.eadm.nobird.databinding.FragmentComposeBinding;
import ru.eadm.nobird.design.animation.OnEndAnimationListener;
import ru.eadm.nobird.design.animation.OnStartAnimationListener;
import ru.eadm.nobird.dialog.DraftDialogFragment;
import ru.eadm.nobird.fragment.implementation.FragmentMgr;
import ru.eadm.nobird.notification.NotificationMgr;
import twitter4j.Status;
import twitter4j.TwitterException;

public final class ComposeFragment extends Fragment implements View.OnClickListener, LocationListener {
    private final static String TAG = "ComposeFragment";

    private final static int ATTACHMENT_SIZE = 0; //23

    private int count = 140;
    private FragmentComposeBinding binding;
    private LocationManager locationManager;
    private Location location;
    private String locationProvider;

    private String attachmentPath, attachmentName;

    private MenuItem action_send;

    private String text;

    /**
     * If true draft dialog won't appear. Used to close fragment on status publish.
     */
    private boolean ignoreDraft = false;

    private final static String ARG_TEXT_FIELD = "text", ARG_IN_REPLY_TO = "inReplyTo";

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_compose, container, false);

        final AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(binding.fragmentComposeToolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try { // TODO: Improve getting this image
            final String image = TwitterMgr.getInstance().account.image;
            ImageMgr.displayImage(binding.fragmentComposeUserImage, image, true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        binding.fragmentComposeText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(final Editable s) { handleTextChange(s); }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        binding.fragmentComposeCounter.setText(String.format(getString(R.string.digit_placeholder), count));


        binding.actionSwitchGeo.setOnClickListener(this);
        binding.actionAttachment.setOnClickListener(this);
        binding.fragmentComposeAttachment.setOnClickListener(this);

        if (attachmentName != null) {
            setAttachment(attachmentPath, attachmentName);
        }

        if (location != null) {
            setLocation(location);
        }

        if (savedInstanceState == null) { // set text on first launch
            final String text = getArguments().getString(ARG_TEXT_FIELD);
            if (text != null) binding.fragmentComposeText.setText(text);
        }

        return binding.getRoot();
    }

    /**
     * Handles text change and update counter
     * @param s - text of editText field
     */
    private void handleTextChange(final Editable s) {
        count = (attachmentName != null ? 140 - ATTACHMENT_SIZE : 140) - s.length();

        if (action_send != null) {
            action_send.setVisible(count < 140);
        }

        binding.fragmentComposeCounter.setText(String.format(getString(R.string.digit_placeholder), count));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        text = binding.fragmentComposeText.getText().toString();
        Util.closeKeyboard(getContext(), binding.fragmentComposeText.getWindowToken());
        binding.unbind();
        binding = null;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        locationProvider = locationManager.getBestProvider(new Criteria(), false);
        ignoreDraft = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeLocation();
        removeAttachment();
        locationManager = null;
        locationProvider = null;

        if (!ignoreDraft && text != null && (text.length() > 0 && !text.equals(getArguments().getString(ARG_TEXT_FIELD))) ) {
                // if text not null and it was changed then asks to save it
            final DialogFragment dialogFragment = new DraftDialogFragment();
            final Bundle bundle = new Bundle();
            bundle.putString(DraftDialogFragment.DATA_KEY, text);
            dialogFragment.setArguments(bundle);
            FragmentMgr.getInstance().showDialog(dialogFragment);
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_compose_menu, menu);
        action_send = menu.findItem(R.id.action_send);
        action_send.setVisible(count < 140);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.action_send) {
            new CreateStatusTask(binding.fragmentComposeText.getText().toString(), attachmentPath,
                    location, getArguments().getLong(ARG_IN_REPLY_TO)).execute();
            ignoreDraft = true;
            FragmentMgr.getInstance().back();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
        action_send = null;
    }

    @Override
    public void onClick(final View v) {
        if (binding == null) return;
        switch (v.getId()) {
            case R.id.action_switch_geo:
                if (!v.isActivated()) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        v.setActivated(true);
                        for (final String provider : locationManager.getAllProviders()) {
                            setLocation(locationManager.getLastKnownLocation(provider));
                        }
                        locationManager.requestLocationUpdates(locationProvider, 400, 1, this);
                    } else {
                        NotificationMgr.getInstance().showSnackbar(R.string.error_location, null);
                    }
                } else {
                    removeLocation();
                }
                break;
            case R.id.action_attachment:
                openImagePicker();
                break;
            case R.id.fragment_compose_attachment:
                removeAttachment();
                break;
        }
    }

    /**
     * Sets current location, obtain it's name as string and displays it with animation
     * @param location - new location
     */
    private void setLocation(final Location location) {
        if (location == null) return;
        this.location = location;
        final double lat = location.getLatitude();
        final double lng = location.getLongitude();

        final Geocoder geoCoder = new Geocoder(getContext(), Locale.getDefault());
        String city;

        try {
            final Address address = geoCoder.getFromLocation(lat, lng, 1).get(0);
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i <= address.getMaxAddressLineIndex(); ++i) {
                builder.append(address.getAddressLine(i));
                builder.append(' ');
            }
            city = builder.toString().trim();
        } catch (final IOException e) {
            e.printStackTrace();
            city = lat + ", " + lng;
        }

        if (binding == null) return;

        binding.actionSwitchGeo.setActivated(true);
        binding.fragmentComposeGeo.setText(city);
        if (binding.fragmentComposeGeo.getVisibility() == View.GONE) {
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_from_top_start);
            animation.setAnimationListener(new OnStartAnimationListener(binding.fragmentComposeGeo));
            binding.fragmentComposeGeo.startAnimation(animation);
        }
    }
    private void removeLocation() {
        this.location = null;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.locationManager.removeUpdates(this);
        }

        if (binding == null) return;
        binding.actionSwitchGeo.setActivated(false);
        if (binding.fragmentComposeGeo.getVisibility() == View.VISIBLE) {
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_from_top_end);
            animation.setAnimationListener(new OnEndAnimationListener(binding.fragmentComposeGeo));
            binding.fragmentComposeGeo.startAnimation(animation);
        }
    }

    private void setAttachment(final String path, final String name) {
        this.attachmentName = name;
        this.attachmentPath = path;

        if (binding == null) return;

        binding.actionAttachment.setActivated(true);
        binding.fragmentComposeAttachment.setText(name);
        if (binding.fragmentComposeAttachment.getVisibility() == View.GONE) {
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_from_bottom_start);
            animation.setAnimationListener(new OnStartAnimationListener(binding.fragmentComposeAttachment));
            binding.fragmentComposeAttachment.startAnimation(animation);
        }

        handleTextChange(binding.fragmentComposeText.getText());
    }
    private void removeAttachment() {
        this.attachmentName = null;
        this.attachmentPath = null;

        if (binding == null) return;
        binding.actionAttachment.setActivated(false);
        if (binding.fragmentComposeAttachment.getVisibility() == View.VISIBLE) {
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_from_bottom_end);
            animation.setAnimationListener(new OnEndAnimationListener(binding.fragmentComposeAttachment));
            binding.fragmentComposeAttachment.startAnimation(animation);
        }

        handleTextChange(binding.fragmentComposeText.getText());
    }

    private final static int REQUEST_CODE_PICKER = 0x3242;
    public void openImagePicker() {
        final Intent intent = new Intent(getContext(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_MODE, ImagePickerActivity.MODE_SINGLE);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SHOW_CAMERA, true);
        startActivityForResult(intent, REQUEST_CODE_PICKER);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == REQUEST_CODE_PICKER && resultCode == ImagePickerActivity.RESULT_OK) {
            final ArrayList<Image> images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            if (images.size() > 0) {
                setAttachment(images.get(0).getPath(), images.get(0).getName());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static void open() {
        open(0, null);
    }
    public static void open(final long replyID, final String text) {
        final Fragment fragment = new ComposeFragment();

        final Bundle bundle = new Bundle();
        if (text != null) bundle.putString(ARG_TEXT_FIELD, text);
        if (replyID != 0) bundle.putLong(ARG_IN_REPLY_TO, replyID);
        fragment.setArguments(bundle);

        FragmentMgr.getInstance().replaceFragment(0, fragment, true);
    }



    @Override
    public void onLocationChanged(final Location location) {
        setLocation(location);
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onProviderDisabled(String provider) {}

    private final class CreateStatusTask extends AsyncTask<Void, Void, Status> {
        private final String text, attachmentPath;
        private final Location location;
        private final long inReplyTo;

        private CreateStatusTask(final String text, final String attachmentPath, final Location location, final long inReplyTo) {
            this.text = text;
            this.attachmentPath = attachmentPath;
            this.location = location;
            this.inReplyTo = inReplyTo;
        }

        @Override
        protected void onPreExecute() {
            NotificationMgr.getInstance().showInfiniteSnackbar(R.string.process_status_publish, null);
        }

        @Override
        protected twitter4j.Status doInBackground(final Void... params) {
            try {
                return TwitterMgr.getInstance().createStatus(text, attachmentPath, location, inReplyTo);
            } catch (final TwitterException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final twitter4j.Status status) {
            if (status == null) {
                NotificationMgr.getInstance().showSnackbar(R.string.error_status_publish, null);
                open(inReplyTo, text);
            } else {
                NotificationMgr.getInstance().showSnackbar(R.string.success_status_publish, null);
            }
        }
    }
}
