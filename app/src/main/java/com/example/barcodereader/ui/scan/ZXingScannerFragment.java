package com.example.barcodereader.ui.scan;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.barcodereader.R;
import com.example.barcodereader.ResultActivity;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ZXingScannerFragment extends Fragment implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scanner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        scanner = new ZXingScannerView(getContext()) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                ViewFinderView vfv = new ViewFinderView(context);
                vfv.setBorderColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                vfv.setBorderAlpha(1);
                return vfv;
            }
        };

        scanner.setAspectTolerance(0.8f);

        Dexter.withContext(getContext())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(getContext(), "CAMERA STARTED", Toast.LENGTH_LONG).show();
                        scanner.setResultHandler(ZXingScannerFragment.this);
                        scanner.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(getContext(), "CAMERA PERMISSION DENIED", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        Toast.makeText(getContext(), "onPermissionRationaleShouldBeShown", Toast.LENGTH_LONG).show();
                    }
                });

        return scanner;
    }

    /*ResultHandler resultHandler;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        resultHandler = (ResultHandler) context;
    }*/

    @Override
    public void onResume() {
        super.onResume();
        scanner.setResultHandler(this);
        scanner.startCamera();
    }

    @Override
    public void onDestroy() {
        scanner.stopCamera();
        super.onDestroy();
    }

    @Override
    public void handleResult(Result rawResult) {
        Toast.makeText(getContext(), rawResult.getText() + " - " + rawResult.getBarcodeFormat(), Toast.LENGTH_LONG).show();
        //resultHandler.onValueFind(rawResult);

        getActivity().finish();
        Intent resultActivity = new Intent(getContext(), ResultActivity.class);
        resultActivity.putExtra("result", rawResult.getText());
        resultActivity.putExtra("type", rawResult.getBarcodeFormat().toString());
        startActivity(resultActivity);
    }

    /*public interface ResultHandler {
        public void onValueFind(Result result);
    }*/
}