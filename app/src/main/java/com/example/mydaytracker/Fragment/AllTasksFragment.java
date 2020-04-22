package com.example.mydaytracker.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mydaytracker.R;
import com.example.mydaytracker.Adapter.AllItemsListAdapter;
import com.example.mydaytracker.model.ItemDetails;
import com.example.mydaytracker.util.UserApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllTasksFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<ItemDetails> itemDetailsList;

    String userId;
    Context context;

    private TextView allTaskTextView;
    private ListView allTaskListView;

    public AllTasksFragment(String uid, Context context) {
            this.userId = uid;
            this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        itemDetailsList = new ArrayList<>();
        getAllTasks();

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_tasks, container, false);

         allTaskTextView = view.findViewById(R.id.all_task_textview);
         allTaskListView = view.findViewById(R.id.all_task_listview);


        return view;
    }

    private void getAllTasks() {
        Query query = db.collection("User").document(UserApi.getInstance().getUserId()).collection("Lists");
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    private void showList() {
        AllItemsListAdapter adapter = new AllItemsListAdapter(context, R.layout.itemlistview, itemDetailsList);
        allTaskListView.setAdapter(adapter);

    }
}
