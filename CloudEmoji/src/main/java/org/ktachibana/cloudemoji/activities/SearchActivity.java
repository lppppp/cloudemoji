package org.ktachibana.cloudemoji.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.SearchFinishedEvent;
import org.ktachibana.cloudemoji.events.SearchInitiatedEvent;
import org.ktachibana.cloudemoji.fragments.SearchResultFragment;
import org.ktachibana.cloudemoji.models.Category;
import org.ktachibana.cloudemoji.models.Entry;
import org.ktachibana.cloudemoji.models.Source;
import org.ktachibana.cloudemoji.utils.SourceInMemoryCache;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class SearchActivity extends BaseActivity {
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    private SourceInMemoryCache mCurrentSourceCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentSourceCache = getIntent().getExtras().getParcelable(MainActivity.CURRENT_SOURCE_CACHE_TAG);
        handleIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            // Handles search intent
            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                String query = intent.getStringExtra(SearchManager.QUERY);
                replaceMainContainer(SearchResultFragment.newInstance(query));
            }
        }
    }

    private void replaceMainContainer(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // Associate searchable configuration with the SearchView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView =
                    (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
        } else {

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onEvent(SearchInitiatedEvent e) {
        String searchQuery = e.getSearchQuery();
        List<Entry> results = new ArrayList<Entry>();

        // Traverse all sources
        List<Source> sources = mCurrentSourceCache.getAllValues();
        for (Source source : sources) {

            // Traverse all categories
            for (Category category : source.getCategories()) {

                // Traverse all entries
                for (Entry entry : category.getEntries()) {
                    String emoticon = entry.getEmoticon();
                    String description = entry.getDescription();

                    if (emoticon.contains(searchQuery) || description.contains(searchQuery)) {
                        results.add(entry);
                    }
                }
            }
        }

        // Search finished
        EventBus.getDefault().post(new SearchFinishedEvent(results));
    }
}
