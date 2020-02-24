package org.jellyfin.androidtv.browsing;

import android.os.Bundle;

import org.jellyfin.androidtv.R;
import org.jellyfin.androidtv.TvApp;
import org.jellyfin.androidtv.querying.QueryType;
import org.jellyfin.androidtv.querying.StdItemQuery;

import org.jellyfin.apiclient.interaction.Response;
import org.jellyfin.apiclient.model.dto.BaseItemDto;
import org.jellyfin.apiclient.model.entities.SortOrder;
import org.jellyfin.apiclient.model.querying.ItemFields;
import org.jellyfin.apiclient.model.querying.ItemSortBy;
import org.jellyfin.apiclient.model.querying.ItemsResult;
import org.jellyfin.apiclient.model.querying.SimilarItemsQuery;

public class SuggestedMoviesFragment extends EnhancedBrowseFragment {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        showViews = false;
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void setupQueries(final IRowLoader rowLoader) {
        StdItemQuery lastPlayed = new StdItemQuery();
        lastPlayed.setParentId(mFolder.getId());
        lastPlayed.setIncludeItemTypes(new String[]{"Movie"});
        lastPlayed.setUserId(TvApp.getApplication().getCurrentUser().getId());
        lastPlayed.setSortOrder(SortOrder.Descending);
        lastPlayed.setSortBy(new String[]{ItemSortBy.DatePlayed});
        lastPlayed.setLimit(8);
        lastPlayed.setRecursive(true);

        TvApp.getApplication().getApiClient().GetItemsAsync(lastPlayed, new Response<ItemsResult>() {
            @Override
            public void onResponse(ItemsResult response) {
                for (BaseItemDto item : response.getItems()) {
                    SimilarItemsQuery similar = new SimilarItemsQuery();
                    similar.setId(item.getId());
                    similar.setFields(new ItemFields[] {
                            ItemFields.PrimaryImageAspectRatio,
                            ItemFields.Overview,
                            ItemFields.ChildCount
                    });
                    similar.setLimit(7);
                    mRows.add(new BrowseRowDef(mApplication.getString(R.string.lbl_because_you_watched)+item.getName(), similar, QueryType.SimilarMovies));
                }

                rowLoader.loadRows(mRows);
            }
        });
    }
}
