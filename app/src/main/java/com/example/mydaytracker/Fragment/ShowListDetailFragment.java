package com.example.mydaytracker.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.mydaytracker.R;
import com.example.mydaytracker.Adapter.ItemListAdapter;
import com.example.mydaytracker.model.ItemDetails;
import com.example.mydaytracker.util.UserApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShowListDetailFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ShowListDetailFragment";
    Context context;
    String userId;
    String listName;
    int listColor;

    //fragment instances
    private TextView listNameTextView;
    private Button addItemButton;
    private ListView itemListView;
    private EditText addItemText;
    private ImageButton addItemImageButton;

    //FireStore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Popup
    AlertDialog.Builder builder;
    AlertDialog dialog;

    //ArrayList
    List<ItemDetails> itemDetailsList;


    public  ShowListDetailFragment(Context context, String listName, int listColor) {
        this.context = context;
        this.listName = listName;
        this.listColor = listColor;
    }

    @Override
    public void onStart() {
        super.onStart();
        itemDetailsList = new ArrayList<>();
        getItems();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_showlistdetailfragment, container, false);
        if(UserApi.getInstance().getUserId() != null)   {
            userId = UserApi.getInstance().getUserId();
        }

        listNameTextView = view.findViewById(R.id.showdetaillist_listname_textview);
        addItemButton = view.findViewById(R.id.showdetaillist_additem_button);
        itemListView = view.findViewById(R.id.showdetaillist_item_listview);
//


        listNameTextView.setTextColor(listColor);
        listNameTextView.setText(listName + " List");

        addItemButton.setOnClickListener(this);

//        addItemImageButton.setOnClickListener(this);


//        Log.d(TAG, "onCreateView: " + userId + "   " + listName);
        return view;
    }

    private void getItems() {
        Query query = db.collection("User").document(userId).collection("Lists");
        query.whereEqualTo("listName", listName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())    {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        ItemDetails itemDetails = snapshot.toObject(ItemDetails.class);
                        if (itemDetails.getItem() != null)  {
                            itemDetailsList.add(itemDetails);
                        }else {
                            continue;
                        }
                    }
                    showList();
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())   {
            case R.id.showdetaillist_additem_button :
                addItemPopup();
                break;

//            case R.id.showdetaillist_add_item_button :
//                addItem();
//                break;
        }
    }

    private void addItem() {
        if (!TextUtils.isEmpty(addItemText.getText().toString().trim()))   {
            final ItemDetails itemDetails = new ItemDetails();
            itemDetails.setItem(addItemText.getText().toString().trim());
            itemDetails.setListName(listName);
            itemDetails.setDateAdded(new Date());
            itemDetails.setItemCompleted(false);
            itemDetails.setListColor(listColor);
            db.collection("User").document(userId).collection("Lists").document(itemDetails.getItem() + itemDetails.getListName() + itemDetails.getDateAdded())
                    .set(itemDetails)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            itemDetailsList.add(itemDetails);
                            ((FragmentActivity) context).getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.user_activity_fragment, new ShowListDetailFragment(context, itemDetails.getListName(), itemDetails.getListColor())).commit();
                            showList();
                            dialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }else   {
            Toast.makeText(context, "Please enter an item", Toast.LENGTH_SHORT).show();
        }
    }

    private void addItemPopup() {
        builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.popup_add_item, null);
        final EditText addItemEditText = view.findViewById(R.id.additempopup_additem_edittext);
        ImageButton addItemButton = view.findViewById(R.id.additempopup_additem_button);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(addItemEditText.getText().toString()))    {
                    final ItemDetails itemDetails = new ItemDetails();
                    itemDetails.setItem(addItemEditText.getText().toString().trim());
                    itemDetails.setListName(listName);
                    itemDetails.setDateAdded(new Date());
                    itemDetails.setItemCompleted(false);
                    itemDetails.setListColor(listColor);
                    db.collection("User").document(userId).collection("Lists").document(itemDetails.getItem() + itemDetails.getListName() + itemDetails.getDateAdded())
                            .set(itemDetails)
                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   dialog.dismiss();
                                   itemDetailsList.add(itemDetails);
                                   ((FragmentActivity) context).getSupportFragmentManager()
                                           .beginTransaction()
                                           .replace(R.id.user_activity_fragment, new ShowListDetailFragment(context, itemDetails.getListName(), itemDetails.getListColor())).commit();
                                   showList();
                               }
                           })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }else {
                    Toast.makeText(context, "Please add an Item", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }


    private void showList() {
        final ItemListAdapter adapter = new ItemListAdapter(context, R.layout.itemlistview, (ArrayList<ItemDetails>) itemDetailsList);
        adapter.notifyDataSetChanged();
        itemListView.setAdapter(adapter);

    }

}
