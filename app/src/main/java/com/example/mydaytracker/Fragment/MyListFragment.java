package com.example.mydaytracker.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.example.mydaytracker.Adapter.MyListAdapter;
import com.example.mydaytracker.model.ItemDetails;
import com.example.mydaytracker.model.ListType;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyListFragment extends Fragment {

    private static final String TAG = "mylist";
    Context context;
    String userId;
    final int[] color = new int[1];

    //Popup elements
    AlertDialog.Builder builder;
    AlertDialog dialog;

    //ListView
    private ListView listView;

    //Instances
    List<String> myListName;
    Map<String, Integer> listSize;
    List<ListType> listTypeList;

    //Layout fields
    TextView addListTextView;
    EditText enterList;

    TextView noListText;

    //FireStore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MyListFragment(String userId, Context context) {
        this.context = context;
        this.userId = userId;
    }

    @Override
    public void onStart() {
        super.onStart();
        myListName = new ArrayList<>();
        listTypeList = new ArrayList<>();
       getOnStartPageNumberofLists();
    }

    private void getOnStartPageNumberofLists() {
        Query query = db.collection("User").document(userId).collection("Lists");
        query.orderBy("listName").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() == 0)    {
                        noListText.setVisibility(View.VISIBLE);
                    }
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        ItemDetails itemDetails = snapshot.toObject(ItemDetails.class);
                        ListType type = snapshot.toObject(ListType.class);
                        String listName = type.getListName();
                        if (myListName.contains(listName)) {
                            for (int i = 0; i < listTypeList.size(); i++) {
                                ListType lt = listTypeList.get(i);
                                if (listName.equalsIgnoreCase(lt.getListName())) {
                                    lt.setListSize(lt.getListSize() + 1);
                                    if (itemDetails.isItemCompleted()) {
                                        lt.setListFinishedSize(lt.getListFinishedSize() + 1);
                                    }
                                    listTypeList.set(i, lt);
                                }
                            }
                        } else {
                            myListName.add(listName);
                            if (itemDetails.isItemCompleted())  {
                                type.setListFinishedSize(1);
                            }
                            listTypeList.add(type);
                        }
                        if (listTypeList.size() == 0)   {
                            noListText.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.INVISIBLE);
                        }
                        storeInfoinList();
                    }
                }
            }
        });

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mylists, container, false);
        listView = view.findViewById(R.id.my_list_listView);
        color[0] = Color.BLACK;
        noListText = view.findViewById(R.id.my_list_no_list_textview);


        Log.d(TAG, "addList: " + color[0]);
//        enterList = view.findViewById(R.id.my_list_enter_item_edittext);

        FloatingActionButton fab = view.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addList();

            }
        });

        return view;
    }

    private void addList() {
        builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.create_list_popup, null);
        final EditText getItemName = view.findViewById(R.id.create_list_popup_edittext);
        ImageButton createListButton = view.findViewById(R.id.create_list_popup_button);
        Button addColorButton = view.findViewById(R.id.create_list_setcolor);
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


        createListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(getItemName.getText().toString())) {
                    final String listName = getItemName.getText().toString().trim();
                    Map<String, Object> map = new HashMap<>();
                    map.put("listName", listName);
                    map.put("listColor", color[0]);

                    if (myListName.contains(listName)) {
                        Toast.makeText(context, "You already contain this List", Toast.LENGTH_SHORT).show();
                    } else {
                    db.collection("User").document(userId).collection("Lists").document(listName)
                            .set(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    ListType type = new ListType();
                                    type.setListName(listName);
                                    type.setListSize(0);
                                    type.setListFinishedSize(0);
                                    type.setListColor(color[0]);
                                    listTypeList.add(type);
                                    storeInfoinList();
                                    noListText.setVisibility(View.INVISIBLE);
                                    dialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                }
            }else {
                    Toast.makeText(context, "Please enter List name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setView(view);
        dialog = builder.create();
        dialog.show();

    }

//    private void getListDetails() {
//        Query query = db.collection("User").document(userId).collection("Lists")
//                .
//    }

//    private void getNumberofLists() {
//        Query query = db.collection("User").document(userId).collection("Lists");
//        query.orderBy("listName", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()) {
//                    for(QueryDocumentSnapshot snapshot : task.getResult())  {
//                        String listName = snapshot.getString("listName");
//                        if(listInfo.containsKey(listName))    {
//                            continue;
//                        }else   {
//                            assert listName != null;
//                            listInfo.put(listName, 0);
//                        }
//                        Log.d(TAG, "listsize: " + listInfo.size());
//                    }
//                    storeInfoinList();
//                }
//            }
//        });
//    }
//
//
//    private void getOnStartPageNumberofLists() {
//        Query query = db.collection("User").document(userId).collection("Lists");
//        query.orderBy("listName", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()) {
//                    for(QueryDocumentSnapshot snapshot : task.getResult())  {
//                        String listName = snapshot.getString("listName");
//                        ListType type = snapshot.toObject(ListType.class);
//                        if(listTypeList.contains(type))    {
////                            listInfo.replace(String.valueOf(listName), listInfo.get(listName), listInfo.get(listName) + 1);
//                            type.setListSize(type.getListSize() + 1);
//                        }else   {
//                           type.setListName(listName);
//
//                        }
//                        Log.d(TAG, "listsize: " + listInfo.size());
//                    }
//                    storeInfoinList();
//                }
//            }
//        });
//    }
//
    private void storeInfoinList() {
//        final List<ListType> list = new ArrayList<>();
//        for(Map.Entry hashmap : listInfo.entrySet())    {
//            ListType type = new ListType();
//            type.setListName(String.valueOf(hashmap.getKey()));
//            type.setListSize(String.valueOf(hashmap.getValue()));
//            Log.d(TAG, "storeInfoinList: " + type.getListColor());
//            list.add(type);
//        }

        for(ListType type : listTypeList)   {
            Log.d(TAG, "storeInfoinList: " + type.getListName() + type.getListColor());
        }


            listView.setVisibility(View.VISIBLE);
            MyListAdapter adapter = new MyListAdapter(context, R.layout.recyclerview_mylists, (ArrayList<ListType>) listTypeList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ListType type = listTypeList.get(i);
                    ((FragmentActivity) view.getContext()).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.user_activity_fragment, new ShowListDetailFragment(view.getContext(), type.getListName(), type.getListColor())).commit();
                }
            });

//
    }


//
}
