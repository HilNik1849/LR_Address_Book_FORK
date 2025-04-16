package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface DetailFragmentListener {
        void onContactDeleted();
        void onEditContact(Uri contactUri);
    }

    private static final int CONTACT_LOADER = 1;

    private DetailFragmentListener listener;
    private Uri contactUri;

    private TextView nameTextView;
    private TextView phoneTextView;
    private TextView emailTextView;
    private TextView streetTextView;
    private TextView cityTextView;
    private TextView stateTextView;
    private TextView zipTextView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (DetailFragmentListener) context;
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
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle args = getArguments();
        if (args != null) contactUri = args.getParcelable(MainActivity.CONTACT_URI);

        nameTextView = view.findViewById(R.id.nameTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        streetTextView = view.findViewById(R.id.streetTextView);
        cityTextView = view.findViewById(R.id.cityTextView);
        stateTextView = view.findViewById(R.id.stateTextView);
        zipTextView = view.findViewById(R.id.zipTextView);

        LoaderManager.getInstance(this).initLoader(CONTACT_LOADER, null, this);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_edit) {
            listener.onEditContact(contactUri);
            return true;
        } else if (itemId == R.id.action_delete) {
            deleteContact();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteContact() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirm_title);
        builder.setMessage(R.string.confirm_message);

        builder.setPositiveButton(R.string.button_delete,
                (dialog, id) -> getActivity().getContentResolver().delete(contactUri, null, null));

        builder.setNegativeButton(R.string.button_cancel, null);
        builder.setOnDismissListener(dialog -> listener.onContactDeleted());
        builder.show();
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
            nameTextView.setText(data.getString(data.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_NAME)));
            phoneTextView.setText(data.getString(data.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_PHONE)));
            emailTextView.setText(data.getString(data.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_EMAIL)));
            streetTextView.setText(data.getString(data.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_STREET)));
            cityTextView.setText(data.getString(data.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_CITY)));
            stateTextView.setText(data.getString(data.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_STATE)));
            zipTextView.setText(data.getString(data.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_ZIP)));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {}
}
