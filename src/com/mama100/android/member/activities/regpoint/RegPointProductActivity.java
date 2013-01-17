package com.mama100.android.member.activities.regpoint;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mama100.android.member.R;
import com.mama100.android.member.activities.BaseActivity;
import com.mama100.android.member.asynctask.AsyncProductPicTask;
import com.mama100.android.member.zxing.CaptureActivity;

/**
 * @Description:该页面用来显示产品信息
 * @author created by liyang
 * @date  2012-11-20 下午2:34:04 
 */
public class RegPointProductActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.regpoint_product);

		// 设置顶部标题、按钮图片、页面背景
		setTopLabel(R.string.regpoint_product_title);
		setLeftButtonImage(R.drawable.selector_back);
		setBackgroundPicture(R.drawable.bg_wall);
		
		//加载商品图片、名称、积分
		loadProductImage();
		loadProductNameAndPoint();
		
		
		//点击下一步按钮
		findViewById(R.id.nextstep).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(RegPointProductActivity.this,RegPointShopActivity.class);
				intent.putExtra("productId", getIntent().getStringExtra("productId"));
				intent.putExtra("productSerial",getIntent().getStringExtra("productSerial"));
				intent.putExtra("productPoint", getIntent().getStringExtra("productPoint"));
				intent.putExtra("productImgUrl", getIntent().getStringExtra("productImgUrl"));
				intent.putExtra("productName", getIntent().getStringExtra("productName"));
				intent.putExtra("productCode", getIntent().getStringExtra("productCode"));
				intent.putExtra("isOperationByScan",getIntent().getBooleanExtra("isOperationByScan", false));
				startActivity(intent);
			}
		});
	}

	@Override
	public void doClickLeftBtn() {
		// TODO Auto-generated method stub
		super.doClickLeftBtn();
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		//如果是从扫描页进来的话，返回打开扫描页
		if(getIntent().getBooleanExtra("isOperationByScan", false)){
			startActivity(new Intent(this, CaptureActivity.class));
		}
		finish();
	}

	/**
	 * 加载商品图片
	 */
	void loadProductImage(){
		String productImgUrl = getIntent().getStringExtra("productImgUrl");
		
		View view = findViewById(R.id.productImg);
		view.setTag(productImgUrl);
		
		AsyncProductPicTask task = new AsyncProductPicTask(this);
		task.execute(view);
	}
	
	/**
	 * 加载商品名称和积分信息
	 */
	void loadProductNameAndPoint(){
		String productName = getIntent().getStringExtra("productName");
		String productPoint = getIntent().getStringExtra("productPoint");
		
		((TextView)findViewById(R.id.productName)).setText(productName==null?"未知产品":productName);
		((TextView)findViewById(R.id.productPoint)).setText("＋"+(productPoint==null?0:productPoint));
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		//页面销毁时清除缓存
	}
}
