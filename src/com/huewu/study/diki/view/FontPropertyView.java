package com.huewu.study.diki.view;

import com.huewu.study.diki.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;

public class FontPropertyView extends LinearLayout{
	
	private NumberPicker mFontSizePicker = null;
	
	public FontPropertyView(Context context)
	{
		super(context);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.font_property, this);
		
		mFontSizePicker = (NumberPicker) findViewById(R.id.ft_size_spinner);
		mFontSizePicker.setMaxValue(80);
		mFontSizePicker.setMinValue(0);
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
//		adapter.add("10sp");
//		adapter.add("12sp");
//		adapter.add("14sp");
//		adapter.add("18sp");
//		adapter.add("25sp");
//		adapter.add("40sp");
//		adapter.add("80sp");
//		mFontSizeSpinner.setAdapter(adapter);
		
//		setOrientation(VERTICAL);
//		Button btn = null;
//		LayoutParams param = null;
//		param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//
//		btn = new Button(context);
//		btn.setLayoutParams(param);
//		btn.setText("Hello Btn#1");
//		addView(btn);
//		
//		btn = new Button(context);
//		btn.setLayoutParams(param);
//		btn.setText("Hello Btn#2");
//		addView(btn);
	}

	public FontPropertyView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

}//end of class
