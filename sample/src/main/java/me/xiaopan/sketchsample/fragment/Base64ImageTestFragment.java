package me.xiaopan.sketchsample.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import me.xiaopan.sketchsample.AssetImage;
import me.xiaopan.sketchsample.BaseFragment;
import me.xiaopan.sketchsample.BindContentView;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.widget.SampleImageView;

@BindContentView(R.layout.fragment_base64_test)
public class Base64ImageTestFragment extends BaseFragment {

    @BindView(R.id.edit_base64TestFragment)
    EditText editText;

    @BindView(R.id.image_base64TestFragment)
    SampleImageView imageView;

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
