package com.example.mydaytracker.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;

import com.example.mydaytracker.Fragment.MyListFragment;
import com.example.mydaytracker.Fragment.ShowListDetailFragment;
import com.example.mydaytracker.R;
import com.example.mydaytracker.model.ListType;
import com.example.mydaytracker.util.UserApi;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyListAdapter extends ArrayAdapter<ListType> {
    private static final String TAG = "personalListAdapter";
    private Context context;
    int resource;
    List<ListType> list;
    String listNameString;
    int[] color;

    AlertDialog.Builder builder;
    AlertDialog dialog;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MyListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ListType> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.list = objects;
        color = new int[1];
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(resource, parent, false);

        final ListType listType = list.get(position);
        listNameString = listType.getListName();
        color[0] = listType.getListColor();

        final TextView listName, listSize, listFinishedSize;
        CardView cardView;
        ImageButton editButton, deleteButton;

        listName = convertView.findViewById(R.id.mylist_list_name_textview);
        listSize = convertView.findViewById(R.id.mylist_list_size_textview);
        listFinishedSize = convertView.findViewById(R.id.mylist_list_finished_size_textview);
        cardView = convertView.findViewById(R.id.mylist_cardview_recycleview);
        editButton = convertView.findViewById(R.id.my_list_update_button);
        deleteButton = convertView.findViewById(R.id.my_list__delete_button);
        listName.setTextColor(listType.getListColor());

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FragmentActivity) view.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.user_activity_fragment, new ShowListDetailFragment(context, listType.getListName(), listType.getListColor())).commit();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + position);
                updateList(listType);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteList(listType);
            }
        });
        listName.setText(listType.getListName() + " List");
        listSize.setText("Items on the list : " + listType.getListSize());
        listFinishedSize.setText("Finished Items : " + listType.getListFinishedSize());

//        addItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addItemPopup();
//            }
//        });
        return convertView;
    }


    private void updateList(final ListType listType) {
        builder = new AlertDialog.Builder(context);
        Log.d(TAG, "updateList: " + listType.getListName());
        View view = LayoutInflater.from(context).inflate(R.layout.create_list_popup, null);
        final EditText getItemName = view.findViewById(R.id.create_list_popup_edittext);
        ImageButton updateListButton = view.findViewById(R.id.create_list_popup_button);
        Button addColorButton = view.findViewById(R.id.create_list_setcolor);
        getItemName.setText(listType.getListName());
        getItemName.setTextColor(listType.getListColor());
        addColorButton.setText("Update List Color");
        addColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialogBuilder.with(context).setTitle("Choose List Color")
                        .initialColor(getItemName.getCurrentTextColor())
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                color[0] = selectedColor;
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int lastSelectedColor, Integer[] allColors) {
                                getItemName.setTextColor(color[0]);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .build()
                        .show();
            }

        });
        updateListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName = getItemName.getText().toString().trim();
                int color = getItemName.getCurrentTextColor();
                updateBatch(itemName, color, listType);

            }
        });
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private void updateBatch(final String newItemName, final int newColor, ListType listType) {
        Query query = db.collection("User").document(UserApi.getInstance().getUserId()).collection("Lists");
        query.whereEqualTo("listName", listType.getListName())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())    {
                            dialog.dismiss();
                            WriteBatch batch = db.batch();
                            List<DocumentSnapshot> snapshotList = Objects.requireNonNull(task.getResult()).getDocuments();
                            for (DocumentSnapshot snapshot : snapshotList)  {
                                batch.update(snapshot.getReference(), "listName", newItemName);
                                batch.update(snapshot.getReference(), "listColor", newColor);
                            }
                            batch.commit()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())    {
                                                Log.d(TAG, "onComplete: " + "update successful");
                                            }
                                        }
                                    });
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ((FragmentActivity) context).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.user_activity_fragment, new MyListFragment(UserApi.getInstance().getUserId(), context)).commit();
                    }
                });


    }

    private void deleteList(final ListType listType) {
        builder = new AlertDialog.Builder(context);
        Log.d(TAG, "deleteList: " + listNameString);
        View view = LayoutInflater.from(context).inflate(R.layout.delete_list_popup, null);
        final TextView deleteText = view.findViewById(R.id.delete_list_popup_textview);
        final Button noDeleteButton = view.findViewById(R.id.delete_list_popup_no_button);
        final Button yesDeleteButton = view.findViewById(R.id.delete_list_popup_yes_button);
        final ProgressBar progressBar = view.findViewById(R.id.delete_list_progressbar);

        noDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        yesDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Query query = db.collection("User").document(UserApi.getInstance().getUserId()).collection("Lists");
                query.whereEqualTo("listName", listType.getListName())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful())    {
                                    WriteBatch batch = db.batch();
                                    List<DocumentSnapshot> snapshotList = Objects.requireNonNull(task.getResult()).getDocuments();
                                    for (DocumentSnapshot snapshot : snapshotList)  {
                                        batch.delete(snapshot.getReference());
                                    }
                                    batch.commit()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())    {
                                                        Log.d(TAG, "onComplete: " + "deleted successfully");


                                                    }
                                                }
                                            });
                                }else   {
                                    Toast.makeText(context, "Error. Could not delete list", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ((FragmentActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.user_activity_fragment, new MyListFragment(UserApi.getInstance().getUserId(), context)).commit();
            }
        });



        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
