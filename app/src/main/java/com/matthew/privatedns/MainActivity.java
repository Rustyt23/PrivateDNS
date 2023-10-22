package com.matthew.privatedns;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.InputType;

import com.matthew.privatedns.databinding.ActivityMainBinding; // Add this import

public class MainActivity extends AppCompatActivity {
    private MyViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new MyViewModel();
        String inputTypeValue = viewModel.getInputType().get();

        binding.setViewModel(viewModel);
        if ("text".equals(inputTypeValue)) {
            binding.editText.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if ("number".equals(inputTypeValue)) {
            binding.editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        binding.setLifecycleOwner(this);
    }
}
