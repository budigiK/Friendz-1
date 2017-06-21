package com.friendz.friendz.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.friendz.friendz.R;
import com.friendz.friendz.adapters.FeedAdapter;
import com.friendz.friendz.db.Posts;
import com.friendz.friendz.db.PostsDataItem;
import com.friendz.friendz.model.PostResponse;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    @BindView(R.id.listFeeds)
    ListView listFeeds;
    Unbinder unbinder;

    public HomeFragment() {
        // Required empty public constructor
    }

    String queries = "id, application, call_to_action, caption, created_time, description, feed_targeting, from, icon,   instagram_eligibility, is_hidden, is_instagram_eligible, is_published, link, message, message_tags, name, object_id, parent_id, permalink_url, picture, place, privacy, properties, shares, source, status_type, story, story_tags, targeting, to, type, updated_time, with_tags";

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/feed?fields="+queries,
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(final GraphResponse response) {

                        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.createObjectFromJson(Posts.class,response.getRawResponse());

                            }
                        });
                       RealmResults<PostsDataItem> dataItems= Realm.getDefaultInstance().where(PostsDataItem.class).findAll();
//                        PostResponse postResponse=new Gson().fromJson(response.getRawResponse(),PostResponse.class);
                        FeedAdapter adapter=new FeedAdapter(getActivity(),(dataItems));
                        listFeeds.setAdapter(adapter);
            /* handle the result */
                    }
                }
        ).executeAsync();
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends?fields=id,name, birthday",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(final GraphResponse response) {

                        System.out.println(response);
            /* handle the result */
                    }
                }
        ).executeAsync();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnItemClick(R.id.listFeeds)
    public void onItemClicked() {
    }
}
