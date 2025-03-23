// Generated by view binder compiler. Do not edit!
package com.example.lhm3d.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.lhm3d.R;
import com.google.android.material.button.MaterialButton;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentProfileBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final MaterialButton buttonLoginLogout;

  @NonNull
  public final CardView cardSettings;

  @NonNull
  public final CardView cardUserInfo;

  @NonNull
  public final ImageView imageProfile;

  @NonNull
  public final TextView settingAbout;

  @NonNull
  public final TextView settingAccount;

  @NonNull
  public final TextView settingNotifications;

  @NonNull
  public final TextView settingSubscription;

  @NonNull
  public final TextView textUserEmail;

  @NonNull
  public final TextView textUserName;

  @NonNull
  public final TextView textUserStatus;

  private FragmentProfileBinding(@NonNull ConstraintLayout rootView,
      @NonNull MaterialButton buttonLoginLogout, @NonNull CardView cardSettings,
      @NonNull CardView cardUserInfo, @NonNull ImageView imageProfile,
      @NonNull TextView settingAbout, @NonNull TextView settingAccount,
      @NonNull TextView settingNotifications, @NonNull TextView settingSubscription,
      @NonNull TextView textUserEmail, @NonNull TextView textUserName,
      @NonNull TextView textUserStatus) {
    this.rootView = rootView;
    this.buttonLoginLogout = buttonLoginLogout;
    this.cardSettings = cardSettings;
    this.cardUserInfo = cardUserInfo;
    this.imageProfile = imageProfile;
    this.settingAbout = settingAbout;
    this.settingAccount = settingAccount;
    this.settingNotifications = settingNotifications;
    this.settingSubscription = settingSubscription;
    this.textUserEmail = textUserEmail;
    this.textUserName = textUserName;
    this.textUserStatus = textUserStatus;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentProfileBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentProfileBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_profile, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentProfileBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.button_login_logout;
      MaterialButton buttonLoginLogout = ViewBindings.findChildViewById(rootView, id);
      if (buttonLoginLogout == null) {
        break missingId;
      }

      id = R.id.card_settings;
      CardView cardSettings = ViewBindings.findChildViewById(rootView, id);
      if (cardSettings == null) {
        break missingId;
      }

      id = R.id.card_user_info;
      CardView cardUserInfo = ViewBindings.findChildViewById(rootView, id);
      if (cardUserInfo == null) {
        break missingId;
      }

      id = R.id.image_profile;
      ImageView imageProfile = ViewBindings.findChildViewById(rootView, id);
      if (imageProfile == null) {
        break missingId;
      }

      id = R.id.setting_about;
      TextView settingAbout = ViewBindings.findChildViewById(rootView, id);
      if (settingAbout == null) {
        break missingId;
      }

      id = R.id.setting_account;
      TextView settingAccount = ViewBindings.findChildViewById(rootView, id);
      if (settingAccount == null) {
        break missingId;
      }

      id = R.id.setting_notifications;
      TextView settingNotifications = ViewBindings.findChildViewById(rootView, id);
      if (settingNotifications == null) {
        break missingId;
      }

      id = R.id.setting_subscription;
      TextView settingSubscription = ViewBindings.findChildViewById(rootView, id);
      if (settingSubscription == null) {
        break missingId;
      }

      id = R.id.text_user_email;
      TextView textUserEmail = ViewBindings.findChildViewById(rootView, id);
      if (textUserEmail == null) {
        break missingId;
      }

      id = R.id.text_user_name;
      TextView textUserName = ViewBindings.findChildViewById(rootView, id);
      if (textUserName == null) {
        break missingId;
      }

      id = R.id.text_user_status;
      TextView textUserStatus = ViewBindings.findChildViewById(rootView, id);
      if (textUserStatus == null) {
        break missingId;
      }

      return new FragmentProfileBinding((ConstraintLayout) rootView, buttonLoginLogout,
          cardSettings, cardUserInfo, imageProfile, settingAbout, settingAccount,
          settingNotifications, settingSubscription, textUserEmail, textUserName, textUserStatus);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
