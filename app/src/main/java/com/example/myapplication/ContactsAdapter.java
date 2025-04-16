package com.example.myapplication;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    public interface ContactClickListener {
        void onClick(Uri contactUri);
    }

    private Cursor cursor;
    private final ContactClickListener clickListener;

    public ContactsAdapter(ContactClickListener listener) {
        this.clickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView textView;
        private long rowID;

        public ViewHolder(View view, ContactClickListener listener) {
            super(view);
            textView = view.findViewById(android.R.id.text1);
            view.setOnClickListener(v -> listener.onClick(DatabaseDescription.Contact.buildContactUri(rowID)));
        }

        public void bindRowId(long id) {
            this.rowID = id;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseDescription.Contact._ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_NAME));
        holder.bindRowId(id);
        holder.textView.setText(name);
    }

    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    public void swapCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }
}

