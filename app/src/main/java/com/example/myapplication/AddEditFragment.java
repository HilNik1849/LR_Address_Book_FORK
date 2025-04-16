package com.example.myapplication;
import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddEditFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface AddEditFragmentListener {
        void onAddEditCompleted(Uri contactUri);
    }

    private static final int CONTACT_LOADER = 2;

    private AddEditFragmentListener listener;
    private Uri contactUri;
    private boolean addingNewContact = true;

    private TextInputLayout nameLayout, phoneLayout, emailLayout, streetLayout, cityLayout, stateLayout, zipLayout;
    private TextInputEditText nameEditText, phoneEditText, emailEditText, streetEditText, cityEditText, stateEditText, zipEditText;
    private FloatingActionButton saveButton;

    @Override
    public void onAttach(@NonNull android.content.Context context) {
        super.onAttach(context);
        listener = (AddEditFragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);


        nameLayout = view.findViewById(R.id.nameTextInputLayout);
        phoneLayout = view.findViewById(R.id.phoneTextInputLayout);
        emailLayout = view.findViewById(R.id.emailTextInputLayout);
        streetLayout = view.findViewById(R.id.streetTextInputLayout);
        cityLayout = view.findViewById(R.id.cityTextInputLayout);
        stateLayout = view.findViewById(R.id.stateTextInputLayout);
        zipLayout = view.findViewById(R.id.zipTextInputLayout);


        nameEditText = view.findViewById(R.id.nameEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        streetEditText = view.findViewById(R.id.streetEditText);
        cityEditText = view.findViewById(R.id.cityEditText);
        stateEditText = view.findViewById(R.id.stateEditText);
        zipEditText = view.findViewById(R.id.zipEditText);

        saveButton = view.findViewById(R.id.saveFloatingActionButton);
        saveButton.setOnClickListener(v -> saveContact());

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveButton.setVisibility(s.toString().trim().isEmpty() ? View.INVISIBLE : View.VISIBLE);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        Bundle args = getArguments();
        if (args != null) {
            addingNewContact = false;
            contactUri = args.getParcelable(MainActivity.CONTACT_URI);
            LoaderManager.getInstance(this).initLoader(CONTACT_LOADER, null, this);
        }

        return view;
    }

    private void saveContact() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        if (getView() != null) imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        ContentValues values = new ContentValues();
        values.put(DatabaseDescription.Contact.COLUMN_NAME, nameEditText.getText().toString());
        values.put(DatabaseDescription.Contact.COLUMN_PHONE, phoneEditText.getText().toString());
        values.put(DatabaseDescription.Contact.COLUMN_EMAIL, emailEditText.getText().toString());
        values.put(DatabaseDescription.Contact.COLUMN_STREET, streetEditText.getText().toString());
        values.put(DatabaseDescription.Contact.COLUMN_CITY, cityEditText.getText().toString());
        values.put(DatabaseDescription.Contact.COLUMN_STATE, stateEditText.getText().toString());
        values.put(DatabaseDescription.Contact.COLUMN_ZIP, zipEditText.getText().toString());

        if (addingNewContact) {
            Uri newContactUri = getActivity().getContentResolver().insert(
                    DatabaseDescription.Contact.CONTENT_URI, values);
            if (newContactUri != null) {
                listener.onAddEditCompleted(newContactUri);
                Toast.makeText(getActivity(), R.string.contact_added, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), R.string.contact_not_added, Toast.LENGTH_SHORT).show();
            }
        } else {
            int updatedRows = getActivity().getContentResolver().update(contactUri, values, null, null);
            if (updatedRows > 0) {
                listener.onAddEditCompleted(contactUri);
                Toast.makeText(getActivity(), R.string.contact_updated, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), R.string.contact_not_updated, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(
                getActivity(),
                contactUri,
                null, null, null, null
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            nameEditText.setText(data.getString(data.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_NAME)));
            phoneEditText.setText(data.getString(data.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_PHONE)));
            emailEditText.setText(data.getString(data.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_EMAIL)));
            streetEditText.setText(data.getString(data.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_STREET)));
            cityEditText.setText(data.getString(data.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_CITY)));
            stateEditText.setText(data.getString(data.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_STATE)));
            zipEditText.setText(data.getString(data.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_ZIP)));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {}
}
