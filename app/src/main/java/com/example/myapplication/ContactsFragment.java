package com.example.myapplication;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;

import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ContactsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface ContactsFragmentListener {
        void onContactSelected(Uri contactUri);
        void onAddContact();
    }

    private static final int CONTACTS_LOADER = 0;

    private ContactsFragmentListener listener;
    private ContactsAdapter contactsAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (ContactsFragmentListener) context;
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
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new ItemDivider(getActivity()));

        contactsAdapter = new ContactsAdapter(new ContactsAdapter.ContactClickListener() {
            @Override
            public void onClick(Uri contactUri) {
                listener.onContactSelected(contactUri);
            }
        });
        recyclerView.setAdapter(contactsAdapter);

        FloatingActionButton addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> listener.onAddContact());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoaderManager.getInstance(this).initLoader(CONTACTS_LOADER, null, this);
    }

    public void updateContactList() {
        contactsAdapter.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == CONTACTS_LOADER) {
            return new CursorLoader(
                    getActivity(),
                    DatabaseDescription.Contact.CONTENT_URI,
                    null, null, null,
                    DatabaseDescription.Contact.COLUMN_NAME + " COLLATE NOCASE ASC"
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        contactsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        contactsAdapter.swapCursor(null);
    }
}