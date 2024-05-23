package com.nenofetch.praktikumtujuh;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    private BiometricPrompt.AuthenticationCallback authenticationCallback;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void
    onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authenticationCallback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(
                    int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                notifyUser("Autentikasi Error : " + errString);
            }


            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                // pindah intent apabila autentikasi berhasil
                startActivity(new Intent(MainActivity.this, SecretMessage.class));

            }
        };

        checkBiometricSupport();

        findViewById(R.id.start_authentication).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                // verifikasi fingerprint sensor
                BiometricPrompt biometricPrompt = new BiometricPrompt
                        .Builder(getApplicationContext())
                        .setTitle("Verifikasi Pengguna")
                        .setDescription("Silahkan verifikasi terlebih dahulu sebelum mengetahui pesan rahasia dari aplikasi ini")
                        .setNegativeButton("Batalkan", getMainExecutor(), new DialogInterface.OnClickListener() {
                            @Override
                            public void
                            onClick(DialogInterface dialogInterface, int i) {
                                notifyUser("Verifikasi dibatalkan");
                            }
                        }).build();

                biometricPrompt.authenticate(
                        getCancellationSignal(),
                        getMainExecutor(),
                        authenticationCallback);
            }
        });
    }


    private CancellationSignal getCancellationSignal() {

        CancellationSignal cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(
                new CancellationSignal.OnCancelListener() {
                    @Override
                    public void onCancel() {
                        notifyUser("Verifikasi dibatalkan oleh pengguna");
                    }
                });
        return cancellationSignal;
    }

    // fungsi untuk memeriksa apakah perangkat sudah mengaktifkan fitur fingerprint atau tidaknya
    private void checkBiometricSupport() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (!keyguardManager.isDeviceSecure()) {
            notifyUser("Fitur kunci fingerprint belum diatur di pengaturan handphone!");
            return;
        }
        getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT);
    }

    // method toast agar reusable
    private void notifyUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
