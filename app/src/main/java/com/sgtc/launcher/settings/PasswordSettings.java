package com.sgtc.launcher.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.sgtc.launcher.Config;
import com.sgtc.launcher.R;
import com.sgtc.launcher.util.PM;
import com.sgtc.launcher.util.dialog.KWARTDialog;

public class PasswordSettings extends AppCompatActivity {
    private SwitchCompat protectAppsEnable;
    private SwitchCompat showOtherApps;
    private SwitchCompat showInternetApps;
    private Button changePassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_settings);
        protectAppsEnable = findViewById(R.id.protectAppsEnable);
        showOtherApps = findViewById(R.id.showOtherApps);
        showInternetApps = findViewById(R.id.showInternetApps);
        changePassword = findViewById(R.id.changePassword);
        protectAppsEnable.setChecked((Boolean) PM.get(Config.KEY_PROTECT_APPS, true));
        showOtherApps.setChecked((Boolean) PM.get(Config.KEY_SHOW_OTHER_APPS, true));
        showInternetApps.setChecked((Boolean) PM.get(Config.KEY_SHOW_INTERNET_APPS, true));
        protectAppsEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PM.put(Config.KEY_PROTECT_APPS, b);
                sendBroadcast(new Intent(getPackageName() + ".SETTINGS_CHANGED"));
            }
        });
        showOtherApps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PM.put(Config.KEY_SHOW_OTHER_APPS, b);
                sendBroadcast(new Intent(getPackageName() + ".SETTINGS_CHANGED"));
            }
        });
        showInternetApps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PM.put(Config.KEY_SHOW_INTERNET_APPS, b);
                sendBroadcast(new Intent(getPackageName() + ".SETTINGS_CHANGED"));
            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final KWARTDialog askPassword = new KWARTDialog(PasswordSettings.this);
                final KWARTDialog newPassword = new KWARTDialog(PasswordSettings.this);
                final KWARTDialog confirmPassword = new KWARTDialog(PasswordSettings.this);
                final KWARTDialog infoPassword = new KWARTDialog(PasswordSettings.this);
                infoPassword.setDoneListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        infoPassword.close();
                    }
                });
                askPassword.setCancelListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        askPassword.close();
                    }
                });
                askPassword.setDoneListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String enteredPassword = askPassword.editText.getText().toString();
                        if (enteredPassword.equalsIgnoreCase(Config.SECRET_CODE) || enteredPassword.equalsIgnoreCase((String) PM.get(Config.KEY_CONTROL_PASSWORD, Config.DEFAULT_CODE))) {
                            newPassword.setCancelListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    askPassword.close();
                                    newPassword.close();
                                    confirmPassword.close();
                                    infoPassword.close();
                                }
                            });
                            newPassword.setDoneListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    confirmPassword.setCancelListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            askPassword.close();
                                            newPassword.close();
                                            confirmPassword.close();
                                            infoPassword.close();
                                        }
                                    });
                                    confirmPassword.setDoneListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (newPassword.editText.getText().toString().equalsIgnoreCase(confirmPassword.editText.getText().toString())) {
                                                PM.put(Config.KEY_CONTROL_PASSWORD, confirmPassword.editText.getText().toString());
                                                infoPassword.setDoneListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        askPassword.close();
                                                        newPassword.close();
                                                        confirmPassword.close();
                                                        infoPassword.close();
                                                    }
                                                });
                                                infoPassword.show(
                                                        getString(R.string.saved),
                                                        getString(R.string.new_password_saved),
                                                        "",
                                                        getString(R.string.ok)
                                                );
                                            } else {
                                                infoPassword.setDoneListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        askPassword.close();
                                                        newPassword.close();
                                                        confirmPassword.close();
                                                        infoPassword.close();
                                                    }
                                                });
                                                infoPassword.show(
                                                        getString(R.string.error),
                                                        getString(R.string.password_mismatch),
                                                        "",
                                                        getString(R.string.ok)
                                                );
                                            }
                                        }
                                    });
                                    confirmPassword.showWithEditText(
                                            getString(R.string.confirm_password),
                                            getString(R.string.forgott_password_warning),
                                            getString(R.string.cancel),
                                            getString(R.string.save),
                                            true,
                                            "",
                                            getString(R.string.confirm_password_hint)
                                    );
                                }
                            });
                            newPassword.showWithEditText(
                                    getString(R.string.new_password),
                                    getString(R.string.forgott_password_warning),
                                    getString(R.string.cancel),
                                    getString(R.string.next),
                                    true,
                                    "",
                                    getString(R.string.enter_new_password_hint)
                            );

                        } else {
                            infoPassword.show(
                                    getString(R.string.error),
                                    getString(R.string.wrong_password_try_again),
                                    "",
                                    getString(R.string.ok)
                            );
                        }
                    }
                });
                askPassword.showWithEditText(
                        getString(R.string.confirm_password2),
                        getString(R.string.change_password_hint),
                        getString(R.string.cancel),
                        getString(R.string.next),
                        true,
                        "",
                        getString(R.string.enter_password)
                );
            }
        });

    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            finish();
        }
        return super.onKeyUp(keyCode, event);
    }
}
