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
import androidx.fragment.app.FragmentActivity;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemListAdapter extends ArrayAdapter<ItemDetails> {

    private static final String TAG = "ListViewAdapter";
    private Context context;
    int resource;
    List<ItemDetails> itemDetailsList;


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ItemListAdapter(@Nullable Context context, int resource, @NonNull ArrayList<ItemDetails> itemDetailsList)    {
        super(context, resource, itemDetailsList);
        this.context = context;
        this.resource = resource;
        this.itemDetailsList = itemDetailsList;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        final ItemDetails itemDetails = itemDetailsList.get(position);

        final TextView itemText;
        final CheckBox checkBox;
        final ImageButton imageButton;

        itemText = convertView.findViewById(R.id.itemname_textview);
        checkBox = convertView.findViewById(R.id.item_checkbox);
        imageButton = convertView.findViewById(R.id.item_delete_button);

        itemText.setText(itemDetails.getItem());
        if(itemDetails.isItemCompleted() == true)   {
            checkBox.setChecked(true);
            itemText.setTextColor(Color.GRAY);
            itemText.setPaintFlags(itemText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            checkBox.setButtonTintList(ColorStateList.valueOf(Color.GRAY));
            imageButton.setVisibility(View.VISIBLE);
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
                                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    itemText.setTextColor(Color.GRAY);
                                    itemText.setPaintFlags(itemText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                    checkBox.setButtonTintList(ColorStateList.valueOf(Color.GRAY));
                                    imageButton.setVisibility(View.VISIBLE);
                                }
                            });
                }else  {
                    itemDetails.setItemCompleted(false);
                    query.set(itemDetails, SetOptions.merge())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    itemText.setTextColor(Color.BLACK);
                                    imageButton.setVisibility(View.INVISIBLE);
                                    itemText.setPaintFlags(itemText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                                }
                            });
                }
            }
        });

        final View finalConvertView = convertView;
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference query = db.collection("User").document(UserApi.getInstance().getUserId()).collection("Lists").document(itemDetails.getItem() + itemDetails.getListName() + itemDetails.getDateAdded());
                query.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ((FragmentActivity) finalConvertView.getContext()).getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.user_activity_fragment, new ShowListDetailFragment(finalConvertView.getContext(), itemDetails.getListName(), itemDetails.getListColor())).commit();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });
        return convertView;
    }



}
