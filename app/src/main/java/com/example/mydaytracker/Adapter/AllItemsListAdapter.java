package com.example.mydaytracker.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;

import com.example.mydaytracker.Fragment.AllTasksFragment;
import com.example.mydaytracker.Fragment.ShowListDetailFragment;
import com.example.mydaytracker.R;
import com.example.mydaytracker.model.ItemDetails;
import com.example.mydaytracker.util.UserApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.List;

public class AllItemsListAdapter extends ArrayAdapter<ItemDetails> {
    private Context context;
    private List<ItemDetails> itemDetailsList;
    int resource;

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public AllItemsListAdapter(@NonNull Context context, int resource, @NonNull List<ItemDetails> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.itemDetailsList = objects;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        final ItemDetails itemDetails = itemDetailsList.get(position);

        final TextView itemText;
        final CheckBox checkBox;
        final ImageButton deleteButton;
        CardView cardView;

        itemText = convertView.findViewById(R.id.itemname_textview);
        checkBox = convertView.findViewById(R.id.item_checkbox);
        deleteButton = convertView.findViewById(R.id.item_delete_button);
        cardView = convertView.findViewById(R.id.itemname_cardview);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FragmentActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.user_activity_fragment, new ShowListDetailFragment(context, itemDetails.getListName(), itemDetails.getListColor())).commit();
            }
        });

        itemText.setTextColor(itemDetails.getListColor());
        itemText.setText(itemDetails.getItem());
        if(itemDetails.isItemCompleted() == true)   {
            checkBox.setChecked(true);
            itemText.setTextColor(Color.GRAY);
            itemText.setPaintFlags(itemText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            checkBox.setButtonTintList(ColorStateList.valueOf(Color.GRAY));
            deleteButton.setVisibility(View.VISIBLE);
        }
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName = itemDetails.getItem();
                String listName = itemDetails.getListName();
                Date dateAdded = itemDetails.getDateAdded();
                DocumentReference query = db.collection("User").document(UserApi.getInstance().getUserId()).collection("Lists").document(itemName + listName + dateAdded);
                if (itemDetails.isItemCompleted() == false) {
                    itemDetails.setItemCompleted(true);
                    query.set(itemDetails, SetOptions.merge())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    itemText.setTextColor(Color.GRAY);
                                    itemText.setPaintFlags(itemText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                    checkBox.setButtonTintList(ColorStateList.valueOf(Color.GRAY));
                                    deleteButton.setVisibility(View.VISIBLE);
                                }
                            });
                }else  {
                    itemDetails.setItemCompleted(false);
                    query.set(itemDetails, SetOptions.merge())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    itemText.setTextColor(Color.BLACK);
                                    itemText.setPaintFlags(itemText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                                    deleteButton.setVisibility(View.INVISIBLE);
                                }
                            });
                }
            }
        });

        final View finalConvertView = convertView;
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference query = db.collection("User").document(UserApi.getInstance().getUserId()).collection("Lists").document(itemDetails.getItem() + itemDetails.getListName() + itemDetails.getDateAdded());
                query.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ((FragmentActivity) finalConvertView.getContext()).getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.user_activity_fragment, new AllTasksFragment(UserApi.getInstance().getUserId(), context)).commit();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });
        return  convertView;
    }
}
