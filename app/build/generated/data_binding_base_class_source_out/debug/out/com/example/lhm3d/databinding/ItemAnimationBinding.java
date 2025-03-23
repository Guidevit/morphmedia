// Generated by view binder compiler. Do not edit!
package com.example.lhm3d.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.lhm3d.R;
import com.google.android.material.card.MaterialCardView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ItemAnimationBinding implements ViewBinding {
  @NonNull
  private final MaterialCardView rootView;

  @NonNull
  public final Button buttonApply;

  @NonNull
  public final ImageView imageViewAnimationType;

  @NonNull
  public final TextView textViewAnimationDescription;

  @NonNull
  public final TextView textViewAnimationName;

  private ItemAnimationBinding(@NonNull MaterialCardView rootView, @NonNull Button buttonApply,
      @NonNull ImageView imageViewAnimationType, @NonNull TextView textViewAnimationDescription,
      @NonNull TextView textViewAnimationName) {
    this.rootView = rootView;
    this.buttonApply = buttonApply;
    this.imageViewAnimationType = imageViewAnimationType;
    this.textViewAnimationDescription = textViewAnimationDescription;
    this.textViewAnimationName = textViewAnimationName;
  }

  @Override
  @NonNull
  public MaterialCardView getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemAnimationBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemAnimationBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_animation, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemAnimationBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonApply;
      Button buttonApply = ViewBindings.findChildViewById(rootView, id);
      if (buttonApply == null) {
        break missingId;
      }

      id = R.id.imageViewAnimationType;
      ImageView imageViewAnimationType = ViewBindings.findChildViewById(rootView, id);
      if (imageViewAnimationType == null) {
        break missingId;
      }

      id = R.id.textViewAnimationDescription;
      TextView textViewAnimationDescription = ViewBindings.findChildViewById(rootView, id);
      if (textViewAnimationDescription == null) {
        break missingId;
      }

      id = R.id.textViewAnimationName;
      TextView textViewAnimationName = ViewBindings.findChildViewById(rootView, id);
      if (textViewAnimationName == null) {
        break missingId;
      }

      return new ItemAnimationBinding((MaterialCardView) rootView, buttonApply,
          imageViewAnimationType, textViewAnimationDescription, textViewAnimationName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
