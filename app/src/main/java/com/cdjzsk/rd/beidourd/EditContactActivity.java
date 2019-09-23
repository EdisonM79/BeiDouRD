package com.cdjzsk.rd.beidourd;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cdjzsk.rd.beidourd.data.MyDataHander;

public class EditContactActivity extends AppCompatActivity {

	private UserLoginTask mAuthTask = null;

	// UI references.
	private EditText cardId;
	private EditText contactName;
	private View mProgressView;
	private View mLoginFormView;
	private ImageView reButton;

	private String otherId;
	private String otherName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_contact);

		Intent intent = getIntent();
		otherId = intent.getStringExtra("otherId");
		otherName = intent.getStringExtra("otherName");

		// Set up the login form.
		cardId = (EditText) findViewById(R.id.cardId);
		cardId.setText(otherId);

		reButton = (ImageView) findViewById(R.id.reButton);
		reButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		contactName = (EditText) findViewById(R.id.contactName);
		contactName.setText(otherName);
		//昵称的编辑监听器
		contactName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		Button del_Contact = findViewById(R.id.button_del_Contact);
		del_Contact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String id = cardId.getText().toString();
				String name = contactName.getText().toString();
				MyDataHander.deleteContactById(id);
				StringBuilder toastMessage = new StringBuilder();
				toastMessage.append("用户").append(name).append("(").append(id).append(")").append("删除成功！");
				Toast.makeText(EditContactActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
				finish();
			}
		});

		/** 注册按钮的监听器 */
		Button mEmailSignInButton = findViewById(R.id.button_Add_Contact);
		mEmailSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});
		mLoginFormView = findViewById(R.id.login_form);
		mProgressView = findViewById(R.id.login_progress);
	}

	/**
	 * 尝试注册
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	private void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		cardId.setError(null);
		contactName.setError(null);

		// Store values at the time of the login attempt.
		String id = cardId.getText().toString();
		String name = contactName.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// 检测用户名是否为空.
		if (TextUtils.isEmpty(name)) {
			contactName.setError(getString(R.string.error_invalid_contact_name));
			focusView = contactName;
			cancel = true;
		}

		// 检测联系人卡号是否为空.
		if (TextUtils.isEmpty(id)) {
			cardId.setError(getString(R.string.error_field_required));
			focusView = cardId;
			cancel = true;
		} else if (!isCardIdValid(id)) {
			cardId.setError(getString(R.string.error_invalid_user_id));
			focusView = cardId;
			cancel = true;
		}
/*		if (isExist(id, name)) {
			cardId.setError(getString(R.string.error_data_exist));
			focusView = cardId;
			cancel = true;
		}*/
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			showProgress(true);
			/**
			 * 步骤2：创建AsyncTask子类的实例对象（即 任务实例）
			 * 注：AsyncTask子类的实例必须在UI线程中创建
			 */
			mAuthTask = new UserLoginTask(id, name);
			/**
			 * 步骤3：手动调用execute(Params... params) 从而执行异步线程任务
			 * 注：
			 *    a. 必须在UI线程中调用
			 *    b. 同一个AsyncTask实例对象只能执行1次，若执行第2次将会抛出异常
			 *    c. 执行任务中，系统会自动调用AsyncTask的一系列方法：onPreExecute() 、doInBackground()、onProgressUpdate() 、onPostExecute()
			 *    d. 不能手动调用上述方法
			 */
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * 验证北斗卡的长度
	 *
	 * @param userId
	 * @return
	 */
	private boolean isCardIdValid(String userId) {
		//TODO: Replace this with your own logic
		return userId.length() >= 6;
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime).alpha(
					show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			});

			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mProgressView.animate().setDuration(shortAnimTime).alpha(
					show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		private final String id;
		private final String name;

		UserLoginTask(String id, String name) {
			this.id = id;
			this.name = name;
		}

		// 方法2：doInBackground（）
		// 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果
		// 注：必须复写，从而自定义线程任务
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				String name = contactName.getText().toString();
				MyDataHander.updateContactById(otherId,name);

				Thread.sleep(500);
			} catch (InterruptedException e) {
				return false;
			}
			// TODO: register the new account here.
			return true;
		}

		// 方法4：onPostExecute（）
		// 作用：接收线程任务执行结果、将执行结果显示到UI组件
		// 注：必须复写，从而自定义UI操作
		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				finish();
			} else {
				contactName.setError(getString(R.string.error_incorrect_password));
				contactName.requestFocus();
			}
		}

		// 方法5：onCancelled()
		// 作用：将异步任务设置为：取消状态
		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}

