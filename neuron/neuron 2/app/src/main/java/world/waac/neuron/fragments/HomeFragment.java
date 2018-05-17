package world.waac.neuron.fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.waac.neuron.R;
import world.waac.neuron.activities.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class HomeFragment extends Fragment {


    private final MainActivity mainActivity;
    @BindView(R.id.floating_search_view)
    FloatingSearchView searchView;

    public HomeFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public static HomeFragment newInstance(MainActivity mainActivity) {
        HomeFragment fragment = new HomeFragment(mainActivity);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // do all initial stuff here.


        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                if ("".equals(currentQuery)) {
                    return;
                }
                doSearch(currentQuery);
            }
        });

    }

    private void doSearch(String query) {

        this.mainActivity.onSearch(query);


    }
}
