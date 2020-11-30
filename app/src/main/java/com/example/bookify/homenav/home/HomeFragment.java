package com.example.bookify.homenav.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookify.R;
import com.example.bookify.Book;
import com.example.bookify.homenav.home.models.VerticalModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    RecyclerView verticalRecyclerView;
    VerticalRecyclerViewAdapter adapter;
    ArrayList<VerticalModel> arrayListVertical;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //
        //homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
        //   @Override
        //    public void onChanged(@Nullable String s) {
        //        textView.setText(s);
        //    }
        //});

        arrayListVertical = new ArrayList<>();

        verticalRecyclerView = root.findViewById(R.id.recycleviewhome);
        verticalRecyclerView.setHasFixedSize(true);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));

        adapter = new VerticalRecyclerViewAdapter(root.getContext(), arrayListVertical);
        verticalRecyclerView.setAdapter(adapter);

        setData();
        // Set data for Recycle View



        return root;
    }

    private void setData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("books");
        // Path reference to books database
        Query reference1 = FirebaseDatabase.getInstance().getReference("books").orderByChild("category").equalTo("Category1");
        // Path reference to books database, but uses query for filtering
        Query reference2 = FirebaseDatabase.getInstance().getReference("books").orderByChild("category").equalTo("Category2");


        //Add books to Recycle View

        //Horizontal Recycle View 1 (Recommended)
        VerticalModel verticalModel = new VerticalModel();
        verticalModel.setTitle("ALL");
        final ArrayList<Book> arrayListHorizontal = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Book book;
                for (DataSnapshot snapshot1 : snapshot.getChildren() ){
                    book = snapshot1.getValue(Book.class);
                    arrayListHorizontal.add(book);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
        verticalModel.setArrayList(arrayListHorizontal);
        arrayListVertical.add(verticalModel);

        //Horizontal Recycle View 2 (Category 1)
        verticalModel = new VerticalModel();
        verticalModel.setTitle("Category 1");
        final ArrayList<Book> arrayListHorizontal1 = new ArrayList<>();
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Book book;
                for (DataSnapshot snapshot1 : snapshot.getChildren() ){
                    book = snapshot1.getValue(Book.class);
                    arrayListHorizontal1.add(book);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
        verticalModel.setArrayList(arrayListHorizontal1);
        arrayListVertical.add(verticalModel);

        //Horizontal Recycle View 3 (Category 2)
        verticalModel = new VerticalModel();
        verticalModel.setTitle("Category 2");
        final ArrayList<Book> arrayListHorizontal2 = new ArrayList<>();
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Book book;
                for (DataSnapshot snapshot1 : snapshot.getChildren() ){
                    book = snapshot1.getValue(Book.class);
                    arrayListHorizontal2.add(book);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
        verticalModel.setArrayList(arrayListHorizontal2);
        arrayListVertical.add(verticalModel);

        // End of Recycle View





    }
}