package com.heavenly.ticket.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.heavenly.ticket.R;
import com.heavenly.ticket.transaction.BaseResponse;
import com.heavenly.ticket.transaction.LoginTransaction;
import com.heavenly.ticket.util.BitmapUtils;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for account and password at the time of the login attempt.
	private String mAccount;
	private String mPassword;

	// UI references.
	private EditText mAccountView;
	private EditText mPasswordView;
	private EditText mVerifyCodeValue;
	private ImageView mVerifyCodeImage;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	
	private LoginTransaction mService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Set up the login form.
		mAccount = getIntent().getStringExtra(EXTRA_EMAIL);
		mAccountView = (EditText) findViewById(R.id.account);
		if (!TextUtils.isEmpty(mAccount)) {
			mAccountView.setText(mAccount);
		}
		mVerifyCodeImage = (ImageView) findViewById(R.id.verify_code_image);
		mVerifyCodeValue = (EditText) findViewById(R.id.verify_code_value);
		mPasswordView = (EditText) findViewById(R.id.password);

		mVerifyCodeValue
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		attemptVerifyCode();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}
	
	public void onVerifyCodeImageClick(View view) {
		attemptVerifyCode();
	}
	
	public void attemptVerifyCode() {
		showProgress(true);
		new VerifyCodeDownlodTask().execute();
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mAccountView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mAccount = mAccountView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mAccount)) {
			mAccountView.setError(getString(R.string.error_field_required));
			focusView = mAccountView;
			cancel = true;
//		} else if (!mAccount.contains("@")) {
//			mAccountView.setError(getString(R.string.error_invalid_email));
//			focusView = mAccountView;
//			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			if (mService == null) {
				mService = new LoginTransaction();
			}
			mService.setUserName(mAccountView.getText().toString());
			mService.setPassword(mPasswordView.getText().toString());
			mService.setVerfiyCode(mVerifyCodeValue.getText().toString());
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}
	
	private void enterMain() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("show.value.user.showname", mService.getUserShowName());
		intent.putExtra("show.value.user.showgender",
				mService.getUserShowGender());
		startActivity(intent);
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
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	public class VerifyCodeDownlodTask extends AsyncTask<Void, Void, Bitmap> {
		final String CODE_URL = "https://dynamic.12306.cn/otsweb/passCodeAction.do";
		@Override
		protected Bitmap doInBackground(Void... arg0) {
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("rand", "sjrand"));
			return BitmapUtils.getFromURL(CODE_URL, list);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			showProgress(false);
			if (result == null) {
				mVerifyCodeImage.setImageDrawable(null);
				return;
			}
			mVerifyCodeImage.setImageBitmap(result);
		}
		
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, BaseResponse> {
		@Override
		protected BaseResponse doInBackground(Void... params) {
			CookieSyncManager.createInstance(LoginActivity.this);

			return mService.doAction();
		}

		@Override
		protected void onPostExecute(final BaseResponse result) {
			mAuthTask = null;
			showProgress(false);

			if (result.success) {
				enterMain();
				finish();
			} else {
//				mPasswordView
//						.setError(getString(R.string.error_incorrect_password));
//				mPasswordView.requestFocus();
			}
			Toast.makeText(LoginActivity.this, String.valueOf(result.msg),
					Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
