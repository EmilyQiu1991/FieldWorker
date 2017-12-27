package com.example.fieldworker1;

import java.util.UUID;

import com.example.dao.AddLogDao;
import com.example.dao.DeleteLogDao;
import com.example.dao.UserDao;
import com.example.domain.AddLog;
import com.example.domain.DeleteLog;
import com.example.fieldworker1.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ResetPasswordActivity extends Activity {

	private EditText oldpwField;
	private EditText newpwField;
	private EditText newpwcField;

	private Button submit, cancel;
	private String userPassword;
	private String username;

	private AddLogDao addLogDao;
	private DeleteLogDao deleteLogDao;
	private UserDao userHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_password);

		oldpwField = (EditText) findViewById(R.id.oldPasswordField);
		newpwField = (EditText) findViewById(R.id.newPasswordField);
		newpwcField = (EditText) findViewById(R.id.newPasswordComField);
		submit = (Button) findViewById(R.id.submitButton);
		cancel = (Button) findViewById(R.id.reset_password_cancel);

		cancel.setOnClickListener(new cancelListener());

		Intent intent = getIntent();
		userPassword = intent.getStringExtra("password");
		username = intent.getStringExtra("username");
		userHelper = new UserDao(this);
		addLogDao = new AddLogDao(this);
		deleteLogDao = new DeleteLogDao(this);
		submit.setOnClickListener(new submitListener());
	}

	class cancelListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}

	}

	class submitListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			String oldpw = oldpwField.getText().toString();
			String newps = newpwField.getText().toString();
			String newpsc = newpwcField.getText().toString();

			if (oldpw.equals(userPassword)) {
				if (oldpw.equals(newps)) {
					Toast toast = Toast
							.makeText(
									ResetPasswordActivity.this,
									"Your new password cannot be identical with old password .",
									Toast.LENGTH_SHORT);
					toast.show();
				} else if (newps.equals(newpsc)) {
					userHelper.resetPassword(username, newpsc);
					AddLog addLog = new AddLog(UUID.randomUUID().hashCode(),
							"User", username);

					if (!addLogDao.checkExist(addLog)) {
						DeleteLog deleteLog = new DeleteLog(UUID.randomUUID()
								.hashCode(), "User", username);
						deleteLogDao.insert(deleteLog);

						addLogDao.insert(addLog);
					}

					Toast toast = Toast
							.makeText(
									ResetPasswordActivity.this,
									"You have changed your password, and login again please:).",
									Toast.LENGTH_SHORT);
					toast.show();
					Intent intent = new Intent();
					intent.setClass(ResetPasswordActivity.this,
							MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("flag", 1);
					startActivity(intent);
				} else {
					Toast toast = Toast.makeText(ResetPasswordActivity.this,
							"The new password is not identical.",
							Toast.LENGTH_SHORT);
					toast.show();
				}
			} else {
				Toast toast = Toast.makeText(ResetPasswordActivity.this,
						"The old password is incorrect.", Toast.LENGTH_SHORT);
				toast.show();
			}
		}

	}

}
