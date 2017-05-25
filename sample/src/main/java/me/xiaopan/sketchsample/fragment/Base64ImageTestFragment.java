package me.xiaopan.sketchsample.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketchsample.AssetImage;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.widget.MyImageView;

@InjectContentView(R.layout.fragment_base64_test)
public class Base64ImageTestFragment extends MyFragment {

    @InjectView(R.id.edit_base64TestFragment)
    private EditText editText;

    @InjectView(R.id.image_base64TestFragment)
    private MyImageView imageView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showImage(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editText.setText(AssetImage.BASE64_IMAGE);
    }

    private void showImage(String imageText) {
        imageView.displayImage(imageText);
    }
}
